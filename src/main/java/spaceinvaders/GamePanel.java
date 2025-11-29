package spaceinvaders;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

/**
 * GamePanel - FULL SCREEN VERSION với dynamic scaling
 */
public class GamePanel extends JPanel implements ActionListener {
    // Dynamic screen dimensions
    public static int WIDTH;
    public static int HEIGHT;

    // Scale factor cho các đối tượng (public để các class khác truy cập)
    public static double SCALE_FACTOR = 1.0;

    private Timer gameTimer;
    private static final int FPS = 60;
    private static final int DELAY = 1000 / FPS;

    private GameState currentState;
    private InputHandler inputHandler;
    private AssetManager assetManager;

    private Player player;
    private List<Enemy> enemies;
    private List<Bullet> playerBullets;
    private List<Bullet> enemyBullets;
    private List<Explosion> explosions;
    private List<Item> items;
    private WaveManager waveManager;

    private int score;
    private int lives;
    private int level;
    private boolean gameRunning;

    private Font gameFont;
    private Font bigFont;

    private Button startButton;
    private Button muteButton;
    private Button restartButton;
    private int mouseX = 0;
    private int mouseY = 0;

    public GamePanel() {
        // Lấy kích thước màn hình
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = (int) screenSize.getWidth();
        HEIGHT = (int) screenSize.getHeight();

        // Tính scale factor (base: 800x600)
        SCALE_FACTOR = Math.min(WIDTH / 800.0, HEIGHT / 600.0);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        initializeGame();
        setupInput();
        setupMouse();
        setupButtons();
        setupTimer();
        setupFonts();
        gameTimer.start();
        requestFocusInWindow();
    }

    private void initializeGame() {
        currentState = GameState.START;
        inputHandler = new InputHandler();
        assetManager = AssetManager.getInstance();

        player = new Player(WIDTH / 2 - scaled(40), HEIGHT - scaled(130));
        enemies = new ArrayList<>();
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        explosions = new ArrayList<>();
        items = new ArrayList<>();
        waveManager = new WaveManager();

        score = 0;
        lives = 3;
        level = 1;
        gameRunning = false;
    }

    private void setupInput() {
        addKeyListener(inputHandler);
    }

