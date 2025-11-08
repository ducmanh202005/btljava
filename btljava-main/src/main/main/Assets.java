package main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

/**
 * Lớp quản lý tài nguyên game - UPDATED với multiple enemies và bosses
 */
public class Assets {
    // Hình ảnh player và chung
    public static BufferedImage playerImage;
    public static BufferedImage explosionImage;
    public static BufferedImage backGroundImage;
    
    // Power-up items
    public static BufferedImage bulletPiercingImage;
    public static BufferedImage hpItemImage;
    public static BufferedImage shieldItemImage;
    public static BufferedImage piercingItemImage;
    public static BufferedImage scoreItemImage;
    public static BufferedImage tripleItemImage;
    public static BufferedImage powerItemImage;
    
    // Maps cho enemies, bosses và bullets theo wave
    public static Map<Integer, BufferedImage> enemyImages = new HashMap<>();
    public static Map<Integer, BufferedImage> bossImages = new HashMap<>();
    public static Map<Integer, BufferedImage> enemyBulletImages = new HashMap<>();
    public static Map<Integer, BufferedImage> bossBulletImages = new HashMap<>();
    
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
            // Load hình ảnh chung
            playerImage = loadImage("/images/player.png");
            explosionImage = loadImage("/images/explosion.png");
            backGroundImage = loadImage("/images/backGround5.png");
            
            // Load power-up items
            bulletPiercingImage = loadImage("/images/bulletpiercing.png");
            hpItemImage = loadImage("/images/hpitem.png");
            shieldItemImage = loadImage("/images/shielditem.png");
            piercingItemImage = loadImage("/images/piercingitem.png");
            scoreItemImage = loadImage("/images/scoreitem.png");
            tripleItemImage = loadImage("/images/tripleitem.png");
            powerItemImage = loadImage("/images/poweritem.png");
            
            // Load enemies (1-5)
            for (int i = 1; i <= 5; i++) {
                try {
                    enemyImages.put(i, loadImage("/images/enemy" + i + ".png"));
                    System.out.println("[DEBUG] Loaded enemy" + i + ".png");
                } catch (Exception e) {
                    System.err.println("Không tìm thấy enemy" + i + ".png");
                }
            }
            
            // Load bosses (1-5)
            for (int i = 1; i <= 5; i++) {
                try {
                    bossImages.put(i, loadImage("/images/boss" + i + ".png"));
                    System.out.println("[DEBUG] Loaded boss" + i + ".png");
                } catch (Exception e) {
                    System.err.println("Không tìm thấy boss" + i + ".png");
                }
            }
            
            // Load enemy bullets (1-5)
            for (int i = 1; i <= 5; i++) {
                try {
                    enemyBulletImages.put(i, loadImage("/images/bulletenemy" + i + ".png"));
                    System.out.println("[DEBUG] Loaded bulletenemy" + i + ".png");
                } catch (Exception e) {
                    System.err.println("Không tìm thấy bulletenemy" + i + ".png");
                }
            }
            
            // Load boss bullets (1-5)
            for (int i = 1; i <= 5; i++) {
                try {
                    bossBulletImages.put(i, loadImage("/images/bulletboss" + i + ".png"));
                    System.out.println("[DEBUG] Loaded bulletboss" + i + ".png");
                } catch (Exception e) {
                    System.err.println("Không tìm thấy bulletboss" + i + ".png");
                }
            }
            
            System.out.println("[DEBUG] ĐÃ LOAD THÀNH CÔNG TẤT CẢ HÌNH ẢNH!");
        } catch (Exception e) {
            System.err.println("Lỗi load hình ảnh: " + e.getMessage());
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
        playerImage = new BufferedImage(40, 30, BufferedImage.TYPE_INT_RGB);
        explosionImage = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
    }
    
    /**
     * Lấy hình ảnh enemy theo wave (cycle 1-5)
     */
    public static BufferedImage getEnemyImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1; // Cycle 1-5
        return enemyImages.getOrDefault(imageIndex, null);
    }
    
    /**
     * Lấy hình ảnh boss theo wave (cycle 1-5)
     */
    public static BufferedImage getBossImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1; // Cycle 1-5
        return bossImages.getOrDefault(imageIndex, null);
    }
    
    /**
     * Lấy hình ảnh đạn enemy theo wave (cycle 1-5)
     */
    public static BufferedImage getEnemyBulletImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1; // Cycle 1-5
        return enemyBulletImages.getOrDefault(imageIndex, null);
    }
    
    /**
     * Lấy hình ảnh đạn boss theo wave (cycle 1-5)
     */
    public static BufferedImage getBossBulletImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1; // Cycle 1-5
        return bossBulletImages.getOrDefault(imageIndex, null);
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