package spaceinvaders;

import javax.swing.*;
import java.awt.*;

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
        frame.setUndecorated(true);
        frame.setResizable(false);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(frame);
        } else {
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
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            Main game = new Main();
            game.run();
        });
    }
}