package siasat.dao;
import java.sql.*;
import siasat.config.DBConnection;
import siasat.model.Mahasiswa;

public class MahasiswaDao {
    public Mahasiswa findByNim(String nim) throws SQLException {
        String sql = "SELECT * FROM mahasiswa WHERE nim = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nim);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Mahasiswa m = new Mahasiswa();
                m.setNim(rs.getString("nim"));
                m.setNama(rs.getString("nama"));
                // optional password column
                try { m.setPassword(rs.getString("password")); } catch(Exception e){}
                return m;
            }
        }
        return null;
    }
}
