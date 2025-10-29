package main;

import javax.swing.*;

/**
 * Lớp Game chính - khởi tạo JFrame và chạy game
 */
public class Game {
    private JFrame frame;
    private GamePanel gamePanel;
    
    public Game() {
        initializeFrame();
        setupGamePanel();
        showFrame();
    }
    /**
     * Khởi tạo JFrame
     */
    private void initializeFrame() {
        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }
    
    /**
     * Thiết lập GamePanel
     */
    private void setupGamePanel() {
        gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.pack();
    }
    
    /**
     * Hiển thị frame
     */
    private void showFrame() {
        frame.setVisible(true);
        gamePanel.requestFocusInWindow();
    }
    
    /**
     * Chạy game
     */
    public void run() {
        // Game loop được xử lý bởi Timer trong GamePanel
        // Không cần thêm gì ở đây
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        // Thiết lập Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Chạy game trên EDT
        SwingUtilities.invokeLater(() -> {
            new Game();
        });
    }
}
