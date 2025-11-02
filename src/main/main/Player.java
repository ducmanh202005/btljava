package main;

import java.awt.*;
import java.awt.AlphaComposite;

/**
 * Lớp Player - tàu người chơi với power-ups
 */
public class Player {
    private int x, y;
    private int width = 80;
    private int height = 80;
    private int speed = 5;
    private boolean active = true;

    // Invincibility frames - nhấp nháy khi bị trúng
    private boolean invincible = false;
    private int invincibilityFrames = 0;
    private static final int INVINCIBILITY_DURATION = 120; // 2 giây ở 60 FPS
    private int blinkCounter = 0;

    // Power-up states
    private boolean tripleShot = false;
    private int tripleShotDuration = 0;
    private static final int TRIPLE_SHOT_TIME = 600; // 10 giây

    private boolean piercing = false;
    private int piercingDuration = 0;
    private static final int PIERCING_TIME = 600; // 10 giây

    // Shield system
    private boolean hasShield = false;
    private int shieldDuration = 0;
    private static final int SHIELD_TIME = 900; // 15 giây
    private int shieldBlinkCounter = 0;

    // Shoot cooldown
    private int shootCooldown = 0;
    private static final int SHOOT_DELAY = 10; // frames giữa các lần bắn

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        // Cập nhật invincibility frames
        if (invincible) {
            invincibilityFrames--;
            blinkCounter++;

            if (invincibilityFrames <= 0) {
                invincible = false;
                blinkCounter = 0;
            }
        }

        // Cập nhật power-up durations
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

        // Cập nhật shoot cooldown
        if (shootCooldown > 0) {
            shootCooldown--;
        }
    }

    /**
     * Kích hoạt triple shot power-up
     */
    public void activateTripleShot() {
        tripleShot = true;
        tripleShotDuration = TRIPLE_SHOT_TIME;
    }

    /**
     * Kích hoạt piercing bullet power-up
     */
    public void activatePiercing() {
        piercing = true;
        piercingDuration = PIERCING_TIME;
    }

    /**
     * Kích hoạt shield
     */
    public void activateShield() {
        hasShield = true;
        shieldDuration = SHIELD_TIME;
        shieldBlinkCounter = 0;
    }

    /**
     * Xử lý va chạm - mất shield trước, sau đó mới mất máu
     */
    public boolean takeDamage() {
        if (invincible) {
            return false; // Không nhận damage khi đang invincible
        }

        if (hasShield) {
            // Mất shield thay vì mất máu
            hasShield = false;
            shieldDuration = 0;
            // Tạo invincibility ngắn sau khi mất shield
            invincible = true;
            invincibilityFrames = 60; // 1 giây invincibility
            blinkCounter = 0;
            return false; // Không mất máu
        } else {
            // Không có shield, nhận damage thật
            invincible = true;
            invincibilityFrames = INVINCIBILITY_DURATION;
            blinkCounter = 0;
            return true; // Mất máu
        }
    }

    /**
     * Kiểm tra xem player có đang invincible không
     */
    public boolean isInvincible() {
        return invincible;
    }

    public boolean hasShield() {
        return hasShield;
    }

    public void draw(Graphics2D g2d) {
        // Nhấp nháy khi invincible
        if (invincible) {
            if (blinkCounter % 10 < 5) {
                if (Assets.playerImage != null) {
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                    g2d.setComposite(alpha);
                    g2d.drawImage(Assets.playerImage, x, y, width, height, null);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                } else {
                    g2d.setColor(Color.RED);
                    g2d.fillRect(x, y, width, height);
                }
            }
        } else {
            // Vẽ player bình thường
            if (Assets.playerImage != null) {
                g2d.drawImage(Assets.playerImage, x, y, width, height, null);
            } else {
                g2d.setColor(Color.GREEN);
                g2d.fillRect(x, y, width, height);
            }
        }

        // Vẽ shield nếu có
        if (hasShield) {
            // Hiệu ứng nhấp nháy khi shield sắp hết (3 giây cuối)
            boolean drawShield = true;
            if (shieldDuration < 180) { // 3 giây cuối
                drawShield = (shieldBlinkCounter % 20 < 10);
            }

            if (drawShield) {
                // Vẽ vòng tròn shield xung quanh player
                g2d.setColor(new Color(100, 200, 255, 150));
                g2d.setStroke(new BasicStroke(3));
                int shieldRadius = (int)(Math.max(width, height) * 0.7);
                g2d.drawOval(x + width/2 - shieldRadius, y + height/2 - shieldRadius,
                        shieldRadius * 2, shieldRadius * 2);

                // Vẽ thêm vòng tròn bên trong
                g2d.setColor(new Color(150, 220, 255, 80));
                int innerRadius = (int)(shieldRadius * 0.85);
                g2d.drawOval(x + width/2 - innerRadius, y + height/2 - innerRadius,
                        innerRadius * 2, innerRadius * 2);
            }
        }

        // Vẽ chỉ báo power-ups
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
        if (x > 0) {
            x -= speed;
        }
    }

    public void moveRight() {
        if (x < GamePanel.WIDTH - width) {
            x += speed;
        }
    }

    public void moveUp() {
        if (y > GamePanel.HEIGHT / 2) { // Giới hạn không cho lên quá nửa màn hình
            y -= speed;
        }
    }

    public void moveDown() {
        if (y < GamePanel.HEIGHT - height - 10) {
            y += speed;
        }
    }

    /**
     * Bắn đạn với power-ups
     */
    public void shoot(java.util.List<Bullet> bullets) {
        if (shootCooldown > 0) {
            return; // Chưa thể bắn
        }

        int centerX = x + width / 2 - 10; // Căn giữa đạn với tàu
        int bulletY = y;

        if (tripleShot) {
            // Bắn 3 viên: thẳng, chéo trái, chéo phải
            bullets.add(new Bullet(centerX, bulletY, -1, 0, piercing)); // Thẳng
            bullets.add(new Bullet(centerX - 15, bulletY, -1, -3, piercing)); // Chéo trái
            bullets.add(new Bullet(centerX + 15, bulletY, -1, 3, piercing)); // Chéo phải
        } else {
            // Bắn 1 viên bình thường (tương thích với constructor cũ)
            if (piercing) {
                bullets.add(new Bullet(centerX, bulletY, -1, 0, piercing));
            } else {
                bullets.add(new Bullet(centerX, bulletY, -1)); // Constructor cũ
            }
        }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Getters
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