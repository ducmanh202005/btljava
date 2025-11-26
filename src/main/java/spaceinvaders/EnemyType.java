package spaceinvaders;
/**
 * Enum định nghĩa các loại kẻ địch trong game
 */
public enum EnemyType {
    NORMAL,     // Địch thường - di chuyển chậm, HP trung bình
    FAST,       // Địch nhanh - di chuyển nhanh, HP thấp
    SHOOTING,   // Địch bắn được - có thể bắn đạn, HP cao
    BOSS        // Boss - HP rất cao, bắn theo hình tròn
}
