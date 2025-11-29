package siasat;

import javax.swing.SwingUtilities;
import siasat.ui.UserSelectionFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UserSelectionFrame().setVisible(true);
        });
    }
}
