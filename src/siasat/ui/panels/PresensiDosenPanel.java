package siasat.ui.panels;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PresensiDosenPanel extends JPanel {

    private final String nidn;
    private final String kodeMk;
    private final String namaMk;
    private final String jadwal;
    private DefaultTableModel model;
    private JTable table;

    private final DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

    public PresensiDosenPanel(String nidn, String kodeMk, String namaMk, String jadwal) {
        this.nidn = nidn;
        this.kodeMk = kodeMk;
        this.namaMk = namaMk;
        this.jadwal = jadwal;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
        seedDummy(); // contoh data awal
    }

    private void initUI() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        JLabel title = new JLabel("Presensi");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel info1 = new JLabel(kodeMk + " - " + namaMk);
        info1.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel info2 = new JLabel(jadwal);
        info2.setFont(new Font("SansSerif", Font.PLAIN, 14));

        top.add(title);
        top.add(Box.createVerticalStrut(8));
        top.add(info1);
        top.add(info2);

        add(top, BorderLayout.NORTH);

        // tabel
        String[] cols = {"No", "Pertemuan", "Materi", "Meet", "Tugas", "Jam Masuk", "Aksi"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                // Submit (col 4) & Aksi(Edit, col 6)
                return c == 4 || c == 6;
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0,193,171));
        th.setForeground(Color.WHITE);
        th.setFont(th.getFont().deriveFont(Font.BOLD));

        // tombol Submit
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4)
                .setCellEditor(new ButtonEditor(new JCheckBox(), "Submit"));

        // tombol Edit
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6)
                .setCellEditor(new ButtonEditor(new JCheckBox(), "Edit"));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 25, 10, 25));
        add(sp, BorderLayout.CENTER);

        // panel bawah: tombol Buat Presensi +
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 25, 20, 25));

        JButton btnAdd = new JButton("Buat Presensi  +");
        btnAdd.setBackground(new Color(0,193,171));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        btnAdd.addActionListener(e -> addNewRowDialog());

        bottom.add(btnAdd, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    private void seedDummy() {
        // contoh 2 pertemuan awal
        model.addRow(new Object[]{
                1, "Pertemuan 1",
                "Introduction to the Course, class rules, assessment, pembagian kelompok",
                "", "Submit",
                "29 Aug 2025 17:09:21", "Edit"
        });
        model.addRow(new Object[]{
                2, "Pertemuan 2",
                "Chapter 1. WHO AND WHAT IS ENTREPREUNER",
                "", "Submit",
                "", "Edit"
        });
    }

    // dialog tambah presensi
    private void addNewRowDialog() {
        JTextField tfPert = new JTextField("Pertemuan " + (model.getRowCount() + 1));
        JTextField tfMateri = new JTextField();
        JTextField tfMeet = new JTextField();
        JTextField tfTugas = new JTextField();

        JPanel p = new JPanel(new GridLayout(0,1,4,4));
        p.add(new JLabel("Pertemuan:"));
        p.add(tfPert);
        p.add(new JLabel("Materi:"));
        p.add(tfMateri);
        p.add(new JLabel("Meet (opsional):"));
        p.add(tfMeet);
        p.add(new JLabel("Tugas (opsional):"));
        p.add(tfTugas);

        int result = JOptionPane.showConfirmDialog(this, p,
                "Buat Presensi Baru", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int no = model.getRowCount() + 1;
            model.addRow(new Object[]{
                    no,
                    tfPert.getText().trim(),
                    tfMateri.getText().trim(),
                    tfMeet.getText().trim(),
                    "Submit",
                    "", // jam masuk kosong sampai ditekan Submit
                    "Edit"
            });
        }
    }

    // ---- renderer generic (Submit & Edit) ----
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(2,10,2,10));
            setFont(new Font("SansSerif", Font.PLAIN, 12));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int col) {
            setText(value == null ? "" : value.toString());
            if ("Edit".equalsIgnoreCase(getText())) {
                setBackground(new Color(0,160,140));
            } else {
                setBackground(new Color(0,193,171));
            }
            return this;
        }
    }

    // ---- editor generic ----
    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean clicked;
        private int row;
        private int column;

        public ButtonEditor(JCheckBox cb, String defaultLabel) {
            super(cb);
            this.button = new JButton(defaultLabel);
            this.button.setOpaque(true);
            this.button.setForeground(Color.WHITE);
            this.button.setBorder(BorderFactory.createEmptyBorder(2,10,2,10));
            this.button.setFont(new Font("SansSerif", Font.PLAIN, 12));
            this.button.addActionListener(e -> fireEditingStopped());
        }

        public Component getCellEditorComponent(JTable table, Object value,
                                                boolean isSelected, int row, int column) {
            this.label = value == null ? "" : value.toString();
            this.row = row;
            this.column = column;
            button.setText(label);
            if ("Edit".equalsIgnoreCase(label)) {
                button.setBackground(new Color(0,160,140));
            } else {
                button.setBackground(new Color(0,193,171));
            }
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                if (column == 4) { // Submit
                    // isi jam masuk dengan waktu sekarang
                    String now = LocalDateTime.now().format(fmt);
                    model.setValueAt(now, row, 5);
                    JOptionPane.showMessageDialog(PresensiDosenPanel.this,
                            "Presensi berhasil disubmit.",
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else if (column == 6) { // Edit
                    editRow(row);
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

    private void editRow(int r) {
        String pertemuan = String.valueOf(model.getValueAt(r, 1));
        String materi    = String.valueOf(model.getValueAt(r, 2));
        String meet      = String.valueOf(model.getValueAt(r, 3));
        String tugas     = String.valueOf(model.getValueAt(r, 4));

        JTextField tfPert  = new JTextField(pertemuan);
        JTextField tfMateri= new JTextField(materi);
        JTextField tfMeet  = new JTextField(meet);
        JTextField tfTugas = new JTextField(tugas);

        JPanel p = new JPanel(new GridLayout(0,1,4,4));
        p.add(new JLabel("Pertemuan:"));
        p.add(tfPert);
        p.add(new JLabel("Materi:"));
        p.add(tfMateri);
        p.add(new JLabel("Meet:"));
        p.add(tfMeet);
        p.add(new JLabel("Tugas:"));
        p.add(tfTugas);

        int res = JOptionPane.showConfirmDialog(this, p,
                "Edit Presensi", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            model.setValueAt(tfPert.getText().trim(),   r, 1);
            model.setValueAt(tfMateri.getText().trim(), r, 2);
            model.setValueAt(tfMeet.getText().trim(),   r, 3);
            model.setValueAt("Submit",                  r, 4); // tetap tombol
        }
    }
}
