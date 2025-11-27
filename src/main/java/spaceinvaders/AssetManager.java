package spaceinvaders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

/**
 * Lớp quản lý tài nguyên game - REFACTORED theo nguyên tắc OOP
 * Sử dụng Singleton pattern để đảm bảo chỉ có một instance duy nhất
 */
public class AssetManager {
    private static AssetManager instance;
    
    // Hình ảnh player và chung
    private BufferedImage playerImage;
    private BufferedImage bulletImage;
    private BufferedImage explosionImage;
    private BufferedImage backGroundImage;

    // Start/End screen images
    private BufferedImage backGroundStartImage;
    private BufferedImage backGroundEndImage;
    private BufferedImage startButtonImage;
    private BufferedImage restartButtonImage;
    private BufferedImage muteButtonImage;
    private BufferedImage titleImage;

    // Power-up items
    private BufferedImage bulletPiercingImage;
    private BufferedImage hpItemImage;
    private BufferedImage shieldItemImage;
    private BufferedImage piercingItemImage;
    private BufferedImage scoreItemImage;
    private BufferedImage tripleItemImage;
    private BufferedImage powerItemImage;

    // Maps cho enemies, bosses và bullets theo wave
    private Map<Integer, BufferedImage> enemyImages;
    private Map<Integer, BufferedImage> bossImages;
    private Map<Integer, BufferedImage> enemyBulletImages;
    private Map<Integer, BufferedImage> bossBulletImages;
    private Map<Integer, BufferedImage> backgroundImages;

    // Âm thanh
    private SoundManager soundManager;

    /**
     * Private constructor để implement Singleton
     */
    private AssetManager() {
        enemyImages = new HashMap<>();
        bossImages = new HashMap<>();
        enemyBulletImages = new HashMap<>();
        bossBulletImages = new HashMap<>();
        backgroundImages = new HashMap<>();
        soundManager = new SoundManager();
        loadAssets();
    }

    /**
     * Lấy instance duy nhất của AssetManager
     */
    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    /**
     * Load tất cả tài nguyên game
     */
    private void loadAssets() {
        loadImages();
        soundManager.loadSounds();
    }

    /**
     * Load hình ảnh
     */
    private void loadImages() {
        try {
            // Load hình ảnh chung
            playerImage = loadImage("/images/player.png");
            bulletImage = loadImage("/images/bullet.png");
            explosionImage = loadImage("/images/explosion.png");
            backGroundImage = loadImage("/images/backGround5.png");

            // Load Start/End screen images
            backGroundStartImage = loadImageSafe("/images/backGroundStart.png", 800, 600);
            backGroundEndImage = loadImageSafe("/images/backGroundEnd.png", 800, 600);
            startButtonImage = loadImageSafe("/images/start.png");
            restartButtonImage = loadImageSafe("/images/reStart.png");
            muteButtonImage = loadImageSafe("/images/mute.png");
            titleImage = loadImageSafe("/images/title.png");

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
                BufferedImage img = loadImageSafe("/images/enemy" + i + ".png");
                if (img != null) {
                    enemyImages.put(i, img);
                }
            }

            // Load bosses (1-5)
            for (int i = 1; i <= 5; i++) {
                BufferedImage img = loadImageSafe("/images/boss" + i + ".png");
                if (img != null) {
                    bossImages.put(i, img);
                }
            }

            // Load enemy bullets (1-5)
            for (int i = 1; i <= 5; i++) {
                BufferedImage img = loadImageSafe("/images/bulletenemy" + i + ".png");
                if (img != null) {
                    enemyBulletImages.put(i, img);
                }
            }

            // Load boss bullets (1-5)
            for (int i = 1; i <= 5; i++) {
                BufferedImage img = loadImageSafe("/images/bulletboss" + i + ".png");
                if (img != null) {
                    bossBulletImages.put(i, img);
                }
            }

            // Load backgrounds (1-5)
            for (int i = 1; i <= 5; i++) {
                BufferedImage img = loadImageSafe("/images/backGround" + i + ".png");
                if (img != null) {
                    backgroundImages.put(i, img);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi load hình ảnh: " + e.getMessage());
            createDefaultImages();
        }
    }

    /**
     * Load hình ảnh từ resources
     */
    private BufferedImage loadImage(String path) throws IOException {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new IOException("Không tìm thấy file: " + path);
        }
        return ImageIO.read(is);
    }

    /**
     * Load hình ảnh an toàn (không throw exception)
     */
    private BufferedImage loadImageSafe(String path) {
        try {
            return loadImage(path);
        } catch (Exception e) {
            System.err.println("Không tìm thấy " + path);
            return null;
        }
    }

    /**
     * Load hình ảnh an toàn với fallback
     */
    private BufferedImage loadImageSafe(String path, int width, int height) {
        try {
            return loadImage(path);
        } catch (Exception e) {
            System.err.println("Không tìm thấy " + path + " - using fallback");
            return createFallbackImage(width, height, java.awt.Color.DARK_GRAY);
        }
    }

    /**
     * Tạo hình ảnh mặc định nếu không load được file
     */
    private void createDefaultImages() {
        if (playerImage == null) {
            playerImage = new BufferedImage(40, 30, BufferedImage.TYPE_INT_RGB);
        }
        if (explosionImage == null) {
            explosionImage = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
        }
    }

