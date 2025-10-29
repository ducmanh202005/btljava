package main;

import java.awt.*;

/**
 * Lớp Enemy - kẻ địch
 * (Placeholder - sẽ được Thành viên 3 implement chi tiết)
 */
public class Enemy {
    private int x, y;
    private int width = 30;
    private int height = 30;
    private int speed = 1;
    private boolean active = true;
    private int direction = 1; // 1 = phải, -1 = trái
    
    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void update() {
        // Logic cập nhật enemy sẽ được implement bởi Thành viên 3
        x += speed * direction;
        
        // Đổi hướng khi chạm biên
        if (x <= 0 || x >= GamePanel.WIDTH - width) {
            direction *= -1;
            y += 20; // Di chuyển xuống
        }
    }
    
    public void draw(Graphics2D g2d) {
        // Vẽ enemy đơn giản
        if(Assets.enemyImage != null){
            g2d.drawImage(Assets.enemyImage,x,y,width,height,null);
        }else {
            g2d.setColor(Color.RED);
            g2d.fillRect(x, y, width, height);
        }
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
