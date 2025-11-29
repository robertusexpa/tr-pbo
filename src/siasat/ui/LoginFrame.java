package siasat.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import siasat.config.DBConnection;
import siasat.ui.panels.*;
import siasat.model.Dosen;
import siasat.model.Mahasiswa;
import siasat.dao.DosenDao;
import siasat.dao.MahasiswaDao;

public class LoginFrame extends JFrame {

    private final String role;      // "ADMIN" / "DOSEN" / "MAHASISWA"
    private JTextField txtUser;
    private JPasswordField txtPass;

    // dipakai dari UserSelectionFrame
    public LoginFrame(String role) {
        this.role = role;                  // ← PENTING: jangan lupa this.role
        setTitle("SiaSat - Login " + role);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
    }

    // opsional: kalau masih ada pemanggilan new LoginFrame() lama
    public LoginFrame() {
        this("MAHASISWA"); // default ke mahasiswa
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // header hijau
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 193, 171));
        header.setPreferredSize(new Dimension(0, 60));
        root.add(header, BorderLayout.NORTH);

        // form login di tengah
        JPanel center = new JPanel(null);
        center.setBackground(Color.WHITE);

        JLabel lblUser = new JLabel(role.equalsIgnoreCase("DOSEN") ? "NIDN" :
                                    role.equalsIgnoreCase("ADMIN") ? "Username"
                                                                   : "NIM");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblUser.setBounds(260, 250, 150, 40);
        center.add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(380, 260, 260, 32);
        txtUser.setBorder(BorderFactory.createLineBorder(new Color(0,193,171), 2));
        center.add(txtUser);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblPass.setBounds(260, 310, 150, 40);
        center.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(380, 320, 260, 32);
        txtPass.setBorder(BorderFactory.createLineBorder(new Color(0,193,171), 2));
        center.add(txtPass);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(380, 370, 120, 34);
        btnLogin.setBackground(new Color(0,193,171));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        center.add(btnLogin);

        JLabel lupa = new JLabel("<html><i>Lupa password?</i></html>");
        lupa.setForeground(Color.RED);
        lupa.setBounds(380, 410, 200, 20);
        center.add(lupa);

        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        btnLogin.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();

        System.out.println("DEBUG: role=" + role + ", user=" + user + ", pass=" + pass);

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi username dan password.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection c = DBConnection.getConnection()) {

            if ("ADMIN".equalsIgnoreCase(role)) {
                // admin hardcode / atau pakai tabel sendiri
                if ("admin".equalsIgnoreCase(user) && "admin123".equals(pass)) {
                    new MainFrame("admin", "ADMIN").setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Username / password admin salah");
                }

            } else if ("DOSEN".equalsIgnoreCase(role)) {
                // login dosen pakai tabel DOSEN
                PreparedStatement ps = c.prepareStatement(
                        "SELECT * FROM dosen WHERE nidn = ? AND password = ?");
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    new MainFrame(user, "DOSEN").setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "NIDN / password dosen salah");
                }

            } else if ("MAHASISWA".equalsIgnoreCase(role)) {
                // login mahasiswa pakai tabel MAHASISWA
                PreparedStatement ps = c.prepareStatement(
                        "SELECT * FROM mahasiswa WHERE nim = ? AND password = ?");
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    new MainFrame(user, "MAHASISWA").setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "NIM / password mahasiswa salah");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Terjadi error saat login: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
