package main;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Lớp Bullet - UPDATED với wave-specific bullets
 */
public class Bullet {
    private double x, y;
    private int width = 25;
    private int height = 35;
    private int speed = 7;
    private int direction; // 1 = xuống, -1 = lên
    private boolean active = true;
    private int damage = 1;

    // Power-up properties
    private boolean piercing = false;
    private int hitCount = 0;
    private static final int MAX_PIERCING_HITS = 3;

    // Cho circular shooting của Boss
    private double velX = 0;
    private double velY = 0;
    private boolean useVelocity = false;
    
    // ★ NEW: Wave và Boss tracking
    private int wave = 1;
    private boolean isBoss = false;

    /**
     * Constructor cơ bản (tương thích code cũ)
     */
    public Bullet(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.piercing = false;
    }

    /**
     * Constructor với damage
     */
    public Bullet(int x, int y, int direction, int damage) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.damage = damage;
        this.piercing = false;
    }
    
    /**
     * ★ NEW: Constructor với wave và boss info (cho enemy/boss bullets)
     */
    public Bullet(int x, int y, int direction, int damage, int wave, boolean isBoss) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.damage = damage;
        this.wave = wave;
        this.isBoss = isBoss;
        this.piercing = false;
    }

    /**
     * Constructor cho player với power-ups (triple shot + piercing)
     */
    public Bullet(int x, int y, int directionY, int speedX, boolean piercing) {
        this.x = x;
        this.y = y;
        this.direction = directionY;
        this.piercing = piercing;

        this.velX = speedX;
        this.velY = directionY * speed;
        this.useVelocity = (speedX != 0);
    }

    /**
     * Set velocity cho circular pattern (Boss)
     */
    public void setVelocity(double velX, double velY) {
        this.velX = velX;
        this.velY = velY;
        this.useVelocity = true;
    }

    /**
     * Set tốc độ đạn
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    /**
     * ★ Set wave info (dùng khi tạo bullet từ constructor cũ)
     */
    public void setWaveInfo(int wave, boolean isBoss) {
        this.wave = wave;
        this.isBoss = isBoss;
    }

    public void update() {
        if (useVelocity) {
            x += velX;
            y += velY;
        } else {
            y += speed * direction;
        }

        if (y < -50 || y > GamePanel.HEIGHT + 50 || x < -50 || x > GamePanel.WIDTH + 50) {
            active = false;
        }
    }

    /**
     * Xử lý khi đạn trúng mục tiêu
     */
    public void onHit() {
        if (piercing) {
            hitCount++;
            if (hitCount >= MAX_PIERCING_HITS) {
                active = false;
            }
        } else {
            active = false;
        }
    }

    public void draw(Graphics2D g2d) {
        if (piercing) {
            // Đạn xuyên (player)
            if (Assets.bulletPiercingImage != null) {
                g2d.drawImage(Assets.bulletPiercingImage, (int)x, (int)y, width, height, null);
            } else {
                g2d.setColor(Color.CYAN);
                g2d.fillRect((int)x - 2, (int)y, width + 4, height);
                g2d.setColor(new Color(100, 255, 255, 150));
                g2d.fillRect((int)x - 1, (int)y, width + 2, height);
            }
            g2d.setColor(new Color(0, 200, 255, 100));
            g2d.fillRect((int)x, (int)y + height, width, 5);
        } else if (direction > 0) {
            // ★ Đạn enemy/boss (direction > 0 = đi xuống)
            BufferedImage bulletImg = null;
            
            if (isBoss) {
                bulletImg = Assets.getBossBulletImage(wave);
            } else {
                bulletImg = Assets.getEnemyBulletImage(wave);
            }
            
            if (bulletImg != null) {
                g2d.drawImage(bulletImg, (int)x, (int)y, width, height, null);
            } else {
                // Fallback color
                g2d.setColor(isBoss ? Color.ORANGE : Color.RED);
                g2d.fillRect((int)x, (int)y, width, height);
            }
        } else {
            // Đạn player thường (direction < 0 = đi lên)
            g2d.setColor(Color.YELLOW);
            g2d.fillRect((int)x, (int)y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPiercing() {
        return piercing;
    }

    // Getters
    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getDirection() { return direction; }
    public int getDamage() { return damage; }
    public int getWave() { return wave; }
    public boolean isBossBullet() { return isBoss; }
}