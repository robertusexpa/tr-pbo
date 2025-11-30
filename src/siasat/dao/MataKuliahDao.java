package siasat.dao;

import java.sql.*;
import java.util.*;
import siasat.config.DBConnection;
import siasat.model.MataKuliah;

public class MataKuliahDao {
    public List<MataKuliah> findAll() throws SQLException {
        List<MataKuliah> out = new ArrayList<>();
        String sql = "SELECT id_matkul, kode_matkul, nama_matkul, sks, id_prodi FROM mata_kuliah";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MataKuliah m = new MataKuliah();
                m.setKode(rs.getString("kode_matkul"));
                m.setNama(rs.getString("nama_matkul"));
                m.setSks(rs.getInt("sks"));
                // optional: store id_matkul or id_prodi if needed
                out.add(m);
            }
        }
        return out;
    }
}
