package spaceinvaders;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Item {
    public enum ItemType {
        HEALTH, POWERUP, SCORE, TRIPLE_SHOT, PIERCING, SHIELD
    }

    private int x, y;
    private int width;
    private int height;
    private int speed;
    private boolean active = true;
    private ItemType type;
    private int rotationAngle = 0;
    private AssetManager assetManager;

    public Item(int x, int y, ItemType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.assetManager = AssetManager.getInstance();

        // Scale kích thước và tốc độ
        this.width = GamePanel.scaled(30);
        this.height = GamePanel.scaled(30);
        this.speed = GamePanel.scaled(3);
    }

    public void update() {
        y += speed;
        rotationAngle += 5;
        if (rotationAngle >= 360) {
            rotationAngle = 0;
        }

        if (y > GamePanel.HEIGHT) {
            active = false;
        }
    }

    public void draw(Graphics2D g2d) {
        Graphics2D g2dCopy = (Graphics2D) g2d.create();
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
        BufferedImage img = assetManager.getTripleItemImage();
        if(img != null) {
            g2d.drawImage(img, x, y, width, height, null);
            return;
        }
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillOval(x, y, width, height);

        g2d.setColor(Color.ORANGE);
        g2d.setStroke(new BasicStroke(GamePanel.scaled(2)));

        int centerX = x + width/2;
        int centerY = y + height/2;
        int arrowSize = GamePanel.scaled(8);
        int arrowOffset = GamePanel.scaled(7);

        g2d.drawLine(centerX, centerY + GamePanel.scaled(5), centerX, centerY - arrowSize);
        g2d.drawLine(centerX, centerY - arrowSize, centerX - GamePanel.scaled(3), centerY - GamePanel.scaled(5));
        g2d.drawLine(centerX, centerY - arrowSize, centerX + GamePanel.scaled(3), centerY - GamePanel.scaled(5));

        g2d.drawLine(centerX - arrowOffset, centerY + GamePanel.scaled(5), centerX - arrowOffset, centerY - GamePanel.scaled(5));
        g2d.drawLine(centerX - arrowOffset, centerY - GamePanel.scaled(5), centerX - arrowOffset - GamePanel.scaled(2), centerY - GamePanel.scaled(3));
        g2d.drawLine(centerX - arrowOffset, centerY - GamePanel.scaled(5), centerX - arrowOffset + GamePanel.scaled(2), centerY - GamePanel.scaled(3));

        g2d.drawLine(centerX + arrowOffset, centerY + GamePanel.scaled(5), centerX + arrowOffset, centerY - GamePanel.scaled(5));
        g2d.drawLine(centerX + arrowOffset, centerY - GamePanel.scaled(5), centerX + arrowOffset - GamePanel.scaled(2), centerY - GamePanel.scaled(3));
        g2d.drawLine(centerX + arrowOffset, centerY - GamePanel.scaled(5), centerX + arrowOffset + GamePanel.scaled(2), centerY - GamePanel.scaled(3));
    }

    private void drawPiercing(Graphics2D g2d) {
        BufferedImage img = assetManager.getPiercingItemImage();
        if(img != null) {
            g2d.drawImage(img, x, y, width, height, null);
            return;
        }
        g2d.setColor(new Color(0, 255, 255));
        g2d.fillOval(x, y, width, height);

        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(GamePanel.scaled(3)));

        int centerX = x + width/2;
        int centerY = y + height/2;

        int[] xPoints = {centerX, centerX + GamePanel.scaled(5), centerX - GamePanel.scaled(2),
                centerX + GamePanel.scaled(3), centerX - GamePanel.scaled(5)};
        int[] yPoints = {centerY - GamePanel.scaled(10), centerY - GamePanel.scaled(2), centerY,
                centerY + GamePanel.scaled(5), centerY + GamePanel.scaled(10)};

        g2d.drawPolyline(xPoints, yPoints, 5);
    }

    private void drawShield(Graphics2D g2d) {
        BufferedImage img = assetManager.getShieldItemImage();
        if(img != null) {
            g2d.drawImage(img, x, y, width, height, null);
            return;
        }
        g2d.setColor(new Color(100, 150, 255));
        g2d.fillOval(x, y, width, height);

        g2d.setColor(new Color(50, 100, 200));
        g2d.setStroke(new BasicStroke(GamePanel.scaled(2)));

        int centerX = x + width/2;
        int centerY = y + height/2;
        int shieldSize = width / 3;

        g2d.fillRect(centerX - shieldSize/2, centerY - shieldSize/2, shieldSize, shieldSize);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(centerX - shieldSize/2, centerY - shieldSize/2, shieldSize, shieldSize);

        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(centerX, centerY - shieldSize/2, centerX, centerY + shieldSize/2);
        g2d.drawLine(centerX - shieldSize/2, centerY, centerX + shieldSize/2, centerY);
    }

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
                break;
        }
        active = false;
    }

    private void drawHealth(Graphics2D g2d) {
        BufferedImage img = assetManager.getHpItemImage();
        if(img != null) {
            g2d.drawImage(img, x, y, width, height, null);
            return;
        }
        g2d.setColor(Color.RED);
        g2d.fillOval(x, y, width, height);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(GamePanel.scaled(3)));

        int centerX = x + width/2;
        int centerY = y + height/2;
        int size = width / 3;

        g2d.drawLine(centerX, centerY - size, centerX, centerY + size);
        g2d.drawLine(centerX - size, centerY, centerX + size, centerY);
    }

    private void drawPowerUp(Graphics2D g2d) {
        BufferedImage img = assetManager.getPowerItemImage();
        if(img != null) {
            g2d.drawImage(img, x, y, width, height, null);
            return;
        }
        g2d.setColor(new Color(200, 0, 255));
        g2d.fillOval(x, y, width, height);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(GamePanel.scaled(2)));

        int centerX = x + width/2;
        int centerY = y + height/2;
        int[] xPoints = new int[5];
        int[] yPoints = new int[5];

        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(i * 72 - 90);
            xPoints[i] = centerX + (int)(Math.cos(angle) * GamePanel.scaled(10));
            yPoints[i] = centerY + (int)(Math.sin(angle) * GamePanel.scaled(10));
        }

        g2d.drawPolygon(xPoints, yPoints, 5);
    }

    private void drawScore(Graphics2D g2d) {
        BufferedImage img = assetManager.getScoreItemImage();
        if(img != null) {
            g2d.drawImage(img, x, y, width, height, null);
            return;
        }
        g2d.setColor(new Color(255, 215, 0));
        g2d.fillOval(x, y, width, height);

        g2d.setColor(new Color(255, 140, 0));
        g2d.setStroke(new BasicStroke(GamePanel.scaled(2)));

        int centerX = x + width/2;
        int centerY = y + height/2;

        g2d.setFont(new Font("Arial", Font.BOLD, GamePanel.scaled(20)));
        FontMetrics fm = g2d.getFontMetrics();
        String symbol = "$";
        int textX = centerX - fm.stringWidth(symbol)/2;
        int textY = centerY + fm.getAscent()/2 - 2;
        g2d.drawString(symbol, textX, textY);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    public ItemType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}