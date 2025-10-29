package main;

import java.awt.*;

/**
 * Lớp Bullet - đạn bắn
 * (Placeholder - sẽ được các thành viên khác sử dụng)
 */
public class Bullet {
    private int x, y;
    private int width = 4;
    private int height = 10;
    private int speed = 7;
    private int direction; // 1 = xuống, -1 = lên
    private boolean active = true;
    
    public Bullet(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
    
    public void update() {
        y += speed * direction;
        
        // Deactivate nếu ra khỏi màn hình
        if (y < 0 || y > GamePanel.HEIGHT) {
            active = false;
        }
    }
    
    public void draw(Graphics2D g2d) {
        // Vẽ bullet đơn giản
        if(Assets.bulletImage!=null) {
            g2d.drawImage(Assets.bulletImage, x, y, width, height, null);
        }else{
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(x, y, width, height);}
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
    public int getDirection() { return direction; }
}
