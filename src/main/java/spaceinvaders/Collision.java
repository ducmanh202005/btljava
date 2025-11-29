package spaceinvaders;
import java.awt.Rectangle;

/**
 * Lớp xử lý logic va chạm trong game
 */
public class Collision {
    
    /**
     * Kiểm tra va chạm giữa hai hình chữ nhật
     */
    public static boolean checkCollision(Rectangle rect1, Rectangle rect2) {
        return rect1.intersects(rect2);
    }
    
    /**
     * Kiểm tra va chạm giữa đạn và enemy
     */

    public static boolean checkBulletEnemyCollision(Bullet bullet, Enemy enemy) {
        if (bullet == null || enemy == null || !bullet.isActive() || !enemy.isActive()) {
            return false;
        }
        
        Rectangle bulletRect = bullet.getBounds();
        Rectangle enemyRect = enemy.getBounds();
        
        return checkCollision(bulletRect, enemyRect);
    }
    
    /**
     * Kiểm tra va chạm giữa đạn enemy và player
     */
    public static boolean checkEnemyBulletPlayerCollision(Bullet enemyBullet, Player player) {
        if (enemyBullet == null || player == null || !enemyBullet.isActive() || !player.isActive()) {
            return false;
        }
        
        Rectangle bulletRect = enemyBullet.getBounds();
        Rectangle playerRect = player.getBounds();
        
        return checkCollision(bulletRect, playerRect);
    }
    
    /**
     * Kiểm tra va chạm giữa enemy và player
     */
    public static boolean checkEnemyPlayerCollision(Enemy enemy, Player player) {
        if (enemy == null || player == null || !enemy.isActive() || !player.isActive()) {
            return false;
        }
        
        Rectangle enemyRect = enemy.getBounds();
        Rectangle playerRect = player.getBounds();
        
        return checkCollision(enemyRect, playerRect);
    }
    
    /**
     * Kiểm tra va chạm với biên màn hình
     */
    public static boolean checkScreenBounds(Rectangle object, int screenWidth, int screenHeight) {
        return object.x < 0 || object.x + object.width > screenWidth ||
               object.y < 0 || object.y + object.height > screenHeight;
    }
    
    /**
     * Giới hạn object trong màn hình
     */
    public static void constrainToScreen(Rectangle object, int screenWidth, int screenHeight) {
        if (object.x < 0) {
            object.x = 0;
        }
        if (object.x + object.width > screenWidth) {
            object.x = screenWidth - object.width;
        }
        if (object.y < 0) {
            object.y = 0;
        }
        if (object.y + object.height > screenHeight) {
            object.y = screenHeight - object.height;
        }
    }
    
    /**
     * Kiểm tra va chạm giữa hai điểm với khoảng cách
     */
    public static boolean checkPointCollision(int x1, int y1, int x2, int y2, int distance) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double actualDistance = Math.sqrt(dx * dx + dy * dy);
        return actualDistance <= distance;
    }
    /**
     * Kiểm tra va chạm giữa hai hình tròn
     */
    public static boolean checkCircleCollision(int x1, int y1, int radius1, int x2, int y2, int radius2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= (radius1 + radius2);
    }
}
