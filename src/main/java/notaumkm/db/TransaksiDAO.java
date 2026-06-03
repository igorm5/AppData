package notaumkm.db;

import notaumkm.model.ItemNota;
import notaumkm.model.Transaksi;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * TransaksiDAO
 * Menangani penyimpanan transaksi ke tabel transaksi dan nota secara atomik
 * menggunakan transaction (commit/rollback).
 */
public class TransaksiDAO {

    private final BarangDAO barangDAO = new BarangDAO();

    /**
     * Menyimpan seluruh transaksi secara atomik:
     * 1. Insert ke tabel transaksi
     * 2. Insert ke tabel nota (detail)
     * 3. Kurangi stok barang
     *
     * Jika salah satu gagal, semua operasi di-rollback.
     *
     * @param namaCustomer nama pembeli
     * @param kasir        nama kasir yang login
     * @param items        daftar item belanja
     * @param total        total harga keseluruhan
     * @return noNota yang berhasil dibuat, atau null jika gagal
     */
    public String simpanTransaksi(String namaCustomer, String kasir,
                                   List<ItemNota> items, double total) {

        Connection conn = null;
        String noNota   = buatNoNota(); // Generate nomor nota unik

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // ─── Mulai transaksi DB ───

            // ── 1. Cek stok semua item sebelum proses ────────────────────────
            for (ItemNota item : items) {
                if (!cekStokCukup(conn, item.getNamaBarang(), item.getQty())) {
                    conn.rollback();
                    System.err.println("[TransaksiDAO] Stok tidak cukup: " + item.getNamaBarang());
                    return "STOK_KURANG:" + item.getNamaBarang();
                }
            }

            // ── 2. Insert header transaksi ───────────────────────────────────
            insertTransaksi(conn, noNota, namaCustomer, kasir, total);

            // ── 3. Insert detail nota + kurangi stok ────────────────────────
            for (ItemNota item : items) {
                insertNota(conn, noNota, item);
                barangDAO.kurangiStok(conn, item.getNamaBarang(), item.getQty());
            }

            conn.commit(); // ─── Commit jika semua berhasil ───
            System.out.println("[TransaksiDAO] Transaksi berhasil: " + noNota);
            return noNota;

        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] Error, rollback: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* abaikan */ }
            }
            return null;
        } finally {
            // Kembalikan auto-commit ke normal
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { /* abaikan */ }
            }
        }
    }

    // ── PRIVATE HELPERS ──────────────────────────────────────────────────────

    /** Insert satu baris ke tabel transaksi */
    private void insertTransaksi(Connection conn, String noNota,
                                  String namaCustomer, String kasir, double total)
            throws SQLException {

        String sql = "INSERT INTO transaksi (no_nota, tanggal_jam, nama_customer, kasir, total) "
                   + "VALUES (?, GETDATE(), ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noNota);
            ps.setString(2, namaCustomer.isEmpty() ? "Umum" : namaCustomer);
            ps.setString(3, kasir);
            ps.setDouble(4, total);
            ps.executeUpdate();
        }
    }

    /** Insert satu baris detail ke tabel nota */
    private void insertNota(Connection conn, String noNota, ItemNota item)
            throws SQLException {

        String sql = "INSERT INTO nota (no_nota, nama_barang, qty, subtotal) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noNota);
            ps.setString(2, item.getNamaBarang());
            ps.setInt   (3, item.getQty());
            ps.setDouble(4, item.getSubtotal());
            ps.executeUpdate();
        }
    }

    /** Cek apakah stok mencukupi untuk qty yang diminta */
    private boolean cekStokCukup(Connection conn, String namaBarang, int qty)
            throws SQLException {

        String sql = "SELECT stok FROM barang WHERE nama_barang = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaBarang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stok") >= qty;
                }
            }
        }
        return false;
    }

    /**
     * Membuat nomor nota unik berformat: C[MMYYYY]-[6-digit-counter]
     * Contoh: C062026-000001
     */
    private String buatNoNota() {
        LocalDateTime now = LocalDateTime.now();
        String prefix = now.format(DateTimeFormatter.ofPattern("MMyyyy"));

        // Ambil counter dari DB untuk hari ini agar tidak duplikat
        String sql = "SELECT COUNT(*) + 1 AS counter FROM transaksi "
                   + "WHERE no_nota LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "C" + prefix + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int counter = rs.getInt("counter");
                    return String.format("C%s-%06d", prefix, counter);
                }
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] buatNoNota error: " + e.getMessage());
        }

        // Fallback jika DB error: gunakan timestamp
        return "C" + System.currentTimeMillis();
    }

    /** Mengambil daftar riwayat transaksi terbaru. */
    public List<Transaksi> getRiwayatTransaksi() {
        List<Transaksi> riwayat = new ArrayList<>();
        String sql = "SELECT TOP 20 no_nota, CONVERT(VARCHAR(19), tanggal_jam, 120) AS tanggal_jam, "
                   + "nama_customer, kasir, total "
                   + "FROM transaksi "
                   + "ORDER BY tanggal_jam DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                riwayat.add(new Transaksi(
                    rs.getString("no_nota"),
                    rs.getString("tanggal_jam"),
                    rs.getString("nama_customer"),
                    rs.getString("kasir"),
                    rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] getRiwayatTransaksi error: " + e.getMessage());
        }
        return riwayat;
    }
}
