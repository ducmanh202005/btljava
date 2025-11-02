package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

/**
 * GamePanel - COMPLETE VERSION với Power-up System
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

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        initializeGame();
        setupInput();
        setupTimer();
        setupFonts();
        gameTimer.start();
        requestFocusInWindow();
    }

    private void initializeGame() {
        currentState = GameState.MENU;
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
        spawnWave();
    }

    /**
     * Spawn wave enemies
     */
    private void spawnWave() {
        enemies.clear();
        waveManager.spawnWave(enemies, WIDTH, HEIGHT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    /**
     * FIXED: Update game logic
     */
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
        if (currentState == GameState.MENU) {
            if (inputHandler.isKeyJustPressed(InputHandler.KEY_ENTER)) {
                startGame();
                resetGame();
            }
        } else if (currentState == GameState.PLAYING) {
            // ★ DI CHUYỂN 4 HƯỚNG ★
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

            // BẮN (giữ SPACE để bắn liên tục)
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
        } else if (currentState == GameState.GAMEOVER) {
            if (inputHandler.isKeyJustPressed(InputHandler.KEY_ENTER)) {
                currentState = GameState.MENU;
                resetGame();
            }
        }
    }

    /**
     * FIXED: Update game objects
     */
    private void updateGameObjects() {
        player.update();

        for (Enemy enemy : enemies) {
            enemy.update();
            enemy.shoot(enemyBullets);
        }

        updateBullets(playerBullets);
        updateBullets(enemyBullets);
        updateExplosions();
        updateItems(); // ★ CẬP NHẬT ITEMS ★

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

    /**
     * ★ CẬP NHẬT ITEMS VÀ KIỂM TRA VA CHẠM VỚI PLAYER ★
     */
    private void updateItems() {
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            item.update();

            // Kiểm tra va chạm với player
            if (item.isActive() && item.getBounds().intersects(player.getBounds())) {
                collectItem(item);
                item.setActive(false);
            }

            // Xóa item không còn active
            if (!item.isActive()) {
                iterator.remove();
            }
        }
    }

    /**
     * ★ NHẶT ITEM VÀ KÍCH HOẠT POWER-UP ★
     */
    private void collectItem(Item item) {
        switch (item.getType()) {
            case HEALTH:
                if (lives < 5) { // Giới hạn tối đa 5 lives
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

        // Phát âm thanh khi nhặt item (nếu có)
        // Assets.playItemPickupSound();
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
     * ★ KIỂM TRA VA CHẠM VỚI HỖ TRỢ PIERCING VÀ SHIELD ★
     */
    private void checkCollisions() {
        // Player bullets vs Enemies (HỖ TRỢ PIERCING)
        Iterator<Bullet> bulletIterator = playerBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            // Bỏ qua đạn enemy (direction > 0)
            if (bullet.getDirection() > 0) continue;

            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();

                if (enemy.isActive() && Collision.checkBulletEnemyCollision(bullet, enemy)) {
                    enemy.takeDamage(bullet.getDamage());

                    // ★ GỌI onHit() THAY VÌ setActive(false) ★
                    bullet.onHit();

                    if (!enemy.isActive()) {
                        explosions.add(new Explosion(
                                enemy.getX() + enemy.getWidth()/2,
                                enemy.getY() + enemy.getHeight()/2,
                                35
                        ));
                        score += enemy.getScoreValue();

                        // ★ SPAWN POWER-UP ITEMS ★
                        spawnPowerUpItem(enemy.getX() + enemy.getWidth()/2, enemy.getY());

                        Assets.playExplosionSound();
                    }

                    // ★ CHỈ BREAK NẾU KHÔNG PHẢI PIERCING ★
                    if (!bullet.isPiercing()) {
                        break;
                    }
                }
            }
        }

        // Enemy bullets vs Player (HỖ TRỢ SHIELD)
        bulletIterator = enemyBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            if (bullet.isActive() && Collision.checkEnemyBulletPlayerCollision(bullet, player)) {
                bullet.setActive(false);

                // ★ XỬ LÝ SHIELD VÀ INVINCIBILITY ★
                boolean tookDamage = player.takeDamage();

                if (tookDamage) {
                    // Player thực sự mất máu (không có shield)
                    lives--;
                    if (lives <= 0) {
                        currentState = GameState.GAMEOVER;
                    }
                }
                // Nếu không tookDamage = mất shield hoặc đang invincible
            }
        }

        // Enemy vs Player (HỖ TRỢ SHIELD)
        Iterator<Enemy> enemyCollisionIterator = enemies.iterator();
        while (enemyCollisionIterator.hasNext()) {
            Enemy enemy = enemyCollisionIterator.next();

            if (enemy.isActive() && Collision.checkEnemyPlayerCollision(enemy, player)) {
                enemy.setActive(false);

                // ★ XỬ LÝ SHIELD VÀ INVINCIBILITY ★
                boolean tookDamage = player.takeDamage();

                explosions.add(new Explosion(
                        enemy.getX() + enemy.getWidth()/2,
                        enemy.getY() + enemy.getHeight()/2,
                        35
                ));

                if (tookDamage) {
                    lives--;
                    if (lives <= 0) {
                        currentState = GameState.GAMEOVER;
                    }
                }
            }
        }
    }

    /**
     * ★ SPAWN POWER-UP ITEMS (30% cơ hội) ★
     */
    private void spawnPowerUpItem(int x, int y) {
        java.util.Random random = new java.util.Random();
        double rand = random.nextDouble();

        if (rand < 0.3) { // 30% cơ hội drop item
            Item.ItemType type;
            double itemRand = random.nextDouble();

            // Phân phối items: 25% mỗi loại power-up, 15% health, 10% score
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

    /**
     * ★ GIỮ NGUYÊN - FALLBACK CHO CODE CŨ ★
     */
    private void spawnRandomItem(int x, int y) {
        spawnPowerUpItem(x, y);
    }

    /**
     * Check wave progress và chuyển wave
     */
    private void checkWaveProgress() {
        if (waveManager.isWaveComplete(enemies)) {
            waveManager.nextWave();
            level = waveManager.getCurrentWave();
            spawnWave();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (Assets.backGroundImage != null) {
            g2d.drawImage(Assets.backGroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (currentState) {
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
            case GAMEOVER:
                drawGameOver(g2d);
                break;
        }
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
        drawPowerUpHUD(g2d); // ★ VẼ POWER-UP HUD ★
    }

    /**
     * ★ VẼ UI HUD VỚI POWER-UP TIMERS ★
     */
    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(gameFont);

        g2d.drawString("Score: " + score, 10, 25);
        g2d.drawString("Lives: " + lives, 10, 50);
        g2d.drawString("Wave: " + waveManager.getCurrentWave(), 10, 75);

        int activeEnemies = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                activeEnemies++;
            }
        }
    }

    /**
     * ★ VẼ POWER-UP HUD (THỜI GIAN CÒN LẠI) ★
     */
    private void drawPowerUpHUD(Graphics2D g2d) {
        int hudX = WIDTH - 200;
        int hudY = 30;

        g2d.setFont(new Font("Arial", Font.BOLD, 14));

        // Triple Shot
        if (player.hasTripleShot()) {
            g2d.setColor(Color.YELLOW);
            int seconds = player.getTripleShotDuration() / 60;
            g2d.drawString("⚡ TRIPLE: " + seconds + "s", hudX, hudY);
            hudY += 25;
        }

        // Piercing
        if (player.hasPiercing()) {
            g2d.setColor(Color.CYAN);
            int seconds = player.getPiercingDuration() / 60;
            g2d.drawString("⚡ PIERCE: " + seconds + "s", hudX, hudY);
            hudY += 25;
        }

        // Shield
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