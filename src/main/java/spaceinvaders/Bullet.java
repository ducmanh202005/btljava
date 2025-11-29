package spaceinvaders;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet {
    private double x, y;
    private int width;
    private int height;
    private int speed;
    private int direction;
    private boolean active = true;
    private int damage = 1;
    private AssetManager assetManager;

    private boolean piercing = false;
    private int hitCount = 0;
    private static final int MAX_PIERCING_HITS = 3;

    private double velX = 0;
    private double velY = 0;
    private boolean useVelocity = false;

    private int wave = 1;
    private boolean isBoss = false;

    public Bullet(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.piercing = false;
        this.assetManager = AssetManager.getInstance();

        // Scale kích thước và tốc độ
        this.width = GamePanel.scaled(20);
        this.height = GamePanel.scaled(30);
        this.speed = GamePanel.scaled(7);
    }

    public Bullet(int x, int y, int direction, int damage) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.damage = damage;
        this.piercing = false;
        this.assetManager = AssetManager.getInstance();

        this.width = GamePanel.scaled(20);
        this.height = GamePanel.scaled(30);
        this.speed = GamePanel.scaled(7);
    }

    public Bullet(int x, int y, int direction, int damage, int wave, boolean isBoss) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.damage = damage;
        this.wave = wave;
        this.isBoss = isBoss;
        this.piercing = false;
        this.assetManager = AssetManager.getInstance();

        this.width = GamePanel.scaled(20);
        this.height = GamePanel.scaled(30);
        this.speed = GamePanel.scaled(7);
    }

    public Bullet(int x, int y, int directionY, int speedX, boolean piercing) {
        this.x = x;
        this.y = y;
        this.direction = directionY;
        this.piercing = piercing;
        this.assetManager = AssetManager.getInstance();

        this.width = GamePanel.scaled(20);
        this.height = GamePanel.scaled(30);
        this.speed = GamePanel.scaled(7);

        this.velX = speedX;
        this.velY = directionY * this.speed;
        this.useVelocity = (speedX != 0);
    }

    public void setVelocity(double velX, double velY) {
        this.velX = velX;
        this.velY = velY;
        this.useVelocity = true;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setWaveInfo(int wave, boolean isBoss) {
        this.wave = wave;
        this.isBoss = isBoss;
    }

    public void update() {
        if (useVelocity) {
            x += velX;
            y += velY;
        } else {
            y += speed * direction;
        }

        int boundary = GamePanel.scaled(50);
        if (y < -boundary || y > GamePanel.HEIGHT + boundary ||
                x < -boundary || x > GamePanel.WIDTH + boundary) {
            active = false;
        }
    }

    public void onHit() {
        if (piercing) {
            hitCount++;
            if (hitCount >= MAX_PIERCING_HITS) {
                active = false;
            }
        } else {
            active = false;
        }
    }

    public void draw(Graphics2D g2d) {
        if (piercing) {
            BufferedImage piercingImg = assetManager.getBulletPiercingImage();
            if (piercingImg != null) {
                g2d.drawImage(piercingImg, (int)x, (int)y, width, height, null);
            } else {
                g2d.setColor(Color.CYAN);
                g2d.fillRect((int)x - 2, (int)y, width + 4, height);
                g2d.setColor(new Color(100, 255, 255, 150));
                g2d.fillRect((int)x - 1, (int)y, width + 2, height);
            }
            g2d.setColor(new Color(0, 200, 255, 100));
            g2d.fillRect((int)x, (int)y + height, width, GamePanel.scaled(5));
        } else if (direction > 0) {
            BufferedImage bulletImg = null;

            if (isBoss) {
                bulletImg = assetManager.getBossBulletImage(wave);
            } else {
                bulletImg = assetManager.getEnemyBulletImage(wave);
            }

            if (bulletImg != null) {
                g2d.drawImage(bulletImg, (int)x, (int)y, width, height, null);
            } else {
                g2d.setColor(isBoss ? Color.ORANGE : Color.RED);
                g2d.fillRect((int)x, (int)y, width, height);
            }
        } else {
            BufferedImage bulletImg = assetManager.getBulletImage();
            if (bulletImg != null) {
                g2d.drawImage(bulletImg, (int)x, (int)y, width, height, null);
            } else {
                g2d.setColor(Color.YELLOW);
                g2d.fillRect((int)x, (int)y, width, height);
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isPiercing() { return piercing; }
    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getDirection() { return direction; }
    public int getDamage() { return damage; }
    public int getWave() { return wave; }
    public boolean isBossBullet() { return isBoss; }
}