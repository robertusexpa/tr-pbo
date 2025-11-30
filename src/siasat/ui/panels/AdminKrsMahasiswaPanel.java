package siasat.ui.panels;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

import siasat.config.DBConnection;
import siasat.ui.MainFrame;

public class AdminKrsMahasiswaPanel extends JPanel {
    private final MainFrame mainFrame;   // boleh null kalau dipakai stand-alone
    private JTable table;
    private DefaultTableModel model;

    // Konstruktor utama (dipakai dari MainFrame)
    public AdminKrsMahasiswaPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
        SwingUtilities.invokeLater(this::loadData);
    }

    // Konstruktor tambahan (kalau mau pakai tanpa MainFrame)
    public AdminKrsMahasiswaPanel() {
        this(null);
    }

    private void initUI() {
        // ================= HEADER =================
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel title = new JLabel("KRS Mahasiswa");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(25, 25, 0, 0));

        JLabel sub = new JLabel("Semester Ganjil 2025/2026");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setBorder(BorderFactory.createEmptyBorder(5, 25, 15, 0));

        top.add(title, BorderLayout.NORTH);
        top.add(sub, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // ================= TABEL =================
        String[] cols = {"No","NIM","Nama","Status","Wali Studi","Aksi"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                // hanya kolom "Aksi"
                return c == 5;
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0,193,171));
        th.setForeground(Color.WHITE);
        th.setFont(th.getFont().deriveFont(Font.BOLD));

        // kolom tombol
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor());

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        add(sp, BorderLayout.CENTER);
    }

    private void loadData() {
        try (Connection c = DBConnection.getConnection()) {
            String sql = """
                SELECT m.nim, m.nama
                FROM mahasiswa m
                JOIN krs k ON k.nim = m.nim
                GROUP BY m.nim, m.nama
                ORDER BY m.nim
                """;

            model.setRowCount(0);
            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                int no = 1;
                while (rs.next()) {
                    model.addRow(new Object[]{
                            no++,
                            rs.getString("nim"),
                            rs.getString("nama"),
                            "Diusulkan",
                            "PRATYAKSA OCSA N. SAIAN",   // placeholder wali studi
                            "Pilih"
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat KRS mahasiswa: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =============== RENDERER TOMBOL "Pilih" ===============
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setText("Pilih");
            setForeground(Color.BLACK);
            setBackground(new Color(0,193,171));
            setBorder(BorderFactory.createEmptyBorder(2,10,2,10));
            setFont(new Font("SansSerif", Font.PLAIN, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    // =============== EDITOR TOMBOL "Pilih" (AKSI KLIK) ===============
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button;
        private int row;

        public ButtonEditor() {
            button = new JButton("Pilih");
            button.setOpaque(true);
            button.setForeground(Color.BLACK);
            button.setBackground(new Color(0,193,171));
            button.setBorder(BorderFactory.createEmptyBorder(2,10,2,10));
            button.setFont(new Font("SansSerif", Font.PLAIN, 12));
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            String nim  = table.getValueAt(row, 1).toString();
            String nama = table.getValueAt(row, 2).toString();

            if (mainFrame != null) {
                // pindah ke halaman detail KRS
                mainFrame.showPanel(new AdminKrsDetailPanel(nim, nama));
            } else {
                // fallback kalau dipakai tanpa MainFrame
                JOptionPane.showMessageDialog(table,
                        "Detail KRS untuk: " + nim + " - " + nama,
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            return "Pilih";
        }
    }
}
