package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

/**
 * GamePanel - FIXED với đầy đủ sound effects
 */
public class GamePanel extends JPanel implements ActionListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Timer gameTimer;
    private static final int FPS = 60;
    private static final int DELAY = 1000 / FPS;

    private GameState currentState;
    private InputHandler inputHandler;

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

    // Buttons và Mouse tracking
    private Button startButton;
    private Button muteButton;
    private Button restartButton;
    private int mouseX = 0;
    private int mouseY = 0;

    public GamePanel() {
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

        player = new Player(WIDTH / 2 - 40, HEIGHT - 130);
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

        Assets.loadAssets();
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
        int buttonWidth = 200;
        int buttonHeight = 80;
        int startX = WIDTH / 2 - buttonWidth / 2;
        int startY = HEIGHT / 2 - 20;

        if (Assets.startButtonImage != null) {
            startButton = new Button(startX, startY, buttonWidth, buttonHeight, Assets.startButtonImage);
        } else {
            startButton = new Button(startX, startY, buttonWidth, buttonHeight, "START");
        }

        int muteSize = 60;
        int muteX = WIDTH - muteSize - 20;
        int muteY = 20;

        if (Assets.muteButtonImage != null) {
            muteButton = new Button(muteX, muteY, muteSize, muteSize, Assets.muteButtonImage);
        } else {
            muteButton = new Button(muteX, muteY, muteSize, muteSize, "MUTE");
        }

        int restartX = WIDTH / 2 - buttonWidth / 2;
        int restartY = HEIGHT / 2 + 20;

        if (Assets.restartButtonImage != null) {
            restartButton = new Button(restartX, restartY, buttonWidth, buttonHeight, Assets.restartButtonImage);
        } else {
            restartButton = new Button(restartX, restartY, buttonWidth, buttonHeight, "RESTART");
        }
    }

    /**
     * ⭐ FIXED: Thêm âm thanh button click
     */
    private void handleMouseClick(int x, int y) {
        if (currentState == GameState.START) {
            if (startButton.isClicked(x, y)) {
                Assets.playButtonSound(); // ⭐ Âm thanh button
                startGame();
                resetGame();
            }
            if (muteButton.isClicked(x, y)) {
                Assets.playButtonSound(); // ⭐ Âm thanh button
                Assets.toggleSound();
            }
        } else if (currentState == GameState.END) {
            if (restartButton.isClicked(x, y)) {
                Assets.playButtonSound(); // ⭐ Âm thanh button
                startGame();
                resetGame();
            }
            if (muteButton.isClicked(x, y)) {
                Assets.playButtonSound(); // ⭐ Âm thanh button
                Assets.toggleSound();
            }
        } else if (currentState == GameState.PLAYING) {
            // Mute button vẫn hoạt động khi đang chơi
            if (muteButton.isClicked(x, y)) {
                Assets.playButtonSound(); // ⭐ Âm thanh button
                Assets.toggleSound();
            }
        }
    }

    private void setupTimer() {
        gameTimer = new Timer(DELAY, this);
    }

    private void setupFonts() {
        gameFont = new Font("Arial", Font.BOLD, 16);
        bigFont = new Font("Arial", Font.BOLD, 32);
    }

    public void startGame() {
        currentState = GameState.PLAYING;
        gameRunning = true;
        gameTimer.start();
        Assets.playBackgroundMusic();
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
        if (currentState == GameState.START) {
            if (inputHandler.isKeyJustPressed(InputHandler.KEY_ENTER)) {
                Assets.playButtonSound(); // ⭐ Âm thanh button
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
                Assets.playButtonSound(); // ⭐ Âm thanh button
                startGame();
                resetGame();
            }
            if (inputHandler.isKeyJustPressed(InputHandler.KEY_ESCAPE)) {
                Assets.playButtonSound(); // ⭐ Âm thanh button
                currentState = GameState.START;
                Assets.stopBackgroundMusic();
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

    /**
     * ⭐ FIXED: Thêm âm thanh death và gameOver
     */
    private void checkCollisions() {
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
                                35
                        ));
                        score += enemy.getScoreValue();
                        spawnPowerUpItem(enemy.getX() + enemy.getWidth()/2, enemy.getY());
                        Assets.playExplosionSound();
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
                    // ⭐ Phát âm thanh death khi player bị damage
                    Assets.playDeathSound();

                    lives--;
                    if (lives <= 0) {
                        currentState = GameState.END;
                        Assets.stopBackgroundMusic();
                        // ⭐ Phát âm thanh game over
                        Assets.playGameOverSound();
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
                        35
                ));

                if (tookDamage) {
                    // ⭐ Phát âm thanh death khi player bị damage
                    Assets.playDeathSound();

                    lives--;
                    if (lives <= 0) {
                        currentState = GameState.END;
                        Assets.stopBackgroundMusic();
                        // ⭐ Phát âm thanh game over
                        Assets.playGameOverSound();
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
        BufferedImage newBackground = Assets.getBackgroundImage(currentWave);
        if (newBackground != null) {
            Assets.backGroundImage = newBackground;
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
        if (Assets.backGroundStartImage != null) {
            g2d.drawImage(Assets.backGroundStartImage, 0, 0, WIDTH, HEIGHT, null);
        } else {
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 20, 50),
                    0, HEIGHT, new Color(50, 20, 80)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "SPACE INVADERS";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(title)) / 2;
        int y = HEIGHT / 3;

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(title, x + 3, y + 3);

        g2d.setColor(new Color(255, 215, 0));
        g2d.drawString(title, x, y);

        startButton.draw(g2d);
        muteButton.draw(g2d);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(Color.WHITE);
        String soundStatus = Assets.isSoundEnabled() ? "Sound: ON" : "Sound: OFF";
        g2d.drawString(soundStatus, WIDTH - 100, 90);

        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(new Color(200, 200, 200));
        String hint = "Press ENTER to Start";
        fm = g2d.getFontMetrics();
        x = (WIDTH - fm.stringWidth(hint)) / 2;
        g2d.drawString(hint, x, HEIGHT - 50);
    }

    private void drawEndScreen(Graphics2D g2d) {
        if (Assets.backGroundEndImage != null) {
            g2d.drawImage(Assets.backGroundEndImage, 0, 0, WIDTH, HEIGHT, null);
        } else {
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(10, 10, 20),
                    0, HEIGHT, new Color(40, 10, 10)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 56));
        String gameOverText = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(gameOverText)) / 2;
        int y = HEIGHT / 3;

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(gameOverText, x + 3, y + 3);

        g2d.setColor(new Color(255, 50, 50));
        g2d.drawString(gameOverText, x, y);

        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        g2d.setColor(Color.WHITE);
        String scoreText = "Final Score: " + score;
        fm = g2d.getFontMetrics();
        x = (WIDTH - fm.stringWidth(scoreText)) / 2;
        y += 70;
        g2d.drawString(scoreText, x, y);

        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(new Color(200, 200, 200));
        String waveText = "Wave Reached: " + waveManager.getCurrentWave();
        fm = g2d.getFontMetrics();
        x = (WIDTH - fm.stringWidth(waveText)) / 2;
        y += 40;
        g2d.drawString(waveText, x, y);

        restartButton.draw(g2d);
        muteButton.draw(g2d);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(Color.WHITE);
        String soundStatus = Assets.isSoundEnabled() ? "Sound: ON" : "Sound: OFF";
        g2d.drawString(soundStatus, WIDTH - 100, 90);

        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.setColor(new Color(220, 220, 220));
        String escText = "Bấm ESC để trở về giao diện bắt đầu";
        fm = g2d.getFontMetrics();
        x = (WIDTH - fm.stringWidth(escText)) / 2;
        g2d.drawString(escText, x, HEIGHT - 80);

        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(new Color(180, 180, 180));
        String enterText = "Press ENTER to Restart";
        fm = g2d.getFontMetrics();
        x = (WIDTH - fm.stringWidth(enterText)) / 2;
        g2d.drawString(enterText, x, HEIGHT - 50);
    }

    private void drawMenu(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(bigFont);

        String title = "SPACE INVADERS";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(title)) / 2;
        int y = HEIGHT / 2 - 50;
        g2d.drawString(title, x, y);

        g2d.setFont(gameFont);
        String startText = "Press ENTER to Start";
        fm = g2d.getFontMetrics();
        x = (WIDTH - fm.stringWidth(startText)) / 2;
        y += 80;
        g2d.drawString(startText, x, y);
    }

    private void drawGame(Graphics2D g2d) {
        if (Assets.backGroundImage != null) {
            g2d.drawImage(Assets.backGroundImage, 0, 0, getWidth(), getHeight(), null);
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
        drawPowerUpHUD(g2d);

        muteButton.draw(g2d);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.WHITE);
        String soundStatus = Assets.isSoundEnabled() ? "ON" : "OFF";
        g2d.drawString(soundStatus, WIDTH - 50, 90);
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(gameFont);

        g2d.drawString("Score: " + score, 10, 25);
        g2d.drawString("Lives: " + lives, 10, 50);
        g2d.drawString("Wave: " + waveManager.getCurrentWave(), 10, 75);
    }

    private void drawPowerUpHUD(Graphics2D g2d) {
        int hudX = WIDTH - 200;
        int hudY = 120;

        g2d.setFont(new Font("Arial", Font.BOLD, 14));

        if (player.hasTripleShot()) {
            g2d.setColor(Color.YELLOW);
            int seconds = player.getTripleShotDuration() / 60;
            g2d.drawString("⚡ TRIPLE: " + seconds + "s", hudX, hudY);
            hudY += 25;
        }

        if (player.hasPiercing()) {
            g2d.setColor(Color.CYAN);
            int seconds = player.getPiercingDuration() / 60;
            g2d.drawString("⚡ PIERCE: " + seconds + "s", hudX, hudY);
            hudY += 25;
        }

        if (player.hasShield()) {
            g2d.setColor(new Color(100, 200, 255));
            int seconds = player.getShieldDuration() / 60;
            g2d.drawString("🛡 SHIELD: " + seconds + "s", hudX, hudY);
        }
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
        y += 50;
        g2d.drawString(resumeText, x, y);
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(bigFont);

        String gameOverText = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(gameOverText)) / 2;
        int y = HEIGHT / 2 - 50;
        g2d.drawString(gameOverText, x, y);

        g2d.setFont(gameFont);
        String scoreText = "Final Score: " + score;
        fm = g2d.getFontMetrics();
        x = (WIDTH - fm.stringWidth(scoreText)) / 2;
        y += 50;
        g2d.drawString(scoreText, x, y);

        String restartText = "Press ENTER to Restart";
        fm = g2d.getFontMetrics();
        x = (WIDTH - fm.stringWidth(restartText)) / 2;
        y += 30;
        g2d.drawString(restartText, x, y);
    }
}