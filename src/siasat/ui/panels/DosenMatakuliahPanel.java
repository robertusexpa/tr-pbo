package siasat.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import siasat.config.DBConnection;

public class DosenMatakuliahPanel extends JPanel {

    private final String nidn;   // username dosen
    private JTable table;
    private DefaultTableModel model;

    public DosenMatakuliahPanel(String nidn) {
        this.nidn = nidn;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
    }

    private void initUI() {
        // ===== HEADER ATAS =====
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel title = new JLabel("Pengambilan Matakuliah");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(25, 25, 0, 0));

        JLabel sub = new JLabel("silahkan pilih matakuliah yang ingin anda ambil semester ini");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setBorder(BorderFactory.createEmptyBorder(5, 25, 0, 0));

        JPanel leftText = new JPanel(new BorderLayout());
        leftText.setBackground(Color.WHITE);
        leftText.add(title, BorderLayout.NORTH);
        leftText.add(sub, BorderLayout.CENTER);

        JLabel sem = new JLabel("Semester Ganjil 2025/2026");
        sem.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sem.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 0));

        JLabel maxSks = new JLabel("Maksimal SKS: 24");
        maxSks.setFont(new Font("SansSerif", Font.PLAIN, 14));
        maxSks.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 25));
        maxSks.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel mid = new JPanel(new BorderLayout());
        mid.setBackground(Color.WHITE);
        mid.add(sem, BorderLayout.WEST);
        mid.add(maxSks, BorderLayout.EAST);

        JPanel wrapperTitle = new JPanel(new BorderLayout());
        wrapperTitle.setBackground(Color.WHITE);
        wrapperTitle.add(leftText, BorderLayout.WEST);
        wrapperTitle.add(mid, BorderLayout.SOUTH);

        top.add(wrapperTitle, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // ===== TABEL =====
        String[] cols = {
                "No", "Kode", "Matakuliah", "SKS", "Kelas", "Jadwal", "Dosen", "Aksi"
        };

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // hanya kolom Aksi yang bisa diklik
                return column == 7;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 193, 171));
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        // kolom Aksi pakai tombol CEK
        table.getColumnModel().getColumn(7).setCellRenderer(new CekButtonRenderer());
        table.getColumnModel().getColumn(7)
                .setCellEditor(new CekButtonEditor(new JCheckBox()));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));
        add(sp, BorderLayout.CENTER);

        // load data setelah komponen siap
        SwingUtilities.invokeLater(this::loadData);
    }

    private void loadData() {
        model.setRowCount(0);

        try (Connection c = DBConnection.getConnection()) {
            String sql = """
                    SELECT mk.kode_matkul,
                           mk.nama_matkul,
                           mk.sks,
                           k.kode_kelas,
                           CONCAT(j.hari, ' ',
                                  TIME_FORMAT(j.jam_mulai,'%H:%i'),
                                  '-', TIME_FORMAT(j.jam_selesai,'%H:%i')) AS jadwal,
                           d.nama AS nama_dosen
                    FROM kelas k
                    JOIN mata_kuliah mk ON k.id_matkul = mk.id_matkul
                    LEFT JOIN jadwal j  ON j.id_kelas = k.id_kelas
                    LEFT JOIN dosen d   ON d.nidn = k.nidn_dosen
                    WHERE d.nidn = ?
                    ORDER BY mk.nama_matkul
                    """;

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, nidn);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                no++,
                                rs.getString("kode_matkul"),
                                rs.getString("nama_matkul"),
                                rs.getInt("sks"),
                                rs.getString("kode_kelas"),
                                rs.getString("jadwal"),
                                rs.getString("nama_dosen"),
                                "CEK"
                        });
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data matakuliah dosen: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================== RENDERER TOMBOL CEK ==================
    class CekButtonRenderer extends JButton implements TableCellRenderer {

        public CekButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
            setFont(new Font("SansSerif", Font.PLAIN, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row, int column) {
            String text = value == null ? "" : value.toString();
            setText(text);

            // warna teal seperti desain
            setBackground(new Color(0, 193, 171));

            return this;
        }
    }

    // ================== EDITOR TOMBOL CEK (AKSI KLIK) ==================
    class CekButtonEditor extends DefaultCellEditor {

        private final JButton button;
        private String label;
        private boolean clicked;
        private int row;

        public CekButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
            button.setFont(new Font("SansSerif", Font.PLAIN, 12));
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {
            label = value == null ? "" : value.toString();
            button.setText(label);
            button.setBackground(new Color(0, 193, 171)); // teal

            clicked = true;
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                // ambil data baris untuk pesan
                String kode = table.getValueAt(row, 1).toString();
                String nama = table.getValueAt(row, 2).toString();

                // Sementara hanya tampilkan pesan.
                // Nanti bisa diganti buka panel lain (misal: daftar KRS mahasiswa).
                JOptionPane.showMessageDialog(button,
                        "CEK KRS untuk matakuliah:\n" +
                                kode + " - " + nama,
                        "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }
            clicked = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}