    /**
     * Tạo hình ảnh fallback với màu cụ thể
     */
    private BufferedImage createFallbackImage(int width, int height, java.awt.Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return img;
    }

    // Getters cho hình ảnh
    public BufferedImage getPlayerImage() { return playerImage; }
    public BufferedImage getBulletImage() { return bulletImage; }
    public BufferedImage getExplosionImage() { return explosionImage; }
    public BufferedImage getBackGroundImage() { return backGroundImage; }
    public void setBackGroundImage(BufferedImage img) { this.backGroundImage = img; }
    
    public BufferedImage getBackGroundStartImage() { return backGroundStartImage; }
    public BufferedImage getBackGroundEndImage() { return backGroundEndImage; }
    public BufferedImage getStartButtonImage() { return startButtonImage; }
    public BufferedImage getRestartButtonImage() { return restartButtonImage; }
    public BufferedImage getMuteButtonImage() { return muteButtonImage; }
    public BufferedImage getTitleImage() { return titleImage; }
    
    public BufferedImage getBulletPiercingImage() { return bulletPiercingImage; }
    public BufferedImage getHpItemImage() { return hpItemImage; }
    public BufferedImage getShieldItemImage() { return shieldItemImage; }
    public BufferedImage getPiercingItemImage() { return piercingItemImage; }
    public BufferedImage getScoreItemImage() { return scoreItemImage; }
    public BufferedImage getTripleItemImage() { return tripleItemImage; }
    public BufferedImage getPowerItemImage() { return powerItemImage; }

    /**
     * Lấy hình ảnh enemy theo wave (cycle 1-5)
     */
    public BufferedImage getEnemyImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return enemyImages.getOrDefault(imageIndex, null);
    }

    /**
     * Lấy hình ảnh boss theo wave (cycle 1-5)
     */
    public BufferedImage getBossImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return bossImages.getOrDefault(imageIndex, null);
    }

    /**
     * Lấy hình ảnh đạn enemy theo wave (cycle 1-5)
     */
    public BufferedImage getEnemyBulletImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return enemyBulletImages.getOrDefault(imageIndex, null);
    }

    /**
     * Lấy hình ảnh đạn boss theo wave (cycle 1-5)
     */
    public BufferedImage getBossBulletImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return bossBulletImages.getOrDefault(imageIndex, null);
    }

    /**
     * Lấy hình ảnh background theo wave (cycle 1-5)
     */
    public BufferedImage getBackgroundImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        BufferedImage bg = backgroundImages.getOrDefault(imageIndex, null);
        return bg != null ? bg : backGroundImage;
    }

    /**
     * Lấy SoundManager
     */
    public SoundManager getSoundManager() {
        return soundManager;
    }
}

/**
 * Lớp quản lý âm thanh - tách riêng từ AssetManager
 */
class SoundManager {
    private Clip shootSound;
    private Clip explosionSound;
    private Clip backgroundMusic;
    private Clip buttonSound;
    private Clip deathSound;
    private Clip gameOverSound;
    
    private boolean soundEnabled;

    public SoundManager() {
        this.soundEnabled = true;
    }

    /**
     * Load tất cả âm thanh
     */
    public void loadSounds() {
        try {
            shootSound = loadSoundSafe("/sounds/bullet.wav", "/sounds/shoot.wav");
            explosionSound = loadSoundSafe("/sounds/explosion.wav");
            backgroundMusic = loadSoundSafe("/sounds/background.wav");
            buttonSound = loadSoundSafe("/sounds/button.wav");
            deathSound = loadSoundSafe("/sounds/death.wav");
            gameOverSound = loadSoundSafe("/sounds/gameOver.wav");
        } catch (Exception e) {
            System.err.println("Lỗi load âm thanh: " + e.getMessage());
        }
    }

    /**
     * Load âm thanh từ resources
     */
    private Clip loadSound(String path) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new IOException("Không tìm thấy file: " + path);
        }

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        return clip;
    }

    /**
     * Load âm thanh an toàn với fallback
     */
    private Clip loadSoundSafe(String... paths) {
        for (String path : paths) {
            try {
                return loadSound(path);
            } catch (Exception e) {
                System.err.println("Không load được " + path);
            }
        }
        return null;
    }

    // Sound control methods
    public void toggleSound() {
        soundEnabled = !soundEnabled;
        if (!soundEnabled) {
            stopBackgroundMusic();
        } else {
            playBackgroundMusic();
        }
        System.out.println("[SOUND] Sound " + (soundEnabled ? "ENABLED" : "DISABLED"));
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        if (!soundEnabled) {
            stopBackgroundMusic();
        }
    }

    public void playShootSound() {
        playSound(shootSound);
    }

    public void playExplosionSound() {
        playSound(explosionSound);
    }

    public void playButtonSound() {
        playSound(buttonSound);
    }

    public void playDeathSound() {
        playSound(deathSound);
    }

    public void playGameOverSound() {
        playSound(gameOverSound);
    }

    public void playBackgroundMusic() {
        if (soundEnabled && backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    /**
     * Helper method để phát âm thanh
     */
    private void playSound(Clip clip) {
        if (soundEnabled && clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
}