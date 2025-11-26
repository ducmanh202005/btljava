package spaceinvaders;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Lớp Button - Nút bấm với hình ảnh
 */
public class Button {
    private int x, y;
    private int width, height;
    private BufferedImage image;
    private Rectangle bounds;
    private boolean hovered = false;
    private String text; // Fallback nếu không có hình ảnh

    /**
     * Constructor với hình ảnh
     */
    public Button(int x, int y, int width, int height, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.bounds = new Rectangle(x, y, width, height);
        this.text = "";
    }

    /**
     * Constructor với text (fallback)
     */
    public Button(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = null;
        this.bounds = new Rectangle(x, y, width, height);
        this.text = text;
    }

    /**
     * Kiểm tra chuột có hover trên button không
     */
    public void update(int mouseX, int mouseY) {
        hovered = bounds.contains(mouseX, mouseY);
    }

    /**
     * Kiểm tra button có được click không
     */
    public boolean isClicked(int mouseX, int mouseY) {
        return bounds.contains(mouseX, mouseY);
    }

    /**
     * Vẽ button
     */
    public void draw(Graphics2D g2d) {
        if (image != null) {
            // Vẽ hình ảnh
            if (hovered) {
                // Hiệu ứng hover: phóng to nhẹ và sáng hơn
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                int offset = 5;
                g2d.drawImage(image, x - offset, y - offset, width + offset*2, height + offset*2, null);

                // Vẽ viền sáng
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(x - offset, y - offset, width + offset*2, height + offset*2);
            } else {
                // Vẽ bình thường
                g2d.drawImage(image, x, y, width, height, null);
            }
        } else {
            // Fallback: vẽ nút với text
            if (hovered) {
                g2d.setColor(new Color(100, 150, 255));
            } else {
                g2d.setColor(new Color(50, 100, 200));
            }
            g2d.fillRoundRect(x, y, width, height, 20, 20);

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, width, height, 20, 20);

            // Vẽ text
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (width - fm.stringWidth(text)) / 2;
            int textY = y + (height + fm.getAscent()) / 2 - 5;
            g2d.drawString(text, textX, textY);
        }
    }

    // Getters
    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.bounds.setLocation(x, y);
    }
}