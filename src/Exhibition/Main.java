package Exhibition;

import Exhibition.gui.ExhibitionGUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Create and show the GUI
            ExhibitionGUI gui = new ExhibitionGUI();
            gui.setVisible(true);
            
            // Set window to appear centered on screen
            gui.setLocationRelativeTo(null);
            
            // Set default close operation
            gui.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        });
    }
}
