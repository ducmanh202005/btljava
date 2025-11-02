package main;

import java.util.List;
import java.util.Random;

/**
 * WaveManager - Quản lý hệ thống wave/level vô tận
 * FIXED: Boss spawn sau khi hết địch thường
 */
public class WaveManager {
    private int currentWave = 1;
    private int enemiesSpawned = 0;
    private int enemiesPerWave;
    private boolean bossSpawned = false;
    private boolean bossSpawnRequested = false; // NEW: Flag để spawn boss 1 lần
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
        bossSpawnRequested = false; // Reset flag
    }
    
    /**
     * Spawn enemies cho wave hiện tại
     */
    public void spawnWave(List<Enemy> enemies, int screenWidth, int screenHeight) {
        if (enemiesSpawned < enemiesPerWave) {
            int enemiesToSpawn = enemiesPerWave - enemiesSpawned;
            
            int cols = Math.min(10, enemiesToSpawn);
            int rows = (int)Math.ceil((double)enemiesToSpawn / cols);
            
            int startX = 80;
            int startY = 50;
            int spacingX = 70;
            int spacingY = 60;
            
            int enemyIndex = 0;
            
            for (int row = 0; row < rows && enemyIndex < enemiesToSpawn; row++) {
                for (int col = 0; col < cols && enemyIndex < enemiesToSpawn; col++) {
                    int x = startX + col * spacingX;
                    int y = startY + row * spacingY;
                    
                    EnemyType type = getEnemyTypeForRow(row, rows, currentWave);
                    
                    Enemy enemy = new Enemy(x, y, type, currentWave, 
                                          baseEnemySpeed, baseEnemyHP, baseEnemyDamage);
                    enemies.add(enemy);
                    enemyIndex++;
                }
            }
            
            enemiesSpawned = enemiesPerWave;
            System.out.println("[WAVE] Spawned " + enemiesPerWave + " enemies for wave " + currentWave);
        }
    }
    
    /**
     * Phân bố loại địch theo hàng
     */
    private EnemyType getEnemyTypeForRow(int row, int totalRows, int wave) {
        double rand = random.nextDouble();
        
        if (wave == 1) {
            if (row == 0) {
                return EnemyType.NORMAL;
            } else if (row == totalRows - 1) {
                if (rand < 0.7) return EnemyType.NORMAL;
                else if (rand < 0.95) return EnemyType.FAST;
                else return EnemyType.SHOOTING;
            } else {
                return rand < 0.6 ? EnemyType.NORMAL : EnemyType.FAST;
            }
        } else {
            if (row == 0) {
                return rand < 0.8 ? EnemyType.NORMAL : EnemyType.FAST;
            } else if (row == totalRows - 1) {
                if (rand < 0.4) return EnemyType.NORMAL;
                else if (rand < 0.7) return EnemyType.FAST;
                else return EnemyType.SHOOTING;
            } else {
                if (rand < 0.5) return EnemyType.NORMAL;
                else if (rand < 0.8) return EnemyType.FAST;
                else return EnemyType.SHOOTING;
            }
        }
    }
    
    /**
     * FIXED: Kiểm tra và spawn boss nếu đủ điều kiện
     * Gọi method này mỗi frame từ GamePanel
     */
    public void checkAndSpawnBoss(List<Enemy> enemies, int screenWidth, int screenHeight) {
        // Chỉ check khi đã spawn đủ địch thường và chưa request spawn boss
        if (enemiesSpawned >= enemiesPerWave && !bossSpawnRequested) {
            
            // Đếm địch thường còn active
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
            
            // DEBUG
            if (activeNormalCount <= 3) { // Chỉ in khi gần hết địch
                System.out.println("[BOSS CHECK] Normal enemies left: " + activeNormalCount + ", Has boss: " + hasBoss);
            }
            
            // SPAWN BOSS khi không còn địch thường và chưa có boss
            if (activeNormalCount == 0 && !hasBoss) {
                spawnBoss(enemies, screenWidth, screenHeight);
            }
        }
    }
    
    /**
     * Spawn boss thực sự
     */
    private void spawnBoss(List<Enemy> enemies, int screenWidth, int screenHeight) {
        bossSpawnRequested = true;
        bossSpawned = true;
        
        // ★★★ FIX: Boss to hơn (120x120) nên điều chỉnh vị trí spawn ★★★
        int x = screenWidth / 2 - 60; // 120/2 = 60 (center boss)
        int y = 60; // Cao hơn một chút
        
        int bossHP = baseEnemyHP * 15 + (currentWave * 8);
        double bossSpeed = baseEnemySpeed * 0.4;
        int bossDamage = baseEnemyDamage * 4 + (currentWave / 2);
        
        Enemy boss = new Enemy(x, y, EnemyType.BOSS, currentWave,
                             bossSpeed, bossHP, bossDamage);
        enemies.add(boss);
        
        System.out.println("╔════════════════════════╗");
        System.out.println("║   BOSS SPAWNED!!!     ║");
        System.out.println("╠════════════════════════╣");
        System.out.println("║ Wave: " + currentWave);
        System.out.println("║ HP: " + bossHP);
        System.out.println("║ Damage: " + bossDamage);
        System.out.println("║ Position: (" + x + ", " + y + ")");
        System.out.println("╚════════════════════════╝");
    }
    
    /**
     * Kiểm tra wave hoàn thành (hết cả địch và boss)
     */
    public boolean isWaveComplete(List<Enemy> enemies) {
        // Chỉ complete khi boss đã spawn VÀ không còn địch nào
        if (!bossSpawned) {
            return false; // Chưa spawn boss thì chưa complete
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
        System.out.println("[WAVE] Starting wave " + currentWave);
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