    private void setupMouse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
    }

    private void setupButtons() {
        int buttonWidth = scaled(200);
        int buttonHeight = scaled(80);
        int startX = WIDTH / 2 - buttonWidth / 2;
        int startY = HEIGHT / 2 + scaled(50);

        BufferedImage startImg = assetManager.getStartButtonImage();
        if (startImg != null) {
            startButton = new Button(startX, startY, buttonWidth, buttonHeight, startImg);
        } else {
            startButton = new Button(startX, startY, buttonWidth, buttonHeight, "START");
        }

        int muteX = WIDTH / 2 - buttonWidth / 2;
        int muteY = startY + buttonHeight + scaled(20);

        BufferedImage muteImg = assetManager.getMuteButtonImage();
        if (muteImg != null) {
            muteButton = new Button(muteX, muteY, buttonWidth, buttonHeight, muteImg);
        } else {
            muteButton = new Button(muteX, muteY, buttonWidth, buttonHeight, "MUTE");
        }

        int restartX = WIDTH / 2 - buttonWidth / 2;
        int restartY = HEIGHT / 2 + scaled(20);

        BufferedImage restartImg = assetManager.getRestartButtonImage();
        if (restartImg != null) {
            restartButton = new Button(restartX, restartY, buttonWidth, buttonHeight, restartImg);
        } else {
            restartButton = new Button(restartX, restartY, buttonWidth, buttonHeight, "RESTART");
        }
    }

    private void handleMouseClick(int x, int y) {
        SoundManager soundMgr = assetManager.getSoundManager();

        if (currentState == GameState.START) {
            if (startButton.isClicked(x, y)) {
                soundMgr.playButtonSound();
                startGame();
                resetGame();
            }
            if (muteButton.isClicked(x, y)) {
                soundMgr.playButtonSound();
                soundMgr.toggleSound();
            }
        } else if (currentState == GameState.END) {
            if (restartButton.isClicked(x, y)) {
                soundMgr.playButtonSound();
                startGame();
                resetGame();
            }
            if (muteButton.isClicked(x, y)) {
                soundMgr.playButtonSound();
                soundMgr.toggleSound();
            }
        } else if (currentState == GameState.PLAYING) {
            if (muteButton.isClicked(x, y)) {
                soundMgr.playButtonSound();
                soundMgr.toggleSound();
            }
        }
    }

    private void setupTimer() {
        gameTimer = new Timer(DELAY, this);
    }

    private void setupFonts() {
        gameFont = new Font("Arial", Font.BOLD, scaled(16));
        bigFont = new Font("Arial", Font.BOLD, scaled(48));
    }

    public void startGame() {
        currentState = GameState.PLAYING;
        gameRunning = true;
        gameTimer.start();
        assetManager.getSoundManager().playBackgroundMusic();
        requestFocus();
    }

    public void stopGame() {
        gameRunning = false;
        gameTimer.stop();
    }

    public void resetGame() {
        score = 0;
        lives = 3;
        level = 1;
        playerBullets.clear();
        enemyBullets.clear();
        enemies.clear();
        explosions.clear();
        items.clear();
        waveManager = new WaveManager();
        player.reset();
        updateBackgroundForWave();
        spawnWave();
    }

    private void spawnWave() {
        enemies.clear();
        waveManager.spawnWave(enemies, WIDTH, HEIGHT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState == GameState.START) {
            startButton.update(mouseX, mouseY);
            muteButton.update(mouseX, mouseY);
        } else if (currentState == GameState.END) {
            restartButton.update(mouseX, mouseY);
            muteButton.update(mouseX, mouseY);
        } else if (currentState == GameState.PLAYING) {
            muteButton.update(mouseX, mouseY);
        }

        update();
        repaint();
    }

    private void update() {
        handleInput();

        if (currentState == GameState.PLAYING) {
            updateGameObjects();
            checkCollisions();
            checkWaveProgress();
        }

        inputHandler.update();
    }

    private void handleInput() {
        SoundManager soundMgr = assetManager.getSoundManager();

        if (currentState == GameState.START) {
            if (inputHandler.isKeyJustPressed(InputHandler.KEY_ENTER)) {
                soundMgr.playButtonSound();
                startGame();
                resetGame();
            }
        } else if (currentState == GameState.PLAYING) {
            if (inputHandler.isKeyPressed(InputHandler.KEY_LEFT)) {
                player.moveLeft();
            }
            if (inputHandler.isKeyPressed(InputHandler.KEY_RIGHT)) {
                player.moveRight();
            }
            if (inputHandler.isKeyPressed(InputHandler.KEY_UP)) {
                player.moveUp();
            }
            if (inputHandler.isKeyPressed(InputHandler.KEY_DOWN)) {
                player.moveDown();
            }

            if (inputHandler.isKeyPressed(InputHandler.KEY_SPACE)) {
                player.shoot(playerBullets);
            }

            if (inputHandler.isKeyJustPressed(InputHandler.KEY_P)) {
                currentState = GameState.PAUSED;
            }
        } else if (currentState == GameState.PAUSED) {
            if (inputHandler.isKeyJustPressed(InputHandler.KEY_P)) {
                currentState = GameState.PLAYING;
            }
        } else if (currentState == GameState.END) {
            if (inputHandler.isKeyJustPressed(InputHandler.KEY_ENTER)) {
                soundMgr.playButtonSound();
                startGame();
                resetGame();
            }
            if (inputHandler.isKeyJustPressed(InputHandler.KEY_ESCAPE)) {
                soundMgr.playButtonSound();
                currentState = GameState.START;
                soundMgr.stopBackgroundMusic();
            }
        }
    }

    private void updateGameObjects() {
        player.update();

        for (Enemy enemy : enemies) {
            enemy.update();
            enemy.shoot(enemyBullets);
        }

        updateBullets(playerBullets);
        updateBullets(enemyBullets);
        updateExplosions();
        updateItems();

        waveManager.checkAndSpawnBoss(enemies, WIDTH, HEIGHT);
    }

    private void updateExplosions() {
        Iterator<Explosion> iterator = explosions.iterator();
        while (iterator.hasNext()) {
            Explosion explosion = iterator.next();
            explosion.update();
            if (!explosion.isActive()) {
                iterator.remove();
            }
        }
    }

    private void updateItems() {
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            item.update();

            if (item.isActive() && item.getBounds().intersects(player.getBounds())) {
                collectItem(item);
                item.setActive(false);
            }

            if (!item.isActive()) {
                iterator.remove();
            }
        }
    }

    private void collectItem(Item item) {
        switch (item.getType()) {
            case HEALTH:
                if (lives < 5) {
                    lives++;
                }
                break;
            case POWERUP:
                score += 50;
                break;
            case SCORE:
                score += 100;
                break;
            case TRIPLE_SHOT:
                player.activateTripleShot();
                break;
            case PIERCING:
                player.activatePiercing();
                break;
            case SHIELD:
                player.activateShield();
                break;
        }
    }

    private void updateBullets(List<Bullet> bullets) {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update();
            if (!bullet.isActive()) {
                iterator.remove();
            }
        }
    }

    private void checkCollisions() {
        SoundManager soundMgr = assetManager.getSoundManager();

        Iterator<Bullet> bulletIterator = playerBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            if (bullet.getDirection() > 0) continue;

            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();

                if (enemy.isActive() && Collision.checkBulletEnemyCollision(bullet, enemy)) {
                    enemy.takeDamage(bullet.getDamage());

                    bullet.onHit();

                    if (!enemy.isActive()) {
                        explosions.add(new Explosion(
                                enemy.getX() + enemy.getWidth()/2,
                                enemy.getY() + enemy.getHeight()/2,
                                scaled(35)
                        ));
                        score += enemy.getScoreValue();
                        spawnPowerUpItem(enemy.getX() + enemy.getWidth()/2, enemy.getY());
                        soundMgr.playExplosionSound();
                    }

                    if (!bullet.isPiercing()) {
                        break;
                    }
                }
            }
        }

        bulletIterator = enemyBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            if (bullet.isActive() && Collision.checkEnemyBulletPlayerCollision(bullet, player)) {
                bullet.setActive(false);

                boolean tookDamage = player.takeDamage();

                if (tookDamage) {
                    soundMgr.playDeathSound();

                    lives--;
                    if (lives <= 0) {
                        currentState = GameState.END;
                        soundMgr.stopBackgroundMusic();
                        soundMgr.playGameOverSound();
                    }
                }
            }
        }

        Iterator<Enemy> enemyCollisionIterator = enemies.iterator();
        while (enemyCollisionIterator.hasNext()) {
            Enemy enemy = enemyCollisionIterator.next();

            if (enemy.isActive() && Collision.checkEnemyPlayerCollision(enemy, player)) {
                enemy.setActive(false);

                boolean tookDamage = player.takeDamage();

                explosions.add(new Explosion(
                        enemy.getX() + enemy.getWidth()/2,
                        enemy.getY() + enemy.getHeight()/2,
                        scaled(35)
                ));

                if (tookDamage) {
                    soundMgr.playDeathSound();

                    lives--;
                    if (lives <= 0) {
                        currentState = GameState.END;
                        soundMgr.stopBackgroundMusic();
                        soundMgr.playGameOverSound();
                    }
                }
            }
        }
    }

    private void spawnPowerUpItem(int x, int y) {
        java.util.Random random = new java.util.Random();
        double rand = random.nextDouble();

        if (rand < 0.3) {
            Item.ItemType type;
            double itemRand = random.nextDouble();

            if (itemRand < 0.25) {
                type = Item.ItemType.TRIPLE_SHOT;
            } else if (itemRand < 0.5) {
                type = Item.ItemType.PIERCING;
            } else if (itemRand < 0.75) {
                type = Item.ItemType.SHIELD;
            } else if (itemRand < 0.9) {
                type = Item.ItemType.HEALTH;
            } else {
                type = Item.ItemType.SCORE;
            }

            items.add(new Item(x, y, type));
        }
    }

    private void checkWaveProgress() {
        if (waveManager.isWaveComplete(enemies)) {
            waveManager.nextWave();
            level = waveManager.getCurrentWave();
            updateBackgroundForWave();
            spawnWave();
        }
    }

    private void updateBackgroundForWave() {
        int currentWave = waveManager.getCurrentWave();
        BufferedImage newBackground = assetManager.getBackgroundImage(currentWave);
        if (newBackground != null) {
            assetManager.setBackGroundImage(newBackground);
            System.out.println("[BACKGROUND] Đổi background cho wave " + currentWave);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (currentState) {
            case START:
                drawStartScreen(g2d);
                break;
            case MENU:
                drawMenu(g2d);
                break;
            case PLAYING:
            case PAUSED:
                drawGame(g2d);
                if (currentState == GameState.PAUSED) {
                    drawPauseScreen(g2d);
                }
                break;
            case END:
                drawEndScreen(g2d);
                break;
            case GAMEOVER:
                drawGameOver(g2d);
                break;
        }
    }

    private void drawStartScreen(Graphics2D g2d) {
        BufferedImage bgStart = assetManager.getBackGroundStartImage();
        if (bgStart != null) {
            g2d.drawImage(bgStart, 0, 0, WIDTH, HEIGHT, null);
        } else {
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 20, 50),
                    0, HEIGHT, new Color(50, 20, 80)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
        }

        BufferedImage titleImg = assetManager.getTitleImage();
        if (titleImg != null) {
            int titleWidth = scaled(700);
            int titleHeight = scaled(160);
            int x = (WIDTH - titleWidth) / 2;
            int y = scaled(180);
            g2d.drawImage(titleImg, x, y, titleWidth, titleHeight, null);
        } else {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, scaled(48)));
            String title = "SPACE INVADERS";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (WIDTH - fm.stringWidth(title)) / 2;
            int y = HEIGHT / 3;

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(title, x + 3, y + 3);

            g2d.setColor(new Color(255, 215, 0));
            g2d.drawString(title, x, y);
        }

        startButton.draw(g2d);
        muteButton.draw(g2d);

        g2d.setFont(new Font("Arial", Font.PLAIN, scaled(14)));
        g2d.setColor(Color.WHITE);
        String soundStatus = assetManager.getSoundManager().isSoundEnabled() ? "ON" : "OFF";
        FontMetrics fmSound = g2d.getFontMetrics();
        int soundX = WIDTH / 2 - fmSound.stringWidth(soundStatus) / 2;
        int soundY = HEIGHT / 2 + scaled(50) + scaled(80) + scaled(20) + scaled(60) + scaled(15);
        g2d.drawString(soundStatus, soundX, soundY);
    }

    private void drawEndScreen(Graphics2D g2d) {
        BufferedImage bgEnd = assetManager.getBackGroundEndImage();
        if (bgEnd != null) {
            g2d.drawImage(bgEnd, 0, 0, WIDTH, HEIGHT, null);
        } else {
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(10, 10, 20),
                    0, HEIGHT, new Color(40, 10, 10)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, scaled(56)));
        String scoreText = "Final Score: " + score;
        FontMetrics fm = g2d.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(scoreText)) / 2;
        int y = HEIGHT / 3 + scaled(70);
        g2d.drawString(scoreText, x, y);

        restartButton.draw(g2d);
    }

    private void drawMenu(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(bigFont);

        String title = "SPACE INVADERS";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(title)) / 2;
        int y = HEIGHT / 2 - scaled(50);
        g2d.drawString(title, x, y);
    }

    private void drawGame(Graphics2D g2d) {
        BufferedImage bg = assetManager.getBackGroundImage();
        if (bg != null) {
            g2d.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
        }

        player.draw(g2d);

        for (Enemy enemy : enemies) {
            enemy.draw(g2d);
        }

        for (Bullet bullet : playerBullets) {
            bullet.draw(g2d);
        }
        for (Bullet bullet : enemyBullets) {
            bullet.draw(g2d);
        }

        for (Explosion explosion : explosions) {
            explosion.draw(g2d);
        }

        for (Item item : items) {
            item.draw(g2d);
        }

        drawUI(g2d);
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(gameFont);

        g2d.drawString("Score: " + score, scaled(10), scaled(25));
        g2d.drawString("Lives: " + lives, scaled(10), scaled(50));
        g2d.drawString("Wave: " + waveManager.getCurrentWave(), scaled(10), scaled(75));
    }

    private void drawPauseScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.setColor(Color.WHITE);
        g2d.setFont(bigFont);
        String pauseText = "PAUSED";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(pauseText)) / 2;
        int y = HEIGHT / 2;
        g2d.drawString(pauseText, x, y);

        g2d.setFont(gameFont);
        String resumeText = "Press P to Resume";
        fm = g2d.getFontMetrics();
        x = (WIDTH - fm.stringWidth(resumeText)) / 2;
        y += scaled(50);
        g2d.drawString(resumeText, x, y);
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(bigFont);

        String scoreText = "Final Score: " + score;
        FontMetrics fm = g2d.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(scoreText)) / 2;
        int y = HEIGHT / 2;
        g2d.drawString(scoreText, x, y);
    }

    // Helper method để scale giá trị theo màn hình
    public static int scaled(int value) {
        return (int) (value * SCALE_FACTOR);
    }

    public static double scaledDouble(double value) {
        return value * SCALE_FACTOR;
    }
}