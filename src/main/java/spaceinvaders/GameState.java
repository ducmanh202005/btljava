package spaceinvaders;
/**
 * Enum định nghĩa các trạng thái của game
 */
public enum GameState {
    START,      // Màn hình bắt đầu với nút Start và Mute
    MENU,       // Màn hình menu chính (giữ lại để tương thích)
    PLAYING,    // Đang chơi game
    GAMEOVER,   // Game kết thúc (tạm thời)
    END,        // Màn hình kết thúc với nút Restart
    PAUSED      // Tạm dừng game
}