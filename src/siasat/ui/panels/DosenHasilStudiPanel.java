package siasat.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class DosenHasilStudiPanel extends JPanel {
    private final String nidn;

    public DosenHasilStudiPanel(String nidn) {
        this.nidn = nidn;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel title = new JLabel("Penilaian");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20,25,0,0));

        top.add(title, BorderLayout.NORTH);
        add(top, BorderLayout.NORTH);

        String[] cols = {"No","NIM","Nama","UTS","UAS","Tugas","Nilai Akhir"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c){ return false; }
        };

        // contoh data dummy (silakan ganti dengan query DB nanti)
        model.addRow(new Object[]{1,"672024077","Robertus Expa Abdhi Guna",85,77,90,"A"});
        model.addRow(new Object[]{2,"672024247","Eko Prasetyo",80,70,70,"A"});
        model.addRow(new Object[]{3,"672024059","Abram Ary Setia Budi",80,75,70,"A"});
        model.addRow(new Object[]{4,"672024100","Silverius Abhindra Sanjaya",87,65,88,"A"});

        JTable table = new JTable(model);
        table.setRowHeight(26);

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0,193,171));
        th.setForeground(Color.WHITE);
        th.setFont(th.getFont().deriveFont(Font.BOLD));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0,25,10,25));
        add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(0,25,20,25));

        JButton edit = new JButton("Edit");
        edit.setBackground(new Color(0,193,171));
        edit.setForeground(Color.WHITE);
        edit.setFocusPainted(false);
        edit.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Fitur edit nilai belum diimplementasikan.\n" +
                "Kalau mau, nanti kita sambungkan ke DB.",
                "Info", JOptionPane.INFORMATION_MESSAGE));

        bottom.add(edit);
        add(bottom, BorderLayout.SOUTH);
    }
}
