package main;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Lớp quản lý tài nguyên game (hình ảnh, âm thanh)
 */
public class Assets {
    // Hình ảnh
    public static BufferedImage playerImage;
    public static BufferedImage enemyImage;
    public static BufferedImage bossImage;
    public static BufferedImage bulletImage;
    public static BufferedImage explosionImage;
    public static BufferedImage backGroundImage;
    
    // Âm thanh
    public static Clip shootSound;
    public static Clip explosionSound;
    public static Clip backgroundMusic;
    
    /**
     * Load tất cả tài nguyên game
     */
    public static void loadAssets() {
        loadImages();
        loadSounds();
    }
    
    /**
     * Load hình ảnh
     */
    private static void loadImages() {

        try {
            // Load hình ảnh từ thư mục resources

            playerImage = loadImage("/images/player.png");
            enemyImage = loadImage("/images/enemy.png");
            try {
                bossImage = loadImage("/images/boss1.png");
            } catch (Exception e) {
                bossImage = enemyImage; // Fallback nếu không có boss image
            }
            bulletImage = loadImage("/images/bullet.png");
            explosionImage = loadImage("/images/explosion.png");
            backGroundImage = loadImage("/images/backGround.png");
            System.out.println("[DEBUG] ĐÃ LOAD THÀNH CÔNG TẤT CẢ HÌNH ẢNH!");
        } catch (Exception e) {
            System.err.println("Lỗi load hình ảnh: " + e.getMessage());
            // Tạo hình ảnh mặc định nếu không load được
            createDefaultImages();
        }
    }
    
    /**
     * Load âm thanh
     */
    private static void loadSounds() {
        try {
            shootSound = loadSound("/sounds/shoot.wav");
            explosionSound = loadSound("/sounds/explosion.wav");
            backgroundMusic = loadSound("/sounds/background.wav");
        } catch (Exception e) {
            System.err.println("Lỗi load âm thanh: " + e.getMessage());
        }
    }
    
    /**
     * Load hình ảnh từ resources
     */
    private static BufferedImage loadImage(String path) throws IOException {
        InputStream is = Assets.class.getResourceAsStream(path);
        if (is == null) {
            throw new IOException("Không tìm thấy file: " + path);
        }
        return ImageIO.read(is);
    }
    
    /**
     * Load âm thanh từ resources
     */
    private static Clip loadSound(String path) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        InputStream is = Assets.class.getResourceAsStream(path);
        if (is == null) {
            throw new IOException("Không tìm thấy file: " + path);
        }
        
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        return clip;
    }
    
    /**
     * Tạo hình ảnh mặc định nếu không load được file
     */
    private static void createDefaultImages() {
        // Tạo hình ảnh mặc định đơn giản
        playerImage = new BufferedImage(40, 30, BufferedImage.TYPE_INT_RGB);
        enemyImage = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
        bulletImage = new BufferedImage(4, 10, BufferedImage.TYPE_INT_RGB);
        explosionImage = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
        
        // Vẽ hình đơn giản (có thể tùy chỉnh sau)
        // Ở đây chỉ tạo hình vuông đơn giản
    }
    
    /**
     * Phát âm thanh bắn
     */
    public static void playShootSound() {
        if (shootSound != null) {
            shootSound.setFramePosition(0);
            shootSound.start();
        }
    }
    
    /**
     * Phát âm thanh nổ
     */
    public static void playExplosionSound() {
        if (explosionSound != null) {
            explosionSound.setFramePosition(0);
            explosionSound.start();
        }
    }
    
    /**
     * Phát nhạc nền
     */
    public static void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    /**
     * Dừng nhạc nền
     */
    public static void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }
}

