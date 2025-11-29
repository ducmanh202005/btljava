package spaceinvaders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;


public class AssetManager {
    private static AssetManager instance;

    private BufferedImage playerImage;
    private BufferedImage bulletImage;
    private BufferedImage explosionImage;
    private BufferedImage backGroundImage;

    private BufferedImage backGroundStartImage;
    private BufferedImage backGroundEndImage;
    private BufferedImage startButtonImage;
    private BufferedImage restartButtonImage;
    private BufferedImage muteButtonImage;
    private BufferedImage titleImage;

    private BufferedImage bulletPiercingImage;
    private BufferedImage hpItemImage;
    private BufferedImage shieldItemImage;
    private BufferedImage piercingItemImage;
    private BufferedImage scoreItemImage;
    private BufferedImage tripleItemImage;
    private BufferedImage powerItemImage;

    private Map<Integer, BufferedImage> enemyImages;
    private Map<Integer, BufferedImage> bossImages;
    private Map<Integer, BufferedImage> enemyBulletImages;
    private Map<Integer, BufferedImage> bossBulletImages;
    private Map<Integer, BufferedImage> backgroundImages;

    private SoundManager soundManager;

    private AssetManager() {
        enemyImages = new HashMap<>();
        bossImages = new HashMap<>();
        enemyBulletImages = new HashMap<>();
        bossBulletImages = new HashMap<>();
        backgroundImages = new HashMap<>();
        soundManager = new SoundManager();
        loadAssets();
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    private void loadAssets() {
        loadImages();
        soundManager.loadSounds();
    }

    private void loadImages() {
        try {
            playerImage = loadImage("/images/player.png");
            bulletImage = loadImage("/images/bullet.png");
            explosionImage = loadImage("/images/explosion.png");
            backGroundImage = loadImage("/images/backGround5.png");

            backGroundStartImage = loadImageSafe("/images/backGroundStart.png", 800, 600);
            backGroundEndImage = loadImageSafe("/images/backGroundEnd.png", 800, 600);
            startButtonImage = loadImageSafe("/images/start.png");
            restartButtonImage = loadImageSafe("/images/reStart.png");
            muteButtonImage = loadImageSafe("/images/mute.png");
            titleImage = loadImageSafe("/images/title.png");

            bulletPiercingImage = loadImage("/images/bulletpiercing.png");
            hpItemImage = loadImage("/images/hpitem.png");
            shieldItemImage = loadImage("/images/shielditem.png");
            piercingItemImage = loadImage("/images/piercingitem.png");
            scoreItemImage = loadImage("/images/scoreitem.png");
            tripleItemImage = loadImage("/images/tripleitem.png");
            powerItemImage = loadImage("/images/poweritem.png");

            for (int i = 1; i <= 5; i++) {
                BufferedImage img = loadImageSafe("/images/enemy" + i + ".png");
                if (img != null) {
                    enemyImages.put(i, img);
                }
            }

            for (int i = 1; i <= 5; i++) {
                BufferedImage img = loadImageSafe("/images/boss" + i + ".png");
                if (img != null) {
                    bossImages.put(i, img);
                }
            }

            for (int i = 1; i <= 5; i++) {
                BufferedImage img = loadImageSafe("/images/bulletenemy" + i + ".png");
                if (img != null) {
                    enemyBulletImages.put(i, img);
                }
            }

            for (int i = 1; i <= 5; i++) {
                BufferedImage img = loadImageSafe("/images/bulletboss" + i + ".png");
                if (img != null) {
                    bossBulletImages.put(i, img);
                }
            }

            for (int i = 1; i <= 5; i++) {
                BufferedImage img = loadImageSafe("/images/backGround" + i + ".png");
                if (img != null) {
                    backgroundImages.put(i, img);
                }
            }
        } catch (Exception e) {
            System.err.println("Lá»—i load hÃ¬nh áº£nh: " + e.getMessage());
            createDefaultImages();
        }
    }

    private BufferedImage loadImage(String path) throws IOException {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new IOException("KhÃ´ng tÃ¬m tháº¥y file: " + path);
        }
        return ImageIO.read(is);
    }

    private BufferedImage loadImageSafe(String path) {
        try {
            return loadImage(path);
        } catch (Exception e) {
            System.err.println("KhÃ´ng tÃ¬m tháº¥y " + path);
            return null;
        }
    }

    private BufferedImage loadImageSafe(String path, int width, int height) {
        try {
            return loadImage(path);
        } catch (Exception e) {
            System.err.println("KhÃ´ng tÃ¬m tháº¥y " + path + " - using fallback");
            return createFallbackImage(width, height, java.awt.Color.DARK_GRAY);
        }
    }

    private void createDefaultImages() {
        if (playerImage == null) {
            playerImage = new BufferedImage(40, 30, BufferedImage.TYPE_INT_RGB);
        }
        if (explosionImage == null) {
            explosionImage = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
        }
    }

    private BufferedImage createFallbackImage(int width, int height, java.awt.Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return img;
    }

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

    public BufferedImage getEnemyImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return enemyImages.getOrDefault(imageIndex, null);
    }

    public BufferedImage getBossImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return bossImages.getOrDefault(imageIndex, null);
    }

    public BufferedImage getEnemyBulletImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return enemyBulletImages.getOrDefault(imageIndex, null);
    }

    public BufferedImage getBossBulletImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        return bossBulletImages.getOrDefault(imageIndex, null);
    }

    public BufferedImage getBackgroundImage(int wave) {
        int imageIndex = ((wave - 1) % 5) + 1;
        BufferedImage bg = backgroundImages.getOrDefault(imageIndex, null);
        return bg != null ? bg : backGroundImage;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }
}

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

    public void loadSounds() {
        try {
            shootSound = loadSoundSafe("/sounds/bullet.wav", "/sounds/shoot.wav");
            explosionSound = loadSoundSafe("/sounds/explosion.wav");
            backgroundMusic = loadSoundSafe("/sounds/background.wav");
            buttonSound = loadSoundSafe("/sounds/button.wav");
            deathSound = loadSoundSafe("/sounds/death.wav");
            gameOverSound = loadSoundSafe("/sounds/gameOver.wav");
        } catch (Exception e) {
            System.err.println("Lá»—i load Ã¢m thanh: " + e.getMessage());
        }
    }

    private Clip loadSound(String path) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new IOException("KhÃ´ng tÃ¬m tháº¥y file: " + path);
        }

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        return clip;
    }

    private Clip loadSoundSafe(String... paths) {
        for (String path : paths) {
            try {
                return loadSound(path);
            } catch (Exception e) {
                System.err.println("KhÃ´ng load Ä'Æ°á»£c " + path);
            }
        }
        return null;
    }

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

    private void playSound(Clip clip) {
        if (soundEnabled && clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
}