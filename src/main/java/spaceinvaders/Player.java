// ==================== Player.java ====================
package spaceinvaders;
import java.awt.*;
import java.awt.image.BufferedImage; // ⭐ THÊM IMPORT NÀY

/**
 * Lớp Player - tàu người chơi với power-ups và âm thanh
 * REFACTORED: Sử dụng AssetManager thay vì Assets static
 */
public class Player {
    private int x, y;
    private int width = 80;
    private int height = 80;
    private int speed = 5;
    private boolean active = true;

    // Asset manager reference
    private AssetManager assetManager;

    // Invincibility frames - nhấp nháy khi bị trúng
    private boolean invincible = false;
    private int invincibilityFrames = 0;
    private static final int INVINCIBILITY_DURATION = 120;
    private int blinkCounter = 0;

    // Power-up states
    private boolean tripleShot = false;
    private int tripleShotDuration = 0;
    private static final int TRIPLE_SHOT_TIME = 600;

    private boolean piercing = false;
    private int piercingDuration = 0;
    private static final int PIERCING_TIME = 600;

    // Shield system
    private boolean hasShield = false;
    private int shieldDuration = 0;
    private static final int SHIELD_TIME = 900;
    private int shieldBlinkCounter = 0;

    // Shoot cooldown
    private int shootCooldown = 0;
    private static final int SHOOT_DELAY = 10;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.assetManager = AssetManager.getInstance();
    }

    public void update() {
        if (invincible) {
            invincibilityFrames--;
            blinkCounter++;
            if (invincibilityFrames <= 0) {
                invincible = false;
                blinkCounter = 0;
            }
        }

        if (tripleShot) {
            tripleShotDuration--;
            if (tripleShotDuration <= 0) {
                tripleShot = false;
            }
        }

        if (piercing) {
            piercingDuration--;
            if (piercingDuration <= 0) {
                piercing = false;
            }
        }

        if (hasShield) {
            shieldDuration--;
            shieldBlinkCounter++;
            if (shieldDuration <= 0) {
                hasShield = false;
                shieldBlinkCounter = 0;
            }
        }

        if (shootCooldown > 0) {
            shootCooldown--;
        }
    }

    public void activateTripleShot() {
        tripleShot = true;
        tripleShotDuration = TRIPLE_SHOT_TIME;
    }

    public void activatePiercing() {
        piercing = true;
        piercingDuration = PIERCING_TIME;
    }

    public void activateShield() {
        hasShield = true;
        shieldDuration = SHIELD_TIME;
        shieldBlinkCounter = 0;
    }

    public boolean takeDamage() {
        if (invincible) {
            return false;
        }

        if (hasShield) {
            hasShield = false;
            shieldDuration = 0;
            invincible = true;
            invincibilityFrames = 60;
            blinkCounter = 0;
            return false;
        } else {
            invincible = true;
            invincibilityFrames = INVINCIBILITY_DURATION;
            blinkCounter = 0;
            return true;
        }
    }

    public boolean isInvincible() {
        return invincible;
    }

    public boolean hasShield() {
        return hasShield;
    }

    public void draw(Graphics2D g2d) {
        BufferedImage playerImg = assetManager.getPlayerImage();
        
        if (invincible) {
            if (blinkCounter % 10 < 5) {
                if (playerImg != null) {
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                    g2d.setComposite(alpha);
                    g2d.drawImage(playerImg, x, y, width, height, null);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                } else {
                    g2d.setColor(Color.RED);
                    g2d.fillRect(x, y, width, height);
                }
            }
        } else {
            if (playerImg != null) {
                g2d.drawImage(playerImg, x, y, width, height, null);
            } else {
                g2d.setColor(Color.GREEN);
                g2d.fillRect(x, y, width, height);
            }
        }

        if (hasShield) {
            boolean drawShield = true;
            if (shieldDuration < 180) {
                drawShield = (shieldBlinkCounter % 20 < 10);
            }

            if (drawShield) {
                g2d.setColor(new Color(100, 200, 255, 150));
                g2d.setStroke(new BasicStroke(3));
                int shieldRadius = (int)(Math.max(width, height) * 0.7);
                g2d.drawOval(x + width/2 - shieldRadius, y + height/2 - shieldRadius,
                        shieldRadius * 2, shieldRadius * 2);

                g2d.setColor(new Color(150, 220, 255, 80));
                int innerRadius = (int)(shieldRadius * 0.85);
                g2d.drawOval(x + width/2 - innerRadius, y + height/2 - innerRadius,
                        innerRadius * 2, innerRadius * 2);
            }
        }

        int indicatorY = y - 15;
        if (tripleShot) {
            g2d.setColor(Color.YELLOW);
            g2d.fillRect(x, indicatorY, width/3 - 2, 5);
        }
        if (piercing) {
            g2d.setColor(Color.CYAN);
            g2d.fillRect(x + width/3 + 1, indicatorY, width/3 - 2, 5);
        }
    }

    public void moveLeft() {
        if (x > 0) x -= speed;
    }

    public void moveRight() {
        if (x < GamePanel.WIDTH - width) x += speed;
    }

    public void moveUp() {
        if (y > GamePanel.HEIGHT / 2) y -= speed;
    }

    public void moveDown() {
        if (y < GamePanel.HEIGHT - height - 10) y += speed;
    }

    public void shoot(java.util.List<Bullet> bullets) {
        if (shootCooldown > 0) return;

        int centerX = x + width / 2 - 10;
        int bulletY = y;

        if (tripleShot) {
            bullets.add(new Bullet(centerX, bulletY, -1, 0, piercing));
            bullets.add(new Bullet(centerX - 15, bulletY, -1, -3, piercing));
            bullets.add(new Bullet(centerX + 15, bulletY, -1, 3, piercing));
        } else {
            if (piercing) {
                bullets.add(new Bullet(centerX, bulletY, -1, 0, piercing));
            } else {
                bullets.add(new Bullet(centerX, bulletY, -1));
            }
        }

        assetManager.getSoundManager().playShootSound();
        shootCooldown = SHOOT_DELAY;
    }

    public void reset() {
        x = GamePanel.WIDTH / 2 - 40;
        y = GamePanel.HEIGHT - 130;
        active = true;
        invincible = false;
        invincibilityFrames = 0;
        blinkCounter = 0;
        tripleShot = false;
        tripleShotDuration = 0;
        piercing = false;
        piercingDuration = 0;
        hasShield = false;
        shieldDuration = 0;
        shieldBlinkCounter = 0;
        shootCooldown = 0;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean hasTripleShot() { return tripleShot; }
    public boolean hasPiercing() { return piercing; }
    public int getTripleShotDuration() { return tripleShotDuration; }
    public int getPiercingDuration() { return piercingDuration; }
    public int getShieldDuration() { return shieldDuration; }
}