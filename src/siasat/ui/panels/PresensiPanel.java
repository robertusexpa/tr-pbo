package siasat.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

import siasat.model.Jadwal;

public class PresensiPanel extends JPanel {
    private final Jadwal jadwal;

    public PresensiPanel(Jadwal jadwal) {
        this.jadwal = jadwal;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
    }

    private void initUI() {
        // ====== HEADER ======
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel title = new JLabel("Presensi");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 25, 0, 0));

        String mkLine = (jadwal.getKodeMk() != null ? jadwal.getKodeMk() : "") +
                " - " +
                (jadwal.getNamaMk() != null ? jadwal.getNamaMk() : "");
        JLabel mkLabel = new JLabel(mkLine);
        mkLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mkLabel.setBorder(BorderFactory.createEmptyBorder(5, 25, 0, 0));

        String jadwalLine =
                (jadwal.getHari() != null ? jadwal.getHari() : "") +
                " " +
                (jadwal.getJam() != null ? jadwal.getJam() : "");
        JLabel jadwalLabel = new JLabel(jadwalLine);
        jadwalLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        jadwalLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 15, 0));

        JPanel sub = new JPanel(new BorderLayout());
        sub.setBackground(Color.WHITE);
        sub.add(mkLabel, BorderLayout.NORTH);
        sub.add(jadwalLabel, BorderLayout.SOUTH);

        top.add(title, BorderLayout.NORTH);
        top.add(sub, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // ====== TABEL ======
        String[] cols = {"No","Pertemuan","Materi","Meet","Tugas","Jam Masuk","Aksi"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                // kolom Submit (4) & Hadir (6) bisa di-klik
                return c == 4 || c == 6;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(26);

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0,193,171));
        th.setForeground(Color.WHITE);
        th.setFont(th.getFont().deriveFont(Font.BOLD));

        // data dummy untuk 3 pertemuan (bisa kamu ganti nanti)
        Object[][] rows = {
                {1,"Pertemuan 1","Introduction to the Course, class rules, assessment, pembagian kelompok","1","Submit","29 Aug 2025 17:09:21","Hadir"},
                {2,"Pertemuan 2","Chapter 1. WHO AND WHAT IS ENTREPREUNER","2","Submit","12 Sep 2025 15:39:29","Hadir"},
                {3,"Pertemuan 3","Chapter 2. Entrepreneurial Personality","3","Submit","03 Oct 2025 16:18:19","Hadir"}
        };
        for (Object[] r : rows) model.addRow(r);

        // renderer & editor untuk tombol
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonCellRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonCellRenderer());

        table.getColumnModel().getColumn(4)
                .setCellEditor(new ButtonCellEditor(new JCheckBox(), table, "submit"));
        table.getColumnModel().getColumn(6)
                .setCellEditor(new ButtonCellEditor(new JCheckBox(), table, "hadir"));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        add(sp, BorderLayout.CENTER);
    }

    // ====== RENDERER TOMBOL ======
    static class ButtonCellRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonCellRenderer() {
            setOpaque(true);
            setBackground(new Color(0,193,171));
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(2,6,2,6));
            setFont(new Font("SansSerif", Font.PLAIN, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    // ====== EDITOR TOMBOL (AKSI) ======
    static class ButtonCellEditor extends DefaultCellEditor {
        private final JButton button;
        private final JTable table;
        private final String type; // "submit" atau "hadir"
        private String label;
        private boolean clicked;
        private int row;

        public ButtonCellEditor(JCheckBox checkBox, JTable table, String type) {
            super(checkBox);
            this.table = table;
            this.type = type;
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(0,193,171));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(2,6,2,6));
            button.setFont(new Font("SansSerif", Font.PLAIN, 12));

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int col) {
            label = value == null ? "" : value.toString();
            button.setText(label);
            clicked = true;
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                String pertemuan = table.getValueAt(row, 1).toString();
                if ("submit".equals(type)) {
                    JOptionPane.showMessageDialog(table,
                            "Berhasil submit tugas untuk " + pertemuan + ".",
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(table,
                            "Presensi berhasil untuk " + pertemuan + ".",
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
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
