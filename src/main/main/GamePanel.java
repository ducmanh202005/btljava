package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * GamePanel - JPanel chính để vẽ game và xử lý game loop
 */
public class GamePanel extends JPanel implements ActionListener {
    // Kích thước màn hình
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    // Game loop
    private Timer gameTimer;
    private static final int FPS = 60;
    private static final int DELAY = 1000 / FPS;
    
    // Game state
    private GameState currentState;
    private InputHandler inputHandler;
    
    // Game objects (sẽ được các thành viên khác implement)
    private Player player;
    private List<Enemy> enemies;
    private List<Bullet> playerBullets;
    private List<Bullet> enemyBullets;
    
    // Game variables
    private int score;
    private int lives;
    private int level;
    private boolean gameRunning;
    
    // UI
    private Font gameFont;
    private Font bigFont;

    public GamePanel() {
        // Thiết lập panel
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // Khởi tạo
        initializeGame();
        setupInput();
        setupTimer();
        setupFonts();
        // Bắt đầu timer để menu cũng nhận input
        gameTimer.start();
        requestFocusInWindow();
    }
    
    /**
     * Khởi tạo game
     */
    private void initializeGame() {
        currentState = GameState.MENU;
        inputHandler = new InputHandler();
        
        // Khởi tạo game objects
        player = new Player(WIDTH / 2, HEIGHT - 50);
        enemies = new ArrayList<>();
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        
        // Game variables
        score = 0;
        lives = 3;
        level = 1;
        gameRunning = false;
        
        // Load assets
        Assets.loadAssets();
    }
    
    /**
     * Thiết lập input handler
     */
    private void setupInput() {
        addKeyListener(inputHandler);
    }
    
    /**
     * Thiết lập timer cho game loop
     */
    private void setupTimer() {
        gameTimer = new Timer(DELAY, this);
    }
    
    /**
     * Thiết lập font
     */
    private void setupFonts() {
        gameFont = new Font("Arial", Font.BOLD, 16);
        bigFont = new Font("Arial", Font.BOLD, 32);
    }
    
    /**
     * Bắt đầu game
     */
    public void startGame() {
        currentState = GameState.PLAYING;
        gameRunning = true;
        gameTimer.start();
        requestFocus();
    }
    
    /**
     * Dừng game
     */
    public void stopGame() {
        gameRunning = false;
        gameTimer.stop();
    }
    
    /**
     * Reset game
     */
    public void resetGame() {
        score = 0;
        lives = 3;
        level = 1;
        playerBullets.clear();
        enemyBullets.clear();
        enemies.clear();
        player.reset();
        spawnEnemies();
    }
    
    /**
     * Spawn enemies cho level hiện tại
     */
    private void spawnEnemies() {
        enemies.clear();
        int rows = 5;
        int cols = 10;
        int startX = 50;
        int startY = 50;
        int spacingX = 60;
        int spacingY = 50;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = startX + col * spacingX;
                int y = startY + row * spacingY;
                enemies.add(new Enemy(x, y));
            }
        }
    }
    
    /**
     * Game loop chính
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Luôn cập nhật để MENU cũng xử lý phím ENTER
        update();
        repaint();
    }
    
    /**
     * Cập nhật game logic
     */
    private void update() {
        // Handle inputs before clearing one-shot keys
        handleInput();
        
        if (currentState == GameState.PLAYING) {
            updateGameObjects();
            checkCollisions();
            checkGameConditions();
        }

        // Clear just-pressed keys at the end of the frame
        inputHandler.update();
    }
    
    /**
     * Xử lý input
     */
    private void handleInput() {
        if (currentState == GameState.MENU) {
            if (inputHandler.isKeyJustPressed(InputHandler.KEY_ENTER)) {
                startGame();
                resetGame();
            }
        } else if (currentState == GameState.PLAYING) {
            // Player movement
            if (inputHandler.isKeyPressed(InputHandler.KEY_LEFT)) {
                player.moveLeft();
            }
            if (inputHandler.isKeyPressed(InputHandler.KEY_RIGHT)) {
                player.moveRight();
            }
            if (inputHandler.isKeyJustPressed(InputHandler.KEY_SPACE)) {
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
     * Cập nhật các game objects
     */
    private void updateGameObjects() {
        // Update player
        player.update();
        
        // Update enemies
        for (Enemy enemy : enemies) {
            enemy.update();
        }
        
        // Update bullets
        updateBullets(playerBullets);
        updateBullets(enemyBullets);
    }
    
    /**
     * Cập nhật danh sách bullets
     */
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
     * Kiểm tra va chạm
     */
    private void checkCollisions() {
        // Player bullets vs Enemies
        Iterator<Bullet> bulletIterator = playerBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                if (Collision.checkBulletEnemyCollision(bullet, enemy)) {
                    bullet.setActive(false);
                    enemy.setActive(false);
                    score += 10;
                    Assets.playExplosionSound();
                    break;
                }
            }
        }
        
        // Enemy bullets vs Player
        bulletIterator = enemyBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            if (Collision.checkEnemyBulletPlayerCollision(bullet, player)) {
                bullet.setActive(false);
                lives--;
                if (lives <= 0) {
                    currentState = GameState.GAMEOVER;
                }
            }
        }
    }
    
    /**
     * Kiểm tra điều kiện game
     */
    private void checkGameConditions() {
        // Kiểm tra thắng (hết enemy)
        boolean allEnemiesDead = true;
        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                allEnemiesDead = false;
                break;
            }
        }
        
        if (allEnemiesDead) {
            level++;
            spawnEnemies();
        }
    }
    
    /**
     * Vẽ game
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (Assets.backGroundImage != null) {
            // Vẽ ảnh nền nếu nó đã được load
            g2d.drawImage(Assets.backGroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Nếu không có ảnh, vẽ nền đen
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        // Anti-aliasing
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
    
    /**
     * Vẽ menu
     */
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
    
    /**
     * Vẽ game
     */
    private void drawGame(Graphics2D g2d) {
        // Vẽ player
        player.draw(g2d);
        
        // Vẽ enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g2d);
        }
        
        // Vẽ bullets
        for (Bullet bullet : playerBullets) {
            bullet.draw(g2d);
        }
        for (Bullet bullet : enemyBullets) {
            bullet.draw(g2d);
        }
        
        // Vẽ UI
        drawUI(g2d);
    }
    
    /**
     * Vẽ UI
     */
    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(gameFont);
        
        // Score
        g2d.drawString("Score: " + score, 10, 25);
        
        // Lives
        g2d.drawString("Lives: " + lives, 10, 50);
        
        // Level
        g2d.drawString("Level: " + level, 10, 75);
    }
    
    /**
     * Vẽ màn hình pause
     */
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
    
    /**
     * Vẽ game over
     */
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
