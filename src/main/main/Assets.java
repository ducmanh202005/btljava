package main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

/**
 * Lớp quản lý tài nguyên game - UPDATED với âm thanh mới
 */
public class Assets {
    // Hình ảnh player và chung
    public static BufferedImage playerImage;
    public static BufferedImage bulletImage;
    public static BufferedImage explosionImage;
    public static BufferedImage backGroundImage;

    // Start/End screen images
    public static BufferedImage backGroundStartImage;
    public static BufferedImage backGroundEndImage;
    public static BufferedImage startButtonImage;
    public static BufferedImage restartButtonImage;
    public static BufferedImage muteButtonImage;

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
    public static Map<Integer, BufferedImage> backgroundImages = new HashMap<>();

    // ⭐ NEW: Âm thanh mới
    public static Clip shootSound;        // /sounds/bullet.mp3
    public static Clip explosionSound;    // /sounds/explosion.mp3
    public static Clip backgroundMusic;   // /sounds/background.wav
    public static Clip buttonSound;       // /sounds/button.mp3
    public static Clip deathSound;        // /sounds/death.mp3
    public static Clip gameOverSound;     // /sounds/gameOver.mp3

    // Sound control
    private static boolean soundEnabled = true;

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
            bulletImage = loadImage("/images/bullet.png");
            explosionImage = loadImage("/images/explosion.png");
            backGroundImage = loadImage("/images/backGround5.png");

            // Load Start/End screen images
            try {
                backGroundStartImage = loadImage("/images/backGroundStart.png");
                System.out.println("[DEBUG] Loaded backGroundStart.png");
            } catch (Exception e) {
                System.err.println("Không tìm thấy backGroundStart.png - using fallback");
                backGroundStartImage = createFallbackImage(800, 600, java.awt.Color.DARK_GRAY);
            }

            try {
                backGroundEndImage = loadImage("/images/backGroundEnd.png");
                System.out.println("[DEBUG] Loaded backGroundEnd.png");
            } catch (Exception e) {
                System.err.println("Không tìm thấy backGroundEnd.png - using fallback");
                backGroundEndImage = createFallbackImage(800, 600, java.awt.Color.BLACK);
            }

            try {
                startButtonImage = loadImage("/images/start.png");
                System.out.println("[DEBUG] Loaded start.png");
            } catch (Exception e) {
                System.err.println("Không tìm thấy start.png");
                startButtonImage = null;
            }

            try {
                restartButtonImage = loadImage("/images/reStart.png");
                System.out.println("[DEBUG] Loaded reStart.png");
            } catch (Exception e) {
                System.err.println("Không tìm thấy reStart.png");
                restartButtonImage = null;
            }

            try {
                muteButtonImage = loadImage("/images/mute.png");
                System.out.println("[DEBUG] Loaded mute.png");
            } catch (Exception e) {
                System.err.println("Không tìm thấy mute.png");
                muteButtonImage = null;
            }

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

            // Load backgrounds (1-5)
            for (int i = 1; i <= 5; i++) {
                try {
                    backgroundImages.put(i, loadImage("/images/backGround" + i + ".png"));
                    System.out.println("[DEBUG] Loaded backGround" + i + ".png");
                } catch (Exception e) {
                    System.err.println("Không tìm thấy backGround" + i + ".png");
                }
            }

