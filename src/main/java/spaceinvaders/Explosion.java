package spaceinvaders;

import java.awt.*;

public class Explosion {
    private int x, y;
    private int radius;
    private int maxRadius;
    private int duration;
    private int maxDuration = 25;

    public Explosion(int x, int y, int maxRadius) {
        this.x = x;
        this.y = y;
        this.maxRadius = maxRadius;
        this.radius = 0;
        this.duration = 0;
    }

    public void update() {
        duration++;

        if (duration < maxDuration / 2) {
            radius = (int)(maxRadius * (duration / (maxDuration / 2.0)));
        } else {
            radius = (int)(maxRadius * (1.0 - (duration - maxDuration / 2) / (maxDuration / 2.0)));
        }
    }

    public void draw(Graphics2D g2d) {
        if (duration >= maxDuration) return;

        float alpha = 1.0f - (duration / (float)maxDuration);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        g2d.setColor(new Color(255, 255, 0, (int)(255 * alpha)));
        g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        int innerRadius = radius * 2 / 3;
        g2d.setColor(new Color(255, 165, 0, (int)(255 * alpha)));
        g2d.fillOval(x - innerRadius, y - innerRadius, innerRadius * 2, innerRadius * 2);

        int coreRadius = radius / 3;
        g2d.setColor(new Color(255, 0, 0, (int)(255 * alpha)));
        g2d.fillOval(x - coreRadius, y - coreRadius, coreRadius * 2, coreRadius * 2);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    public boolean isActive() {
        return duration < maxDuration;
    }
}