package main;

import java.awt.*;

/**
 * Lớp Player - tàu người chơi
 * (Placeholder - sẽ được Thành viên 2 implement chi tiết)
 */
public class Player {
    private int x, y;
    private int width = 40;
    private int height = 40;
    private int speed = 5;
    private boolean active = true;
    
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void update() {
        // Logic cập nhật player sẽ được implement bởi Thành viên 2
    }

    public void draw(Graphics2D g2d) {
        // Thay vì vẽ hình chữ nhật, hãy vẽ hình ảnh từ lớp Assets
        if (Assets.playerImage != null) {
            g2d.drawImage(Assets.playerImage, x, y, width, height, null);
        } else {
            // Dự phòng: nếu ảnh không load được, vẫn vẽ hình chữ nhật
            g2d.setColor(Color.GREEN);
            g2d.fillRect(x, y, width, height);
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
        x = GamePanel.WIDTH / 2;
        y = GamePanel.HEIGHT - 50;
        active = true;
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