            System.out.println("[DEBUG] ĐÃ LOAD THÀNH CÔNG TẤT CẢ HÌNH ẢNH!");
        } catch (Exception e) {
            System.err.println("Lỗi load hình ảnh: " + e.getMessage());
            createDefaultImages();
        }
    }

    /**
     * ⭐ UPDATED: Load âm thanh với các file mới
     */
    private static void loadSounds() {
        try {
            // Âm thanh player bắn (bullet.mp3)
            try {
                shootSound = loadSound("/sounds/bullet.wav");
                System.out.println("[SOUND] Loaded bullet.mp3");
            } catch (Exception e) {
                System.err.println("Không tìm thấy bullet.mp3, thử bullet.wav");
                try {
                    shootSound = loadSound("/sounds/shoot.wav");
                } catch (Exception e2) {
                    System.err.println("Không load được shoot sound");
                }
            }

            // Âm thanh nổ/enemy chết (explosion.mp3)
            try {
                explosionSound = loadSound("/sounds/explosion.wav");
                System.out.println("[SOUND] Loaded explosion.mp3");
            } catch (Exception e) {
                System.err.println("Không tìm thấy explosion.mp3, thử explosion.wav");
                try {
                    explosionSound = loadSound("/sounds/explosion.wav");
                } catch (Exception e2) {
                    System.err.println("Không load được explosion sound");
                }
            }

            // Nhạc nền
            try {
                backgroundMusic = loadSound("/sounds/background.wav");
                System.out.println("[SOUND] Loaded background.wav");
            } catch (Exception e) {
                System.err.println("Không load được background music");
            }

            // ⭐ NEW: Button click sound
            try {
                buttonSound = loadSound("/sounds/button.wav");
                System.out.println("[SOUND] Loaded button.mp3");
            } catch (Exception e) {
                System.err.println("Không tìm thấy button.mp3");
            }

            // ⭐ NEW: Player death sound
            try {
                deathSound = loadSound("/sounds/death.wav");
                System.out.println("[SOUND] Loaded death.mp3");
            } catch (Exception e) {
                System.err.println("Không tìm thấy death.mp3");
            }

            // ⭐ NEW: Game over sound
            try {
                gameOverSound = loadSound("/sounds/gameOver.wav");
                System.out.println("[SOUND] Loaded gameOver.mp3");
            } catch (Exception e) {
                System.err.println("Không tìm thấy gameOver.mp3");
            }

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
     * Load âm thanh từ resources - hỗ trợ cả WAV và MP3
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
     * Tạo hình ảnh fallback với màu cụ thể
     */
    private static BufferedImage createFallbackImage(int width, int height, java.awt.Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return img;
    }

    /**
     * Lấy hình ảnh enemy theo wave (cycle 1-5)
     */
    public static BufferedImage getEnemyImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return enemyImages.getOrDefault(imageIndex, null);
    }

    /**
     * Lấy hình ảnh boss theo wave (cycle 1-5)
     */
    public static BufferedImage getBossImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return bossImages.getOrDefault(imageIndex, null);
    }

    /**
     * Lấy hình ảnh đạn enemy theo wave (cycle 1-5)
     */
    public static BufferedImage getEnemyBulletImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return enemyBulletImages.getOrDefault(imageIndex, null);
    }

    /**
     * Lấy hình ảnh đạn boss theo wave (cycle 1-5)
     */
    public static BufferedImage getBossBulletImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return bossBulletImages.getOrDefault(imageIndex, null);
    }

    /**
     * Lấy hình ảnh background theo wave (cycle 1-5)
     */
    public static BufferedImage getBackgroundImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        BufferedImage bg = backgroundImages.getOrDefault(imageIndex, null);
        return bg != null ? bg : backGroundImage;
    }

    // Sound control methods

    /**
     * Toggle âm thanh bật/tắt
     */
    public static void toggleSound() {
        soundEnabled = !soundEnabled;
        if (!soundEnabled) {
            stopBackgroundMusic();
        } else {
            playBackgroundMusic();
        }
        System.out.println("[SOUND] Sound " + (soundEnabled ? "ENABLED" : "DISABLED"));
    }

    /**
     * Kiểm tra âm thanh có đang bật không
     */
    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Set âm thanh bật/tắt
     */
    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        if (!soundEnabled) {
            stopBackgroundMusic();
        }
    }

    /**
     * Phát âm thanh bắn (player bullet)
     */
    public static void playShootSound() {
        if (soundEnabled && shootSound != null) {
            shootSound.setFramePosition(0);
            shootSound.start();
        }
    }

    /**
     * Phát âm thanh nổ (enemy chết)
     */
    public static void playExplosionSound() {
        if (soundEnabled && explosionSound != null) {
            explosionSound.setFramePosition(0);
            explosionSound.start();
        }
    }

    /**
     * ⭐ NEW: Phát âm thanh button click
     */
    public static void playButtonSound() {
        if (soundEnabled && buttonSound != null) {
            buttonSound.setFramePosition(0);
            buttonSound.start();
        }
    }

    /**
     * ⭐ NEW: Phát âm thanh player chết
     */
    public static void playDeathSound() {
        if (soundEnabled && deathSound != null) {
            deathSound.setFramePosition(0);
            deathSound.start();
        }
    }

    /**
     * ⭐ NEW: Phát âm thanh game over
     */
    public static void playGameOverSound() {
        if (soundEnabled && gameOverSound != null) {
            gameOverSound.setFramePosition(0);
            gameOverSound.start();
        }
    }

    /**
     * Phát nhạc nền
     */
    public static void playBackgroundMusic() {
        if (soundEnabled && backgroundMusic != null) {
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