package main;

import java.awt.*;
import java.awt.AlphaComposite;

/**
 * Lớp Player - tàu người chơi
 * (Placeholder - sẽ được Thành viên 2 implement chi tiết)
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
    private int blinkCounter = 0; // Đếm frame để nhấp nháy
    
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
    }
    
    /**
     * Kích hoạt invincibility khi bị trúng
     */
    public void takeDamage() {
        if (!invincible) {
            invincible = true;
            invincibilityFrames = INVINCIBILITY_DURATION;
            blinkCounter = 0;
        }
    }
    
    /**
     * Kiểm tra xem player có đang invincible không
     */
    public boolean isInvincible() {
        return invincible;
    }

    public void draw(Graphics2D g2d) {
        // Nhấp nháy khi invincible: vẽ mỗi 5 frames (tạo hiệu ứng nhấp nháy)
        if (invincible) {
            // Chỉ vẽ khi blinkCounter chia hết cho 5 (nhấp nháy)
            if (blinkCounter % 10 < 5) {
                // Vẽ player với độ trong suốt khi bị trúng
                if (Assets.playerImage != null) {
                    // Vẽ với alpha để tạo hiệu ứng
                    AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                    g2d.setComposite(alpha);
                    g2d.drawImage(Assets.playerImage, x, y, width, height, null);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                } else {
                    // Dự phòng: vẽ màu đỏ nhấp nháy
                    g2d.setColor(Color.RED);
                    g2d.fillRect(x, y, width, height);
                }
            }
            // Nếu không trong frame vẽ, không vẽ gì (tạo hiệu ứng nhấp nháy)
        } else {
            // Vẽ bình thường khi không invincible
            if (Assets.playerImage != null) {
                g2d.drawImage(Assets.playerImage, x, y, width, height, null);
            } else {
                // Dự phòng: nếu ảnh không load được, vẫn vẽ hình chữ nhật
                g2d.setColor(Color.GREEN);
                g2d.fillRect(x, y, width, height);
            }
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
    
    public void shoot(java.util.List<Bullet> bullets) {
        // Logic bắn đạn sẽ được implement bởi Thành viên 2
        bullets.add(new Bullet(x + width/2, y, -1)); // -1 = đạn đi lên
    }
    
    public void reset() {
        x = GamePanel.WIDTH / 2-40;
        y = GamePanel.HEIGHT - 130;
        active = true;
        invincible = false;
        invincibilityFrames = 0;
        blinkCounter = 0;
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
}
