package siasat.ui.panels;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

import siasat.config.DBConnection;

public class AdminKrsDetailPanel extends JPanel {
    private final String nim;
    private final String nama;
    private JTable table;
    private DefaultTableModel model;

    public AdminKrsDetailPanel(String nim, String nama) {
        this.nim = nim;
        this.nama = nama;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
        SwingUtilities.invokeLater(this::loadData);
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel title = new JLabel("KRS");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(25, 25, 0, 0));

        JLabel info = new JLabel("<html>" + nama + "<br/>" + nim + "</html>");
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));
        info.setBorder(BorderFactory.createEmptyBorder(5, 25, 15, 0));

        top.add(title, BorderLayout.NORTH);
        top.add(info, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        String[] cols = {"No","Kode","Matakuliah","SKS","Jadwal","Status","Setuju","Tolak"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 6 || c == 7; }
        };
        table = new JTable(model);
        table.setRowHeight(26);

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0,193,171));
        th.setForeground(Color.WHITE);
        th.setFont(th.getFont().deriveFont(Font.BOLD));

        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer("Setuju", new Color(0,193,171)));
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor("Setuju"));

        table.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer("Tolak", Color.RED));
        table.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor("Tolak"));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        add(sp, BorderLayout.CENTER);
    }

    private void loadData() {
        try (Connection c = DBConnection.getConnection()) {
            String sql = """
                SELECT mk.kode_matkul, mk.nama_matkul, mk.sks,
                       CONCAT(j.hari, ' ', TIME_FORMAT(j.jam_mulai,'%H:%i'),
                              '-', TIME_FORMAT(j.jam_selesai,'%H:%i')) AS jadwal
                FROM krs k
                JOIN kelas kls ON k.id_kelas = kls.id_kelas
                JOIN mata_kuliah mk ON kls.id_matkul = mk.id_matkul
                LEFT JOIN jadwal j  ON j.id_kelas = kls.id_kelas
                WHERE k.nim = ?
                """;
            model.setRowCount(0);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, nim);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                no++,
                                rs.getString("kode_matkul"),
                                rs.getString("nama_matkul"),
                                rs.getInt("sks"),
                                rs.getString("jadwal"),
                                "Diusulkan",
                                "Setuju",
                                "Tolak"
                        });
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat detail KRS: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // renderer umum untuk tombol Setuju/Tolak
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text, Color bg) {
            setText(text);
            setOpaque(true);
            setForeground(Color.WHITE);
            setBackground(bg);
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

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button;
        private final String action;
        private int row;

        public ButtonEditor(String action) {
            this.action = action;
            button = new JButton(action);
            button.setOpaque(true);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(2,10,2,10));
            button.setFont(new Font("SansSerif", Font.PLAIN, 12));
            if ("Setuju".equals(action)) {
                button.setBackground(new Color(0,193,171));
            } else {
                button.setBackground(Color.RED);
            }
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
            String kode = table.getValueAt(row, 1).toString();
            String namaMk = table.getValueAt(row, 2).toString();

            if ("Setuju".equals(action)) {
                table.setValueAt("Disetujui", row, 5);
                JOptionPane.showMessageDialog(table,
                        "KRS disetujui untuk matakuliah:\n" + kode + " - " + namaMk,
                        "KRS Disetujui", JOptionPane.INFORMATION_MESSAGE);
            } else {
                table.setValueAt("Ditolak", row, 5);
                JOptionPane.showMessageDialog(table,
                        "KRS DITOLAK untuk matakuliah:\n" + kode + " - " + namaMk,
                        "KRS Ditolak", JOptionPane.WARNING_MESSAGE);
            }
            return action;
        }
    }
}
