package siasat.ui;

import javax.swing.*;
import java.awt.*;

import siasat.ui.components.SidebarButton;
import siasat.ui.panels.*;   // pastikan ini ada, supaya semua panel ke-import

public class MainFrame extends JFrame {

    private final String role;      // "ADMIN" / "DOSEN" / "MAHASISWA"
    private final String username;  // nim / nidn / admin

    private JPanel contentPanel;

    private SidebarButton btnHome;
    private SidebarButton btnMatkul;
    private SidebarButton btnJadwal;
    private SidebarButton btnHasil;
    private SidebarButton btnTrans;

    public MainFrame(String username, String role) {
        this.username = username;
        this.role = role;

        setTitle("SiaSat - " + role + " - " + username);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());

        // ====== HEADER HIJAU ATAS ======
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 193, 171));
        header.setPreferredSize(new Dimension(0, 60));
        getContentPane().add(header, BorderLayout.NORTH);

        // ====== SIDEBAR KIRI ======
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(new Color(230, 230, 230));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        getContentPane().add(sidebar, BorderLayout.WEST);

        // ====== AREA KONTEN ======
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // ====== TOMBOL LOGOUT (SAMA UNTUK SEMUA ROLE) ======
        JButton logout = new JButton("Log out");
        logout.setBounds(40, 560, 140, 34);
        logout.setBackground(Color.RED);
        logout.setForeground(Color.WHITE);
        logout.setFocusPainted(false);
        sidebar.add(logout);

        logout.addActionListener(e -> {
            dispose();
            new UserSelectionFrame().setVisible(true);
        });

        // ====== CABANG SIDEBAR BERDASARKAN ROLE ======
        switch (role.toUpperCase()) {
            case "ADMIN":
                buildSidebarForAdmin(sidebar);
                setActive(btnHome);
                showPanel(new AdminBerandaPanel());
                break;

            case "DOSEN":
                buildSidebarForDosen(sidebar);
                setActive(btnHome);
                showPanel(new BerandaPanel(username));   // beranda dosen
                break;

            default: // MAHASISWA
                buildSidebarForMahasiswa(sidebar);
                setActive(btnHome);
                showPanel(new BerandaPanel(username));
                break;
        }

        revalidate();
        repaint();
    }

    // ==========================
    // SIDEBAR MAHASISWA
    // ==========================
    private void buildSidebarForMahasiswa(JPanel sidebar) {
        btnHome   = new SidebarButton("Beranda");
        btnMatkul = new SidebarButton("Matakuliah");
        btnJadwal = new SidebarButton("Jadwal kuliah");
        btnHasil  = new SidebarButton("Hasil Studi");
        btnTrans  = new SidebarButton("Transkrip nilai");

        btnHome.setBounds(20, 80, 180, 32);
        btnMatkul.setBounds(20, 120, 180, 32);
        btnJadwal.setBounds(20, 160, 180, 32);
        btnHasil.setBounds(20, 200, 180, 32);
        btnTrans.setBounds(20, 240, 180, 32);

        sidebar.add(btnHome);
        sidebar.add(btnMatkul);
        sidebar.add(btnJadwal);
        sidebar.add(btnHasil);
        sidebar.add(btnTrans);

        btnHome.addActionListener(e -> {
            setActive(btnHome);
            showPanel(new BerandaPanel(username));
        });

        btnMatkul.addActionListener(e -> {
            setActive(btnMatkul);
            showPanel(new MataKuliahPanel(username));
        });

        btnJadwal.addActionListener(e -> {
            setActive(btnJadwal);
            showPanel(new JadwalPanel(username, role));
        });

        btnHasil.addActionListener(e -> {
            setActive(btnHasil);
            showPanel(new HasilStudiPanel(username));
        });

        btnTrans.addActionListener(e -> {
            setActive(btnTrans);
            showPanel(new TranskripPanel(username));
        });
    }

    // ==========================
    // SIDEBAR DOSEN
    // ==========================
    private void buildSidebarForDosen(JPanel sidebar) {
        btnHome   = new SidebarButton("Beranda");
        btnMatkul = new SidebarButton("Matakuliah");
        btnJadwal = new SidebarButton("Jadwal Mengajar");
        btnHasil  = new SidebarButton("Hasil Studi");
        btnTrans  = new SidebarButton("Transkrip nilai");

        btnHome.setBounds(20, 80, 180, 32);
        btnMatkul.setBounds(20, 120, 180, 32);
        btnJadwal.setBounds(20, 160, 180, 32);
        btnHasil.setBounds(20, 200, 180, 32);
        btnTrans.setBounds(20, 240, 180, 32);

        sidebar.add(btnHome);
        sidebar.add(btnMatkul);
        sidebar.add(btnJadwal);
        sidebar.add(btnHasil);
        sidebar.add(btnTrans);

        btnHome.addActionListener(e -> {
            setActive(btnHome);
            showPanel(new BerandaPanel(username)); // kalau mau, bisa bikin BerandaDosenPanel
        });

        btnMatkul.addActionListener(e -> {
            setActive(btnMatkul);
            showPanel(new AdminMatakuliahPanel());   // list kelas / cek KRS
        });

        btnJadwal.addActionListener(e -> {
            setActive(btnJadwal);
            showPanel(new JadwalMengajarPanel(username));
        });

        btnHasil.addActionListener(e -> {
            setActive(btnHasil);
            showPanel(new DosenHasilStudiPanel(username));
        });

        btnTrans.addActionListener(e -> {
            setActive(btnTrans);
            showPanel(new DosenTranskripPanel(username));
        });
    }

    // ==========================
    // SIDEBAR ADMIN
    // ==========================
    private void buildSidebarForAdmin(JPanel sidebar) {
        btnHome   = new SidebarButton("Beranda");
        btnMatkul = new SidebarButton("Matakuliah");
        btnJadwal = new SidebarButton("KRS Mahasiswa");
        btnHasil  = null;
        btnTrans  = null;

        btnHome.setBounds(20, 80, 180, 32);
        btnMatkul.setBounds(20, 120, 180, 32);
        btnJadwal.setBounds(20, 160, 180, 32);

        sidebar.add(btnHome);
        sidebar.add(btnMatkul);
        sidebar.add(btnJadwal);

        // === ACTION LISTENER ===
        btnHome.addActionListener(e -> {
            setActive(btnHome);
            showPanel(new AdminBerandaPanel());          // <- kalau konstruktor TANPA parameter
            // atau: showPanel(new AdminBerandaPanel(username));
        });

        btnMatkul.addActionListener(e -> {
            setActive(btnMatkul);
            showPanel(new AdminMatakuliahPanel());       // <- atau (username) kalau butuh
        });

        btnJadwal.addActionListener(e -> {
            setActive(btnJadwal);
            showPanel(new AdminKrsMahasiswaPanel());     // <- atau (username) kalau butuh
        });
    }


    // ==========================
    // UTILITAS
    // ==========================
    private void setActive(SidebarButton active) {
        if (btnHome   != null) btnHome.setActive(btnHome == active);
        if (btnMatkul != null) btnMatkul.setActive(btnMatkul == active);
        if (btnJadwal != null) btnJadwal.setActive(btnJadwal == active);
        if (btnHasil  != null) btnHasil.setActive(btnHasil == active);
        if (btnTrans  != null) btnTrans.setActive(btnTrans == active);
    }

    public void showPanel(JPanel p) {
        contentPanel.removeAll();
        contentPanel.add(p, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
