package main;

import java.awt.*;

/**
 * Lớp Bullet - đạn bắn
 * (Placeholder - sẽ được các thành viên khác sử dụng)
 */
public class Bullet {
    private double x, y; // Dùng double cho smooth movement
    private int width = 20;
    private int height = 25;
    private int speed = 7; // Tốc độ cơ bản, có thể tăng theo level
    private int direction; // 1 = xuống, -1 = lên
    private boolean active = true;
    private int damage = 1;
    
    // Cho circular shooting của Boss
    private double velX = 0;
    private double velY = 0;
    private boolean useVelocity = false;
    
    public Bullet(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
    
    public Bullet(int x, int y, int direction, int damage) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.damage = damage;
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
            // Di chuyển theo velocity (cho Boss circular pattern)
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
    
    public void draw(Graphics2D g2d) {
        // Vẽ bullet đơn giản
        if(Assets.bulletImage!=null) {
            g2d.drawImage(Assets.bulletImage, (int)x, (int)y, width, height, null);
        }else{
            g2d.setColor(useVelocity ? Color.ORANGE : Color.YELLOW); // Boss bullets màu cam
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
    
    // Getters
    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getDirection() { return direction; }
    public int getDamage() { return damage; }
}
