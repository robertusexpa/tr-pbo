package siasat.dao;
import java.sql.*;
import siasat.config.DBConnection;
import siasat.model.Dosen;
public class DosenDao {
    public Dosen findByNidn(String nidn) throws SQLException {
        String sql = "SELECT * FROM dosen WHERE nidn = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nidn);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Dosen d = new Dosen();
                d.setNidn(rs.getString("nidn"));
                d.setNama(rs.getString("nama"));
                try { d.setPassword(rs.getString("password")); } catch(Exception e){}
                return d;
            }
        }
        return null;
    }
}
