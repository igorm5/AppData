package notaumkm.db;

import notaumkm.model.Barang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangDAO {

    public List<Barang> getAll() {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT id_barang, nama_barang, harga_satuan, stok "
                   + "FROM barang ORDER BY nama_barang";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BarangDAO] getAll error: " + e.getMessage());
        }
        return list;
    }

    public Barang getByNama(String namaBarang) {
        String sql = "SELECT id_barang, nama_barang, harga_satuan, stok "
                   + "FROM barang WHERE nama_barang = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, namaBarang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[BarangDAO] getByNama error: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(Barang b) {
        String sql = "INSERT INTO barang (nama_barang, harga_satuan, stok) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, b.getNamaBarang());
            ps.setDouble(2, b.getHargaSatuan());
            ps.setInt   (3, b.getStok());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BarangDAO] insert error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Barang b) {
        String sql = "UPDATE barang SET nama_barang = ?, harga_satuan = ?, stok = ? "
                   + "WHERE id_barang = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, b.getNamaBarang());
            ps.setDouble(2, b.getHargaSatuan());
            ps.setInt   (3, b.getStok());
            ps.setInt   (4, b.getIdBarang());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BarangDAO] update error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int idBarang) {
        String sql = "DELETE FROM barang WHERE id_barang = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idBarang);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BarangDAO] delete error: " + e.getMessage());
            return false;
        }
    }

    public boolean kurangiStok(Connection conn, String namaBarang, int qty) throws SQLException {
        String sql = "UPDATE barang SET stok = stok - ? "
                   + "WHERE nama_barang = ? AND stok >= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, qty);
            ps.setString(2, namaBarang);
            ps.setInt   (3, qty);          
            return ps.executeUpdate() > 0;
        }
    }

    private Barang mapRow(ResultSet rs) throws SQLException {
        return new Barang(
            rs.getInt   ("id_barang"),
            rs.getString("nama_barang"),
            rs.getDouble("harga_satuan"),
            rs.getInt   ("stok")
        );
    }
}
