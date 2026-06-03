package notaumkm.db;

import notaumkm.model.Barang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BarangDAO
 * Data Access Object untuk tabel barang.
 * Menyediakan operasi CRUD dan pengurangan stok.
 */
public class BarangDAO {

    // ── SELECT ALL ───────────────────────────────────────────────────────────

    /**
     * Mengambil semua data barang dari database.
     *
     * @return List berisi semua objek Barang
     */
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

    // ── SELECT BY ID ─────────────────────────────────────────────────────────

    /**
     * Mengambil data barang berdasarkan nama_barang (primary key bisnis).
     *
     * @param namaBarang nama barang yang dicari
     * @return objek Barang atau null jika tidak ditemukan
     */
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

    // ── INSERT ───────────────────────────────────────────────────────────────

    /**
     * Menambahkan barang baru ke database.
     *
     * @param b objek Barang yang akan disimpan
     * @return true jika berhasil
     */
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

    // ── UPDATE ───────────────────────────────────────────────────────────────

    /**
     * Memperbarui data barang (harga dan stok) berdasarkan id_barang.
     *
     * @param b objek Barang dengan data terbaru
     * @return true jika berhasil
     */
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

    // ── DELETE ───────────────────────────────────────────────────────────────

    /**
     * Menghapus barang berdasarkan id_barang.
     *
     * @param idBarang ID barang yang akan dihapus
     * @return true jika berhasil
     */
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

    // ── KURANGI STOK ─────────────────────────────────────────────────────────

    /**
     * Mengurangi stok barang setelah transaksi berhasil.
     * Validasi stok cukup dilakukan sebelum memanggil metode ini.
     *
     * @param namaBarang nama barang
     * @param qty        jumlah yang dibeli
     * @return true jika berhasil dikurangi
     */
    public boolean kurangiStok(Connection conn, String namaBarang, int qty) throws SQLException {
        String sql = "UPDATE barang SET stok = stok - ? "
                   + "WHERE nama_barang = ? AND stok >= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, qty);
            ps.setString(2, namaBarang);
            ps.setInt   (3, qty);          // Jaminan stok tidak negatif

            return ps.executeUpdate() > 0; // Jika 0 = stok tidak mencukupi
        }
    }

    // ── HELPER ───────────────────────────────────────────────────────────────

    /** Memetakan satu baris ResultSet ke objek Barang */
    private Barang mapRow(ResultSet rs) throws SQLException {
        return new Barang(
            rs.getInt   ("id_barang"),
            rs.getString("nama_barang"),
            rs.getDouble("harga_satuan"),
            rs.getInt   ("stok")
        );
    }
}
