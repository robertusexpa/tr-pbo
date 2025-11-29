package siasat.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Simple styled button used in the sidebar to match screenshot design.
 */
public class SidebarButton extends JButton {
    private static final long serialVersionUID = 1L;

    public SidebarButton(String text) {
        super(text);
        init();
    }

    private void init() {
        setFocusPainted(false);
        setBackground(new Color(0,193,171));
        setForeground(Color.WHITE);
        setBorderPainted(false);
        setOpaque(true);
        setPreferredSize(new Dimension(180, 30));
        setFont(getFont().deriveFont(Font.BOLD, 13f));
    }

    /**
     * Mark the button as active (dark variant)
     */
    public void setActive(boolean active) {
        if (active) {
            setBackground(new Color(0,90,80)); // darker teal for active
        } else {
            setBackground(new Color(0,193,171));
        }
    }
}
