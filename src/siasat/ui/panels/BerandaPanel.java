package siasat.ui.panels;

import javax.swing.*;
import java.awt.*;

public class BerandaPanel extends JPanel {
    private final String username;

    public BerandaPanel(String username) {
        this.username = username;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        init();
    }

    private void init() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel title = new JLabel("Sistem informasi Akademik Universitas Kristen Satya Wacana");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(25, 25, 0, 0));

        JLabel sub = new JLabel("Semester Genap 2025/2026  -  Teknik Informatika");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setBorder(BorderFactory.createEmptyBorder(5, 25, 15, 0));

        top.add(title, BorderLayout.NORTH);
        top.add(sub, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // kartu besar di tengah (pengumuman + logo)
        JPanel card = new JPanel();
        card.setBackground(new Color(224, 255, 252)); // hijau muda
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        card.setLayout(new GridLayout(1, 2));

        JLabel info = new JLabel("<html>" +
                "<h3>Pengumuman terbaru</h3>" +
                "<ul>" +
                "<li>Selamat datang, <b>" + username + "</b></li>" +
                "<li>Silakan cek menu Jadwal kuliah dan Hasil Studi.</li>" +
                "</ul>" +
                "</html>");
        info.setVerticalAlignment(SwingConstants.TOP);

// ===== LOGO UKSW (GAMBAR ASLI) =====
        ImageIcon logoIcon = new ImageIcon(
                getClass().getResource("/siasat/assets/logo_uksw.jpg")
        );

        // Resize agar proporsional
        Image logoImg = logoIcon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // masukkan ke card
        card.add(info);
        card.add(logoLabel);


        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));
        wrapper.add(card, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }
}
