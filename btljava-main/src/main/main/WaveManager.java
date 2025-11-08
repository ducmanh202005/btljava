package main;

import java.util.List;
import java.util.Random;

/**
 * WaveManager - Mỗi wave có enemy và boss riêng, lặp lại sau 5 wave
 */
public class WaveManager {
    private int currentWave = 1;
    private int enemiesSpawned = 0;
    private int enemiesPerWave;
    private boolean bossSpawned = false;
    private boolean bossSpawnRequested = false;
    private Random random;
    
    // Scaling parameters
    private double baseEnemySpeed = 1.0;
    private int baseEnemyHP = 1;
    private int baseEnemyDamage = 1;
    
    public WaveManager() {
        random = new Random();
        calculateWaveParameters();
    }
    
    /**
     * Tính toán tham số cho wave hiện tại
     */
    private void calculateWaveParameters() {
        int baseEnemies = 10;
        
        if (currentWave == 1) {
            enemiesPerWave = 8 + random.nextInt(5); // 8-12
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
    
    /**
     * Lấy enemy variant (1-5) dựa trên wave, lặp lại sau 5 wave
     */
    private int getEnemyVariantForWave() {
        // Wave 1->enemy1, 2->enemy2, ..., 5->enemy5, 6->enemy1, ...
        return ((currentWave - 1) % 5) + 1;
    }
    
    /**
     * Spawn enemies cho wave hiện tại - TẤT CẢ CÙNG LOẠI
     */
    public void spawnWave(List<Enemy> enemies, int screenWidth, int screenHeight) {
        if (enemiesSpawned < enemiesPerWave) {
            int enemiesToSpawn = enemiesPerWave - enemiesSpawned;
            int enemyVariant = getEnemyVariantForWave(); // Lấy variant cho wave này
            
            int cols = Math.min(10, enemiesToSpawn);
            int rows = (int)Math.ceil((double)enemiesToSpawn / cols);
            
            int startX = 80;
            int startY = 50;
            int spacingX = 70;
            int spacingY = 60;
            
            int enemyIndex = 0;
            
            // TẤT CẢ ENEMY TRONG WAVE NÀY ĐỀU LÀ NORMAL VÀ CÙNG VARIANT
            for (int row = 0; row < rows && enemyIndex < enemiesToSpawn; row++) {
                for (int col = 0; col < cols && enemyIndex < enemiesToSpawn; col++) {
                    int x = startX + col * spacingX;
                    int y = startY + row * spacingY;
                    
                    // Tất cả enemy trong wave đều là NORMAL và cùng variant
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
    
    /**
     * Kiểm tra và spawn boss nếu đủ điều kiện
     */
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
    
    /**
     * Spawn boss với variant tương ứng wave
     */
    private void spawnBoss(List<Enemy> enemies, int screenWidth, int screenHeight) {
        bossSpawnRequested = true;
        bossSpawned = true;
        
        int bossVariant = getEnemyVariantForWave(); // Boss cùng variant với enemy trong wave
        int x = screenWidth / 2 - 60;
        int y = 60;
        
        int bossHP = baseEnemyHP * 15 + (currentWave * 8);
        double bossSpeed = baseEnemySpeed * 0.4;
        int bossDamage = baseEnemyDamage * 4 + (currentWave / 2);
        
        Enemy boss = new Enemy(x, y, EnemyType.BOSS, currentWave,
                             bossSpeed, bossHP, bossDamage, bossVariant);
        enemies.add(boss);
        
        System.out.println("[BOSS] Spawned boss variant " + bossVariant + " for wave " + currentWave);
    }
    
    /**
     * Kiểm tra wave hoàn thành
     */
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
    
    /**
     * Chuyển sang wave tiếp theo
     */
    public void nextWave() {
        currentWave++;
        calculateWaveParameters();
        System.out.println("[WAVE] Starting wave " + currentWave + " (enemy variant: " + getEnemyVariantForWave() + ")");
    }
    
    // Getters
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