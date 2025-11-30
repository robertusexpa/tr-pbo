package siasat.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel Pengambilan Matakuliah (mode ADMIN)
 * Tombol "CEK" hanya menampilkan notifikasi.
 */
public class AdminMatakuliahPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public AdminMatakuliahPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
        loadData();
    }

    private void initUI() {
        // ===== HEADER =====
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel title = new JLabel("Pengambilan Matakuliah");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 25, 0, 0));

        JLabel sub = new JLabel("silahkan pilih matakuliah yang ingin anda ambil semester ini");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setBorder(BorderFactory.createEmptyBorder(5, 25, 0, 0));

        JLabel sub2 = new JLabel("Semester Ganjil 2025/2026                               Maksimal SKS: 24");
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
        String[] cols = {"No", "Kode", "Matakuliah", "SKS", "Kelas", "Jadwal", "Dosen", "Aksi"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // hanya kolom Aksi yg bisa diklik
                return column == 7;
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0, 193, 171));
        th.setForeground(Color.WHITE);
        th.setFont(th.getFont().deriveFont(Font.BOLD));

        // kolom Aksi pakai renderer & editor tombol CEK
        table.getColumnModel().getColumn(7).setCellRenderer(new CekButtonRenderer());
        table.getColumnModel().getColumn(7)
                .setCellEditor(new CekButtonEditor(new JCheckBox()));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        add(sp, BorderLayout.CENTER);
    }

    private void loadData() {
        // TODO: ganti dengan query DB sebenarnya
        List<Object[]> dummy = new ArrayList<>();
        dummy.add(new Object[]{1, "MU110H", "Kewirausahaan H", 3, "A103", "Jumat 16-19", "ALIA KATHARINA", "CEK"});
        dummy.add(new Object[]{2, "TC212G", "Pemrograman Berorientasi Objek A", 4, "FTI300", "Senin 10-13", "PRATYAKSA OCSA N. SAIAN", "CEK"});
        dummy.add(new Object[]{3, "MU110H", "Ast Pemrograman Web G", 4, "FTI403", "Senin 15-17", "DANANG TRI WIDIATMOKO", "CEK"});
        dummy.add(new Object[]{4, "MU110H", "Pemrograman Web G", 4, "FTI455", "Rabu 16-18", "ASISTEN 10", "CEK"});
        dummy.add(new Object[]{5, "MU111B", "Pancasila B", 3, "F103", "Kamis 07-09", "HANI SIRINE", "CEK"});

        model.setRowCount(0);
        for (Object[] row : dummy) {
            model.addRow(row);
        }
    }

    // ======================= RENDERER =======================

    class CekButtonRenderer extends JButton implements TableCellRenderer {

        public CekButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
            setFont(new Font("SansSerif", Font.PLAIN, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            String text = (value == null) ? "" : value.toString();
            setText(text);

            // di desain, baris terakhir "CEK" warnanya abu, lainnya hijau
            if (row == table.getRowCount() - 1) {
                setBackground(new Color(180, 180, 180)); // abu
            } else {
                setBackground(new Color(0, 193, 171));   // hijau
            }
            return this;
        }
    }

    // ======================== EDITOR ========================

    class CekButtonEditor extends DefaultCellEditor {

        private final JButton button;
        private JTable tableRef;
        private int row;
        private String label;
        private boolean clicked;

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
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.tableRef = table;
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);

            if (row == table.getRowCount() - 1) {
                button.setBackground(new Color(180, 180, 180));
            } else {
                button.setBackground(new Color(0, 193, 171));
            }

            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                String kode = tableRef.getValueAt(row, 1).toString();
                String nama = tableRef.getValueAt(row, 2).toString();

                JOptionPane.showMessageDialog(button,
                        "Cek detail matakuliah: " + kode + " - " + nama,
                        "Cek Matakuliah", JOptionPane.INFORMATION_MESSAGE);
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
