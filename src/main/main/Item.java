package main;

import java.awt.*;

/**
 * Lớp Item - các power-up rơi xuống
 */
public class Item {
    // Các loại itemå
    public enum ItemType {
        HEALTH,       // Hồi máu (cũ)
        POWERUP,      // Power-up chung (cũ)
        SCORE,        // Điểm (cũ)
        TRIPLE_SHOT,  // Bắn 3 tia (mới)
        PIERCING,     // Đạn xuyên (mới)
        SHIELD        // Khiên bảo vệ (mới)
    }

    private int x, y;
    private int width = 30;
    private int height = 30;
    private int speed = 3;
    private boolean active = true;
    private ItemType type;
    private int rotationAngle = 0; // Góc xoay cho hiệu ứng

    public Item(int x, int y, ItemType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void update() {
        y += speed;
        rotationAngle += 5; // Xoay item
        if (rotationAngle >= 360) {
            rotationAngle = 0;
        }

        // Kiểm tra item có rơi ra ngoài màn hình không
        if (y > GamePanel.HEIGHT) {
            active = false;
        }
    }

    public void draw(Graphics2D g2d) {
        // Lưu transform gốc
        Graphics2D g2dCopy = (Graphics2D) g2d.create();

        // Xoay item
        g2dCopy.rotate(Math.toRadians(rotationAngle), x + width/2, y + height/2);

        switch (type) {
            case TRIPLE_SHOT:
                drawTripleShot(g2dCopy);
                break;
            case PIERCING:
                drawPiercing(g2dCopy);
                break;
            case SHIELD:
                drawShield(g2dCopy);
                break;
            case HEALTH:
                drawHealth(g2dCopy);
                break;
            case POWERUP:
                drawPowerUp(g2dCopy);
                break;
            case SCORE:
                drawScore(g2dCopy);
                break;
        }

        g2dCopy.dispose();
    }

    private void drawTripleShot(Graphics2D g2d) {
        // Vẽ icon bắn 3 tia (màu vàng)
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillOval(x, y, width, height);

        g2d.setColor(Color.ORANGE);
        g2d.setStroke(new BasicStroke(2));

        // Vẽ 3 mũi tên hướng lên
        int centerX = x + width/2;
        int centerY = y + height/2;

        // Mũi tên giữa
        g2d.drawLine(centerX, centerY + 5, centerX, centerY - 8);
        g2d.drawLine(centerX, centerY - 8, centerX - 3, centerY - 5);
        g2d.drawLine(centerX, centerY - 8, centerX + 3, centerY - 5);

        // Mũi tên trái
        g2d.drawLine(centerX - 7, centerY + 5, centerX - 7, centerY - 5);
        g2d.drawLine(centerX - 7, centerY - 5, centerX - 9, centerY - 3);
        g2d.drawLine(centerX - 7, centerY - 5, centerX - 5, centerY - 3);

        // Mũi tên phải
        g2d.drawLine(centerX + 7, centerY + 5, centerX + 7, centerY - 5);
        g2d.drawLine(centerX + 7, centerY - 5, centerX + 5, centerY - 3);
        g2d.drawLine(centerX + 7, centerY - 5, centerX + 9, centerY - 3);
    }

    private void drawPiercing(Graphics2D g2d) {
        // Vẽ icon đạn xuyên (màu cyan)

        g2d.setColor(new Color(0, 255, 255));
        g2d.fillOval(x, y, width, height);

        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(3));

        // Vẽ tia sét
        int centerX = x + width/2;
        int centerY = y + height/2;

        int[] xPoints = {centerX, centerX + 5, centerX - 2, centerX + 3, centerX - 5};
        int[] yPoints = {centerY - 10, centerY - 2, centerY, centerY + 5, centerY + 10};

        g2d.drawPolyline(xPoints, yPoints, 5);
    }

    private void drawShield(Graphics2D g2d) {
        // Vẽ icon khiên (màu xanh dương)
        g2d.setColor(new Color(100, 150, 255));
        g2d.fillOval(x, y, width, height);

        g2d.setColor(new Color(50, 100, 200));
        g2d.setStroke(new BasicStroke(2));

        // Vẽ hình khiên
        int centerX = x + width/2;
        int centerY = y + height/2;
        int shieldSize = width / 3;

        // Vẽ hình khiên với viền
        g2d.fillRect(centerX - shieldSize/2, centerY - shieldSize/2, shieldSize, shieldSize);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(centerX - shieldSize/2, centerY - shieldSize/2, shieldSize, shieldSize);

        // Vẽ chữ thập trên khiên
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(centerX, centerY - shieldSize/2, centerX, centerY + shieldSize/2);
        g2d.drawLine(centerX - shieldSize/2, centerY, centerX + shieldSize/2, centerY);
    }

    /**
     * Áp dụng hiệu ứng item cho player
     */
    public void applyEffect(Player player) {
        switch (type) {
            case TRIPLE_SHOT:
                player.activateTripleShot();
                break;
            case PIERCING:
                player.activatePiercing();
                break;
            case SHIELD:
                player.activateShield();
                break;
            case HEALTH:
            case POWERUP:
            case SCORE:
                // Xử lý trong GamePanel.collectItem()
                break;
        }
        active = false;
    }

    private void drawHealth(Graphics2D g2d) {
        // Vẽ icon hồi máu (màu đỏ)
        g2d.setColor(Color.RED);
        g2d.fillOval(x, y, width, height);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));

        // Vẽ dấu cộng
        int centerX = x + width/2;
        int centerY = y + height/2;
        int size = width / 3;

        g2d.drawLine(centerX, centerY - size, centerX, centerY + size);
        g2d.drawLine(centerX - size, centerY, centerX + size, centerY);
    }

    private void drawPowerUp(Graphics2D g2d) {
        // Vẽ icon power-up chung (màu tím)
        g2d.setColor(new Color(200, 0, 255));
        g2d.fillOval(x, y, width, height);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));

        // Vẽ ngôi sao
        int centerX = x + width/2;
        int centerY = y + height/2;
        int[] xPoints = new int[5];
        int[] yPoints = new int[5];

        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(i * 72 - 90);
            xPoints[i] = centerX + (int)(Math.cos(angle) * 10);
            yPoints[i] = centerY + (int)(Math.sin(angle) * 10);
        }

        g2d.drawPolygon(xPoints, yPoints, 5);
    }

    private void drawScore(Graphics2D g2d) {
        // Vẽ icon điểm (màu vàng kim)
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillOval(x, y, width, height);

        g2d.setColor(new Color(255, 140, 0));
        g2d.setStroke(new BasicStroke(2));

        // Vẽ ký hiệu $ hoặc coin
        int centerX = x + width/2;
        int centerY = y + height/2;

        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        String symbol = "$";
        int textX = centerX - fm.stringWidth(symbol)/2;
        int textY = centerY + fm.getAscent()/2 - 2;
        g2d.drawString(symbol, textX, textY);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public ItemType getType() {
        return type;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
