package main;

import java.awt.*;

/**
 * Lớp Bullet - viên đạn với power-ups và hỗ trợ boss patterns
 */
public class Bullet {
    private double x, y; // Dùng double cho smooth movement
    private int width = 20;
    private int height = 25;
    private int speed = 7; // Tốc độ cơ bản
    private int direction; // 1 = xuống, -1 = lên
    private boolean active = true;
    private int damage = 1;

    // Power-up properties
    private boolean piercing = false; // Đạn xuyên không bị xóa khi trúng
    private int hitCount = 0; // Số lần trúng (cho đạn xuyên)
    private static final int MAX_PIERCING_HITS = 3; // Đạn xuyên tối đa 3 lần

    // Cho circular shooting của Boss
    private double velX = 0;
    private double velY = 0;
    private boolean useVelocity = false;

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
     * Constructor cho player với power-ups (triple shot + piercing)
     * speedX: tốc độ ngang cho triple shot (âm = trái, dương = phải)
     */
    public Bullet(int x, int y, int directionY, int speedX, boolean piercing) {
        this.x = x;
        this.y = y;
        this.direction = directionY;
        this.piercing = piercing;

        // Tính velocity từ speedX và direction
        this.velX = speedX;
        this.velY = directionY * speed;
        this.useVelocity = (speedX != 0); // Chỉ dùng velocity nếu có speedX
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
     * Set tốc độ đạn (tăng theo wave)
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void update() {
        if (useVelocity) {
            // Di chuyển theo velocity (cho Boss circular pattern hoặc triple shot)
            x += velX;
            y += velY;
        } else {
            // Di chuyển bình thường
            y += speed * direction;
        }

        // Deactivate nếu ra khỏi màn hình
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
                active = false; // Đạn xuyên hết hiệu lực sau 3 lần trúng
            }
        } else {
            active = false; // Đạn thường biến mất ngay
        }
    }

    public void draw(Graphics2D g2d) {
        if (piercing) {
            // Đạn xuyên có màu đặc biệt (cyan)
            if (Assets.bulletPiercingImage != null) {
                // Vẽ với tint cyan
//                g2d.setColor(new Color(0, 255, 255, 200));
//                g2d.fillRect((int)x - 2, (int)y - 2, width + 4, height + 4);
                g2d.drawImage(Assets.bulletPiercingImage, (int)x, (int)y, width, height, null);
            } else {
                g2d.setColor(Color.CYAN);
                g2d.fillRect((int)x - 2, (int)y, width + 4, height);

                // Vẽ hiệu ứng sáng
                g2d.setColor(new Color(100, 255, 255, 150));
                g2d.fillRect((int)x - 1, (int)y, width + 2, height);
            }

            // Vẽ trail effect
            g2d.setColor(new Color(0, 200, 255, 100));
            g2d.fillRect((int)x, (int)y + height, width, 5);
        } else {
            // Đạn thường
            if (Assets.bulletImage != null) {
                g2d.drawImage(Assets.bulletImage, (int)x, (int)y, width, height, null);
            } else {
                g2d.setColor(useVelocity && direction > 0 ? Color.ORANGE : Color.YELLOW);
                // Boss bullets màu cam (direction > 0 và useVelocity)
                g2d.fillRect((int)x, (int)y, width, height);
            }
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
}
