package siasat.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import siasat.config.DBConnection;

public class MataKuliahPanel extends JPanel {
    private final String nim;
    private JTable table;
    private DefaultTableModel model;

    public MataKuliahPanel(String nim) {
        this.nim = nim;
        System.out.println("DEBUG: MataKuliahPanel dibuka untuk NIM = " + nim);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        init();
    }

    private void init() {
        // ===== HEADER =====
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel title = new JLabel("Pengambilan Matakuliah");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 25, 0, 0));

        JLabel sub = new JLabel("silahkan pilih matakuliah yang ingin anda ambil semester ini");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setBorder(BorderFactory.createEmptyBorder(5, 25, 0, 0));

        JLabel sub2 = new JLabel("Semester Ganjil 2025/2026                     Maksimal SKS: 24");
        sub2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub2.setBorder(BorderFactory.createEmptyBorder(0, 25, 15, 25));

        JPanel subPanel = new JPanel(new BorderLayout());
        subPanel.setBackground(Color.WHITE);
        subPanel.add(sub, BorderLayout.NORTH);
        subPanel.add(sub2, BorderLayout.SOUTH);

        top.add(title, BorderLayout.NORTH);
        top.add(subPanel, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // ===== TABEL =====
        String[] cols = {"No","Kode","Matakuliah","SKS","Kelas","Jadwal","Dosen","Aksi"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                // hanya kolom Aksi yang bisa diklik
                return c == 7;
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0,193,171));
        th.setForeground(Color.WHITE);
        th.setFont(th.getFont().deriveFont(Font.BOLD));

        // renderer & editor khusus untuk tombol Ambil / Penuh
        table.getColumnModel().getColumn(7).setCellRenderer(new AksiButtonRenderer());
        table.getColumnModel().getColumn(7)
                .setCellEditor(new AksiButtonEditor());

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        add(sp, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::loadData);
    }

    private void loadData() {
        System.out.println("DEBUG: loadData Matakuliah dipanggil...");
        try (Connection c = DBConnection.getConnection()) {

            // id_kelas yang SUDAH diambil oleh nim ini
            Set<Integer> krsKelas = new HashSet<>();
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT id_kelas FROM krs WHERE nim = ?")) {
                ps.setString(1, nim);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        krsKelas.add(rs.getInt(1));
                    }
                }
            }

            String sql = """
                SELECT k.id_kelas, mk.kode_matkul, mk.nama_matkul, mk.sks,
                       k.kode_kelas,
                       CONCAT(j.hari, ' ', TIME_FORMAT(j.jam_mulai,'%H:%i'),
                              '-', TIME_FORMAT(j.jam_selesai,'%H:%i')) AS jadwal,
                       d.nama AS nama_dosen
                FROM kelas k
                JOIN mata_kuliah mk ON k.id_matkul = mk.id_matkul
                LEFT JOIN jadwal j  ON j.id_kelas = k.id_kelas
                LEFT JOIN dosen d   ON d.nidn = k.nidn_dosen
                ORDER BY mk.nama_matkul
                """;

            model.setRowCount(0);
            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                int no = 1;
                while (rs.next()) {
                    int idKelas = rs.getInt("id_kelas");
                    String aksi = krsKelas.contains(idKelas) ? "Penuh" : "Ambil";

                    model.addRow(new Object[]{
                            no++,
                            rs.getString("kode_matkul"),
                            rs.getString("nama_matkul"),
                            rs.getInt("sks"),
                            rs.getString("kode_kelas"),
                            rs.getString("jadwal"),
                            rs.getString("nama_dosen"),
                            aksi
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat daftar matakuliah: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Renderer tombol hijau / merah =====
    class AksiButtonRenderer extends JButton implements TableCellRenderer {
        public AksiButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(2,10,2,10));
            setFont(new Font("SansSerif", Font.PLAIN, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            String text = value == null ? "" : value.toString();
            setText(text);

            if ("Ambil".equalsIgnoreCase(text)) {
                setBackground(new Color(0,193,171));   // hijau / teal
            } else { // Penuh
                setBackground(new Color(220, 0, 0));   // merah
            }
            return this;
        }
    }

    // ===== Editor tombol (aksi klik) =====
    class AksiButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button;
        private String label;
        private int row;

        public AksiButtonEditor() {
            button = new JButton();
            button.setOpaque(true);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(2,10,2,10));
            button.setFont(new Font("SansSerif", Font.PLAIN, 12));
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = value == null ? "" : value.toString();
            button.setText(label);

            if ("Ambil".equalsIgnoreCase(label)) {
                button.setBackground(new Color(0,193,171)); // hijau
            } else {
                button.setBackground(new Color(220, 0, 0)); // merah
            }

            this.row = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (label != null) {
                String kode = table.getValueAt(row, 1).toString();
                String nama = table.getValueAt(row, 2).toString();

                if ("Ambil".equalsIgnoreCase(label)) {
                    JOptionPane.showMessageDialog(table,
                            "Berhasil mengambil mata kuliah: " + kode + " - " + nama,
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(table,
                            "Kelas yang kamu ambil telah penuh, silahkan pilih kelas lain.",
                            "Informasi", JOptionPane.WARNING_MESSAGE);
                }
            }
            return label;
        }
    }
}
