package net.lemonfactory.sudoku.gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Starting point for the GUI application.
 * 
 * @author Chungmin Lee
 */
class GUILauncher {

    // Use native look and feel
    static {
        try {
            UIManager.getInstalledLookAndFeels();  // tweak for some systems
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Prevent instantiation
    private GUILauncher() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SudokuGUIView();
            }
        });
    }
}
