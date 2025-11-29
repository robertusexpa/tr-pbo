package siasat.ui;

import javax.swing.*;
import java.awt.*;

public class UserSelectionFrame extends JFrame {

    public UserSelectionFrame() {
        setTitle("SiaSat - Pilih Pengguna");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // Bar hijau atas
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 193, 171));
        header.setPreferredSize(new Dimension(0, 60));
        root.add(header, BorderLayout.NORTH);

        // Tengah: 3 tombol abu-abu besar
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 260;
        gbc.ipady = 18;

        JButton bAdmin = createRoleButton("Admin");
        JButton bDosen = createRoleButton("Dosen");
        JButton bMhs   = createRoleButton("Mahasiswa");

        gbc.gridy = 0; center.add(bAdmin, gbc);
        gbc.gridy = 1; center.add(bDosen, gbc);
        gbc.gridy = 2; center.add(bMhs, gbc);

        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        // Aksi
        bAdmin.addActionListener(e -> {
            new LoginFrame("ADMIN").setVisible(true);
            dispose();
        });

        bDosen.addActionListener(e -> {
            new LoginFrame("DOSEN").setVisible(true);
            dispose();
        });

        bMhs.addActionListener(e -> {
            new LoginFrame("MAHASISWA").setVisible(true);
            dispose();
        });
    }

    private JButton createRoleButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(90, 90, 90));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 22)); // ← 22 (int), bukan 22f
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        return b;
    }
}
