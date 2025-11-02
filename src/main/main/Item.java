package main;

import java.awt.*;

/**
 * Lớp Item - vật phẩm rơi khi địch chết
 */
public class Item {
    private int x, y;
    private int width = 25;
    private int height = 25;
    private int speed = 3;
    private ItemType type;
    private boolean active = true;
    
    public enum ItemType {
        HEALTH,     // Hồi máu
        POWERUP,    // Tăng sức mạnh
        SCORE       // Điểm thưởng
    }
    
    public Item(int x, int y, ItemType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    
    public void update() {
        y += speed;
        if (y > GamePanel.HEIGHT) {
            active = false;
        }
    }
    
    public void draw(Graphics2D g2d) {
        Color color;
        switch (type) {
            case HEALTH:
                color = Color.GREEN;
                break;
            case POWERUP:
                color = Color.BLUE;
                break;
            case SCORE:
                color = Color.YELLOW;
                break;
            default:
                color = Color.WHITE;
        }
        
        g2d.setColor(color);
        g2d.fillOval(x, y, width, height);
        g2d.setColor(Color.WHITE);
        g2d.drawOval(x, y, width, height);
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
    
    public ItemType getType() {
        return type;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
}

