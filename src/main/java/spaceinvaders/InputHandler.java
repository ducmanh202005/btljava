package spaceinvaders;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Lớp xử lý input từ bàn phím
 */
public class InputHandler implements KeyListener {
    private Set<Integer> pressedKeys;
    private Set<Integer> justPressedKeys;
    
    // Các phím điều khiển
    public static final int KEY_LEFT = KeyEvent.VK_LEFT;
    public static final int KEY_RIGHT = KeyEvent.VK_RIGHT;
    public static final int KEY_UP = KeyEvent.VK_UP;
    public static final int KEY_DOWN = KeyEvent.VK_DOWN;
    public static final int KEY_SPACE = KeyEvent.VK_SPACE;
    public static final int KEY_ENTER = KeyEvent.VK_ENTER;
    public static final int KEY_ESCAPE = KeyEvent.VK_ESCAPE;
    public static final int KEY_P = KeyEvent.VK_P;
    
    public InputHandler() {
        pressedKeys = new HashSet<>();
        justPressedKeys = new HashSet<>();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (!pressedKeys.contains(keyCode)) {
            pressedKeys.add(keyCode);
            justPressedKeys.add(keyCode);
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeys.remove(keyCode);
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Không cần xử lý
    }
    
    /**
     * Kiểm tra phím có đang được nhấn không
     */
    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }
    
    /**
     * Kiểm tra phím vừa được nhấn (chỉ trả về true một lần)
     */
    public boolean isKeyJustPressed(int keyCode) {
        return justPressedKeys.contains(keyCode);
    }
    
    /**
     * Xóa danh sách phím vừa nhấn (gọi mỗi frame)
     */
    public void update() {
        justPressedKeys.clear();
    }
    
    /**
     * Kiểm tra có phím nào đang được nhấn không
     */
    public boolean isAnyKeyPressed() {
        return !pressedKeys.isEmpty();
    }
    
    /**
     * Lấy danh sách tất cả phím đang được nhấn
     */
    public Set<Integer> getPressedKeys() {
        return new HashSet<>(pressedKeys);
    }
}
