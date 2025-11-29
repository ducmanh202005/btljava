package spaceinvaders;

import javax.swing.*;
import java.awt.*;

/**
 * Main - Full screen version
 */
public class Main {
    private JFrame frame;
    private GamePanel gamePanel;

    public Main() {
        initializeFrame();
        setupGamePanel();
        showFrame();
    }

    private void initializeFrame() {
        frame = new JFrame("Space Invaders - Full Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true); // Bỏ viền window
        frame.setResizable(false);

        // Lấy graphics environment để full screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        // Set full screen exclusive mode
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(frame);
        } else {
            // Fallback: maximized window nếu không support full screen
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    private void setupGamePanel() {
        gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.pack();
    }

    private void showFrame() {
        frame.setVisible(true);
        gamePanel.requestFocusInWindow();
    }

    public void run() {
        // Game loop được xử lý bởi Timer trong GamePanel
    }

    public static void main(String[] args) {
        // Thiết lập Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chạy game trên EDT
        SwingUtilities.invokeLater(() -> {
            Main game = new Main();
            game.run();
        });
    }
}