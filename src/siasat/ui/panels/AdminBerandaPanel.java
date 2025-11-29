package siasat.ui.panels;

import javax.swing.*;
import java.awt.*;

public class AdminBerandaPanel extends JPanel {

    public AdminBerandaPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
    }

    private void initUI() {
        // =========================
        // HEADER ATAS
        // =========================
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel title = new JLabel(
                "Sistem informasi Akademik Universitas Kristen Satya Wacana - Admin");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(25, 25, 0, 0));

        JLabel sub = new JLabel("Semester Genap 2025/2026  -  Teknik Informatika");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setBorder(BorderFactory.createEmptyBorder(5, 25, 15, 0));

        top.add(title, BorderLayout.NORTH);
        top.add(sub, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // =========================
        // CARD BESAR (PENGUMUMAN + LOGO)
        // =========================
        JPanel card = new JPanel(new GridLayout(1, 2));
        card.setBackground(new Color(224, 255, 252));
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // ----- BAGIAN KIRI: PENGUMUMAN -----
        JLabel info = new JLabel("<html>"
                + "<h2>🔔 Pengumuman Terbaru</h2>"
                + "<ul>"
                + "<li><b>Jadwal buka KRS</b><br/>10 Desember 2025</li><br/>"
                + "<li><b>Jadwal Input Nilai</b><br/>11 – 15 Desember 2025</li><br/>"
                + "<li><b>Info Cuti Sistem</b><br/>20 Desember 2025 – 1 Januari 2026</li>"
                + "</ul>"
                + "</html>");
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));
        info.setVerticalAlignment(SwingConstants.TOP);

        // ----- BAGIAN KANAN: LOGO -----
        JLabel logo = new JLabel();
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/siasat/assets/logo_uksw.jpg")
            );
            Image scaled = icon.getImage().getScaledInstance(240, 240, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            logo.setText("LOGO UKSW");
            logo.setFont(new Font("SansSerif", Font.BOLD, 16));
        }

        card.add(info);
        card.add(logo);

        // =========================
        // WRAPPER TENGAH
        // =========================
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));
        wrapper.add(card, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }
}
