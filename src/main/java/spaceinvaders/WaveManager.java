package spaceinvaders;

import java.util.List;
import java.util.Random;

/**
 * WaveManager - Full screen version với dynamic positioning
 */
public class WaveManager {
    private int currentWave = 1;
    private int enemiesSpawned = 0;
    private int enemiesPerWave;
    private boolean bossSpawned = false;
    private boolean bossSpawnRequested = false;
    private Random random;

    private double baseEnemySpeed = 1.0;
    private int baseEnemyHP = 1;
    private int baseEnemyDamage = 1;

    public WaveManager() {
        random = new Random();
        calculateWaveParameters();
    }

    private void calculateWaveParameters() {
        int baseEnemies = 10;

        if (currentWave == 1) {
            enemiesPerWave = 8 + random.nextInt(5);
        } else {
            enemiesPerWave = baseEnemies + (currentWave - 2) * 2;
            if (enemiesPerWave > 20) enemiesPerWave = 20;
        }

        if (currentWave == 1) {
            baseEnemySpeed = 1.2;
            baseEnemyHP = 1;
            baseEnemyDamage = 1;
        } else {
            baseEnemySpeed = 1.2 + (currentWave - 2) * 0.3;
            baseEnemyHP = 1 + (currentWave - 2);
            baseEnemyDamage = 1 + (currentWave - 2) / 2;
        }

        enemiesSpawned = 0;
        bossSpawned = false;
        bossSpawnRequested = false;
    }

    private int getEnemyVariantForWave() {
        return ((currentWave - 1) % 5) + 1;
    }

    public void spawnWave(List<Enemy> enemies, int screenWidth, int screenHeight) {
        if (enemiesSpawned < enemiesPerWave) {
            int enemiesToSpawn = enemiesPerWave - enemiesSpawned;
            int enemyVariant = getEnemyVariantForWave();

            // Tính toán số cột và hàng dựa trên kích thước màn hình
            int maxCols = Math.min(10, (screenWidth - GamePanel.scaled(160)) / GamePanel.scaled(70));
            int cols = Math.min(maxCols, enemiesToSpawn);
            int rows = (int)Math.ceil((double)enemiesToSpawn / cols);

            int startX = GamePanel.scaled(80);
            int startY = GamePanel.scaled(50);
            int spacingX = GamePanel.scaled(70);
            int spacingY = GamePanel.scaled(60);

            int enemyIndex = 0;

            for (int row = 0; row < rows && enemyIndex < enemiesToSpawn; row++) {
                for (int col = 0; col < cols && enemyIndex < enemiesToSpawn; col++) {
                    int x = startX + col * spacingX;
                    int y = startY + row * spacingY;

                    Enemy enemy = new Enemy(x, y, EnemyType.NORMAL, currentWave,
                            baseEnemySpeed, baseEnemyHP, baseEnemyDamage, enemyVariant);
                    enemies.add(enemy);
                    enemyIndex++;
                }
            }

            enemiesSpawned = enemiesPerWave;
            System.out.println("[WAVE] Spawned " + enemiesPerWave + " enemies (variant " + enemyVariant + ") for wave " + currentWave);
        }
    }

    public void checkAndSpawnBoss(List<Enemy> enemies, int screenWidth, int screenHeight) {
        if (enemiesSpawned >= enemiesPerWave && !bossSpawnRequested) {
            int activeNormalCount = 0;
            boolean hasBoss = false;

            for (Enemy enemy : enemies) {
                if (enemy.isActive()) {
                    if (enemy.getType() == EnemyType.BOSS) {
                        hasBoss = true;
                        break;
                    } else {
                        activeNormalCount++;
                    }
                }
            }

            if (activeNormalCount == 0 && !hasBoss) {
                spawnBoss(enemies, screenWidth, screenHeight);
            }
        }
    }

    private void spawnBoss(List<Enemy> enemies, int screenWidth, int screenHeight) {
        bossSpawnRequested = true;
        bossSpawned = true;

        int bossVariant = getEnemyVariantForWave();

        // Boss xuất hiện ở giữa màn hình với kích thước scaled
        int bossWidth = GamePanel.scaled(250);
        int x = screenWidth / 2 - bossWidth / 2;
        int y = GamePanel.scaled(60);

        int bossHP = baseEnemyHP * 15 + (currentWave * 8);
        double bossSpeed = baseEnemySpeed * 0.4;
        int bossDamage = baseEnemyDamage * 4 + (currentWave / 2);

        Enemy boss = new Enemy(x, y, EnemyType.BOSS, currentWave,
                bossSpeed, bossHP, bossDamage, bossVariant);
        enemies.add(boss);

        System.out.println("[BOSS] Spawned boss variant " + bossVariant + " for wave " + currentWave);
    }

    public boolean isWaveComplete(List<Enemy> enemies) {
        if (!bossSpawned) {
            return false;
        }

        for (Enemy enemy : enemies) {
            if (enemy.isActive()) {
                return false;
            }
        }
        return true;
    }

    public void nextWave() {
        currentWave++;
        calculateWaveParameters();
        System.out.println("[WAVE] Starting wave " + currentWave + " (enemy variant: " + getEnemyVariantForWave() + ")");
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getEnemiesPerWave() {
        return enemiesPerWave;
    }

    public double getBaseEnemySpeed() {
        return baseEnemySpeed;
    }

    public int getBaseEnemyHP() {
        return baseEnemyHP;
    }

    public int getBaseEnemyDamage() {
        return baseEnemyDamage;
    }

    public boolean isBossSpawned() {
        return bossSpawned;
    }
}