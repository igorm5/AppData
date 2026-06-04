package notaumkm.db;

import notaumkm.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    public Admin login(String username, String password) throws SQLException {
        String sql = "SELECT id_admin, username, role FROM admin "
                   + "WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setIdAdmin(rs.getInt("id_admin"));
                    admin.setUsername(rs.getString("username"));
                    admin.setRole(rs.getString("role"));
                    return admin;
                }
            }
        }
        return null; 
    }
}
