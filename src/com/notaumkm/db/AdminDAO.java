package com.notaumkm.db;

import com.notaumkm.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AdminDAO
 * Data Access Object untuk tabel admin.
 * Menangani autentikasi login.
 */
public class AdminDAO {

    /**
     * Memvalidasi username dan password admin.
     * Menggunakan PreparedStatement untuk mencegah SQL Injection.
     *
     * @param username username yang diinput
     * @param password password yang diinput (plaintext — pertimbangkan hashing di produksi)
     * @return objek Admin jika valid, null jika tidak cocok
     */
    public Admin login(String username, String password) {
        String sql = "SELECT id_admin, username, role FROM admin "
                   + "WHERE username = ? AND password = ?";

        try (Connection  conn = DBConnection.getConnection();
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
        } catch (SQLException e) {
            System.err.println("[AdminDAO] Error login: " + e.getMessage());
        }
        return null; // Login gagal
    }
}
