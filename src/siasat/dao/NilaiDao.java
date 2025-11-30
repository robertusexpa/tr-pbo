package siasat.dao;

import java.sql.*;
import java.util.*;
import siasat.config.DBConnection;
import siasat.model.Nilai;

public class NilaiDao {
    public List<Nilai> findByNim(String nim) throws SQLException {
        List<Nilai> out = new ArrayList<>();
        String sql = """
            SELECT cl.semester, mk.kode_matkul, mk.nama_matkul, mk.sks,
                   n.nilai_angka, n.nilai_huruf, n.bobot, k.tanggal_daftar
            FROM nilai n
            JOIN krs k ON n.id_krs = k.id_krs
            JOIN kelas cl ON k.id_kelas = cl.id_kelas
            JOIN mata_kuliah mk ON cl.id_matkul = mk.id_matkul
            WHERE k.nim = ?
            ORDER BY cl.semester
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Nilai v = new Nilai();
                    v.setSemester(String.valueOf(rs.getInt("semester")));
                    v.setKodeMk(rs.getString("kode_matkul"));
                    v.setNamaMk(rs.getString("nama_matkul"));
                    v.setSks(rs.getInt("sks"));
                    // ada dua kolom nilai di DB: nilai_angka dan nilai_huruf
                    double angka = rs.getDouble("nilai_angka");
                    String huruf = rs.getString("nilai_huruf");
                    // tampilkan huruf bila ada, jika tidak pakai angka
                    if (huruf != null && !huruf.isEmpty()) v.setNilai(huruf);
                    else v.setNilai(String.valueOf(angka));
                    v.setBobot(rs.getDouble("bobot"));
                    out.add(v);
                }
            }
        }
        return out;
    }
}
