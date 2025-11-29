package spaceinvaders;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public class Enemy {
    private int x, y;
    private int width;
    private int height;
    private double speed;
    private boolean active = true;
    private int direction = 1;
    private AssetManager assetManager;

    private EnemyType type;
    private int level;
    private int maxHP;
    private int currentHP;
    private int damage;
    private int scoreValue;
    private int enemyVariant;

    private int shootCooldown = 0;
    private int shootInterval;
    private Random random;

    private int moveDirectionX = 1;
    private int moveDirectionY = 0;
    private int randomMoveTimer = 0;
    private static final int RANDOM_MOVE_INTERVAL = 60;

    private int bossShootAngle = 0;
    private int bossMovePattern = 0;

    public Enemy(int x, int y, EnemyType type, int level,
                 double baseSpeed, int baseHP, int baseDamage, int enemyVariant) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.level = level;
        this.enemyVariant = enemyVariant;
        this.random = new Random();
        this.assetManager = AssetManager.getInstance();

        changeRandomDirection();
        randomMoveTimer = random.nextInt(RANDOM_MOVE_INTERVAL);

        calculateStats(baseSpeed, baseHP, baseDamage);

        switch (type) {
            case BOSS:
                shootInterval = Math.max(15, 120 - (level * 4));
                break;
            case SHOOTING:
                shootInterval = Math.max(25, 180 - (level * 6));
                break;
            case FAST:
                shootInterval = Math.max(40, 200 - (level * 5));
                break;
            case NORMAL:
                shootInterval = Math.max(50, 250 - (level * 4));
                break;
        }
        int cooldownRange = level == 1 ? shootInterval / 2 : shootInterval / 3;
        shootCooldown = random.nextInt(cooldownRange) + (level == 1 ? shootInterval / 3 : 0);
    }

    private void calculateStats(double baseSpeed, int baseHP, int baseDamage) {
        switch (type) {
            case NORMAL:
                this.speed = GamePanel.scaledDouble(baseSpeed);
                this.maxHP = baseHP;
                this.damage = baseDamage;
                this.scoreValue = 10 * level;
                this.width = GamePanel.scaled(70);
                this.height = GamePanel.scaled(70);
                break;
            case FAST:
                this.speed = GamePanel.scaledDouble(baseSpeed * 1.8);
                this.maxHP = Math.max(1, baseHP / 2);
                this.damage = baseDamage;
                this.scoreValue = 15 * level;
                this.width = GamePanel.scaled(40);
                this.height = GamePanel.scaled(40);
                break;
            case SHOOTING:
                this.speed = GamePanel.scaledDouble(baseSpeed * 0.7);
                this.maxHP = baseHP * 2;
                this.damage = baseDamage * 2;
                this.scoreValue = 25 * level;
                this.width = GamePanel.scaled(50);
                this.height = GamePanel.scaled(50);
                break;
            case BOSS:
                this.speed = GamePanel.scaledDouble(baseSpeed * 0.4);
                this.maxHP = baseHP * 2 + (level * 2);
                this.damage = baseDamage * 4 + (level / 2);
                this.scoreValue = 200 * level;
                this.width = GamePanel.scaled(250);
                this.height = GamePanel.scaled(250);
                break;
        }
        this.currentHP = maxHP;
    }

    public void update() {
        if (!active) return;
        move();
        shootCooldown--;
    }

    private void move() {
        if (type == EnemyType.BOSS) {
            bossMovePattern++;
            double patternX = Math.sin(bossMovePattern * 0.05) * 2 * GamePanel.SCALE_FACTOR;
            x += (int)(speed * direction) + (int)patternX;

            if (bossMovePattern % 60 == 0) {
                y += GamePanel.scaled(1);
            }

            if (x <= 0 || x >= GamePanel.WIDTH - width) {
                direction *= -1;
            }

            if (x < 0) x = 0;
            if (x > GamePanel.WIDTH - width) x = GamePanel.WIDTH - width;
            if (y > GamePanel.HEIGHT - GamePanel.scaled(150)) y = GamePanel.HEIGHT - GamePanel.scaled(150);
        } else {
            randomMoveTimer++;

            if (randomMoveTimer >= RANDOM_MOVE_INTERVAL) {
                randomMoveTimer = 0;
                changeRandomDirection();
            }

            double moveX = speed * moveDirectionX;
            double moveY = speed * 0.3 * moveDirectionY;

            if (Math.abs(moveX) >= 1.0) {
                x += (int)moveX;
            } else if (moveDirectionX != 0 && randomMoveTimer % 2 == 0) {
                x += moveDirectionX;
            }

            if (Math.abs(moveY) >= 0.5) {
                y += (int)moveY;
            } else if (moveDirectionY != 0 && randomMoveTimer % 3 == 0) {
                y += moveDirectionY;
            }

            if (x <= 0) {
                x = 0;
                moveDirectionX = 1;
            } else if (x >= GamePanel.WIDTH - width) {
                x = GamePanel.WIDTH - width;
                moveDirectionX = -1;
            }

            if (y <= 0) {
                y = 0;
                moveDirectionY = 1;
            } else if (y >= GamePanel.HEIGHT - GamePanel.scaled(200)) {
                y = GamePanel.HEIGHT - GamePanel.scaled(200);
                moveDirectionY = -1;
            }
        }
    }

    private void changeRandomDirection() {
        moveDirectionX = random.nextBoolean() ? 1 : -1;
        int rand = random.nextInt(3);
        if (rand == 0) {
            moveDirectionY = -1;
        } else if (rand == 1) {
            moveDirectionY = 1;
        } else {
            moveDirectionY = 0;
        }
    }

    public void shoot(List<Bullet> enemyBullets) {
        if (shootCooldown <= 0) {
            if (type == EnemyType.BOSS) {
                shootCircularPattern(enemyBullets);
            } else {
                shootNormalPattern(enemyBullets);
            }
            shootCooldown = shootInterval;
        }
    }

    private void shootNormalPattern(List<Bullet> enemyBullets) {
        int bulletWidth = GamePanel.scaled(20);
        int bulletX = x + width / 2 - bulletWidth / 2;
        int bulletY = y + height;

        int numBullets = 1;
        if (level >= 8) numBullets = 2;
        if (level >= 13) numBullets = 3;

        int bulletSpeed;
        if (level == 1) {
            bulletSpeed = GamePanel.scaled(5);
        } else {
            bulletSpeed = GamePanel.scaled(6 + ((level - 2) / 2));
        }

        boolean isBoss = (type == EnemyType.BOSS);

        if (numBullets == 1) {
            Bullet bullet = new Bullet(bulletX, bulletY, 1, damage, enemyVariant, isBoss);
            bullet.setSpeed(bulletSpeed);
            enemyBullets.add(bullet);
        } else if (numBullets == 2) {
            int spread = GamePanel.scaled(15);
            Bullet bullet1 = new Bullet(bulletX - spread, bulletY, 1, damage, enemyVariant, isBoss);
            Bullet bullet2 = new Bullet(bulletX + spread, bulletY, 1, damage, enemyVariant, isBoss);
            bullet1.setSpeed(bulletSpeed);
            bullet2.setSpeed(bulletSpeed);
            enemyBullets.add(bullet1);
            enemyBullets.add(bullet2);
        } else {
            int spread = GamePanel.scaled(20);
            Bullet bullet1 = new Bullet(bulletX - spread, bulletY, 1, damage, enemyVariant, isBoss);
            Bullet bullet2 = new Bullet(bulletX, bulletY, 1, damage, enemyVariant, isBoss);
            Bullet bullet3 = new Bullet(bulletX + spread, bulletY, 1, damage, enemyVariant, isBoss);
            bullet1.setSpeed(bulletSpeed);
            bullet2.setSpeed(bulletSpeed);
            bullet3.setSpeed(bulletSpeed);
            enemyBullets.add(bullet1);
            enemyBullets.add(bullet2);
            enemyBullets.add(bullet3);
        }
    }

    private void shootCircularPattern(List<Bullet> enemyBullets) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;

        int numBullets = 8 + (level / 2);
        if (numBullets > 16) numBullets = 16;

        bossShootAngle += 10;
        if (bossShootAngle >= 360) bossShootAngle = 0;

        double baseBulletSpeed = GamePanel.scaledDouble(3.0 + (level * 0.3));

        for (int i = 0; i < numBullets; i++) {
            double angle = (Math.PI * 2 * i / numBullets) + (bossShootAngle * Math.PI / 180);

            int spawnRadius = GamePanel.scaled(30);
            int bulletX = centerX + (int)(Math.cos(angle) * spawnRadius);
            int bulletY = centerY + (int)(Math.sin(angle) * spawnRadius);

            Bullet bullet = new Bullet(bulletX, bulletY, 1, damage, enemyVariant, true);
            bullet.setVelocity(Math.cos(angle) * baseBulletSpeed, Math.sin(angle) * baseBulletSpeed);
            enemyBullets.add(bullet);
        }
    }

    public void takeDamage(int damage) {
        currentHP -= damage;
        if (currentHP <= 0) {
            active = false;
        }
    }

    public void draw(Graphics2D g2d) {
        if (!active) return;

        BufferedImage img = null;

        if (type == EnemyType.BOSS) {
            img = assetManager.getBossImage(enemyVariant);

            if (img != null) {
                Composite oldComposite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.drawImage(img, x, y, width, height, null);
                g2d.setComposite(oldComposite);
            } else {
                g2d.setColor(Color.ORANGE);
                g2d.fillRect(x, y, width, height);
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(GamePanel.scaled(3)));
                g2d.drawRect(x, y, width, height);
            }
        } else {
            img = assetManager.getEnemyImage(enemyVariant);

            if (img != null) {
                g2d.drawImage(img, x, y, width, height, null);
            } else {
                g2d.setColor(getEnemyColor());
                g2d.fillRect(x, y, width, height);
            }
        }

        if (maxHP > 1) {
            drawHPBar(g2d);
        }

        if (shootCooldown < 10) {
            int indicatorSize = GamePanel.scaled(8);
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x + width - indicatorSize, y + height - indicatorSize, indicatorSize, indicatorSize);
        }
    }

    private Color getEnemyColor() {
        switch (type) {
            case NORMAL: return Color.RED;
            case FAST: return Color.CYAN;
            case SHOOTING: return Color.MAGENTA;
            case BOSS: return Color.ORANGE;
            default: return Color.RED;
        }
    }

    private void drawHPBar(Graphics2D g2d) {
        int barWidth = width;
        int barHeight = type == EnemyType.BOSS ? GamePanel.scaled(8) : GamePanel.scaled(6);
        int barX = x;
        int barY = y - (type == EnemyType.BOSS ? GamePanel.scaled(15) : GamePanel.scaled(12));

        g2d.setColor(Color.BLACK);
        g2d.fillRect(barX, barY, barWidth, barHeight);

        double hpPercent = (double)currentHP / maxHP;
        Color hpColor;
        if (hpPercent > 0.6) {
            hpColor = Color.GREEN;
        } else if (hpPercent > 0.3) {
            hpColor = Color.YELLOW;
        } else {
            hpColor = Color.RED;
        }

        g2d.setColor(hpColor);
        int currentWidth = (int)(barWidth * hpPercent);
        g2d.fillRect(barX, barY, currentWidth, barHeight);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(barX, barY, barWidth, barHeight);

        if (type == EnemyType.BOSS) {
            g2d.setFont(new Font("Arial", Font.BOLD, GamePanel.scaled(12)));
            String hpText = currentHP + "/" + maxHP;
            FontMetrics fm = g2d.getFontMetrics();
            int textX = barX + (barWidth - fm.stringWidth(hpText)) / 2;
            g2d.drawString(hpText, textX, barY - GamePanel.scaled(3));
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public EnemyType getType() { return type; }
    public int getLevel() { return level; }
    public int getCurrentHP() { return currentHP; }
    public int getMaxHP() { return maxHP; }
    public int getScoreValue() { return scoreValue; }
    public int getDamage() { return damage; }
    public int getDirection() { return direction; }
    public void setDirection(int dir) { this.direction = dir; }
    public int getEnemyVariant() { return enemyVariant; }
}