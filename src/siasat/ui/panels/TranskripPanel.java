package siasat.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

import siasat.dao.NilaiDao;
import siasat.model.Nilai;

public class TranskripPanel extends JPanel {
    private final String nim;

    public TranskripPanel(String nim) {
        this.nim = nim;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
    }

    private void initUI() {
        // ===== HEADER =====
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel title = new JLabel("Transkrip nilai");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 25, 0, 0));

        JLabel sub = new JLabel("Semua semester");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setBorder(BorderFactory.createEmptyBorder(5, 25, 15, 0));

        top.add(title, BorderLayout.NORTH);
        top.add(sub, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // ===== TABEL =====
        String[] cols = {"No","Kode","Matakuliah","SKS","B/U","Nilai","AK","Semester"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(24);

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(0,193,171));
        th.setForeground(Color.WHITE);
        th.setFont(th.getFont().deriveFont(Font.BOLD));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 25, 10, 25));
        add(sp, BorderLayout.CENTER);

        // ===== BOTTOM: TOTAL SKS & IPK =====
        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(240,240,240));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));
        bottom.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 5));

        JLabel lblTotalSks = new JLabel("Total SKS");
        lblTotalSks.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel valueTotalSks = new JLabel("0", SwingConstants.CENTER);
        valueTotalSks.setOpaque(true);
        valueTotalSks.setBackground(new Color(0,193,171));
        valueTotalSks.setForeground(Color.WHITE);
        valueTotalSks.setPreferredSize(new Dimension(60, 24));
        valueTotalSks.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel lblIpk = new JLabel("IPK");
        lblIpk.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel valueIpk = new JLabel("0.00", SwingConstants.CENTER);
        valueIpk.setOpaque(true);
        valueIpk.setBackground(new Color(0,193,171));
        valueIpk.setForeground(Color.WHITE);
        valueIpk.setPreferredSize(new Dimension(60, 24));
        valueIpk.setFont(new Font("SansSerif", Font.BOLD, 14));

        bottom.add(lblTotalSks);
        bottom.add(valueTotalSks);
        bottom.add(lblIpk);
        bottom.add(valueIpk);

        add(bottom, BorderLayout.SOUTH);

        // ===== LOAD DATA =====
        SwingUtilities.invokeLater(() -> {
            try {
                NilaiDao dao = new NilaiDao();
                List<Nilai> list = dao.findByNim(nim);

                if (list == null || list.isEmpty()) return;

                model.setRowCount(0);
                int no = 1;
                int totalSks = 0;
                double totalAk = 0.0;

                for (Nilai n : list) {
                    int sks = n.getSks();
                    double bobot = n.getBobot();
                    double ak = sks * bobot;

                    model.addRow(new Object[]{
                            no++,
                            n.getKodeMk(),
                            n.getNamaMk(),
                            sks,
                            "B",                 // B/U statis dulu
                            n.getNilai(),
                            String.format("%.2f", ak),
                            n.getSemester()
                    });

                    totalSks += sks;
                    totalAk  += ak;
                }

                valueTotalSks.setText(String.valueOf(totalSks));
                if (totalSks > 0) {
                    double ipk = totalAk / totalSks;
                    valueIpk.setText(String.format("%.2f", ipk));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Gagal memuat transkrip nilai: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
