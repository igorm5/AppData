package com.notaumkm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Kelas DBConnection
 * Mengelola koneksi tunggal (Singleton) ke database SQL Server via JDBC.
 * Ubah konstanta URL, USER, PASS sesuai konfigurasi server Anda.
 */
public class DBConnection {

    // ── Konfigurasi koneksi ──────────────────────────────────────────────────
    private static final String SERVER   = "localhost";   // Nama/IP SQL Server
    private static final String PORT     = "1433";        // Port default SQL Server
    private static final String DATABASE = "nota_umkm";   // Nama database
    private static final String USER     = "sa";          // Username SQL Server
    private static final String PASS     = "12345"; // Password SQL Server

    // URL koneksi JDBC untuk SQL Server (Microsoft JDBC Driver)
    private static final String URL =
        "jdbc:sqlserver://" + SERVER + ":" + PORT
        + ";databaseName=" + DATABASE
        + ";encrypt=false"          // Nonaktifkan enkripsi TLS (dev environment)
        + ";trustServerCertificate=true";

    // Instance tunggal (Singleton pattern)
    private static Connection instance = null;

    /** Constructor privat — kelas ini tidak diinstansiasi langsung */
    private DBConnection() {}

    /**
     * Mengembalikan koneksi aktif. Jika belum ada atau sudah tertutup,
     * koneksi baru dibuat.
     *
     * @return objek Connection ke database
     * @throws SQLException jika koneksi gagal
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Muat driver JDBC SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver SQL Server tidak ditemukan. Pastikan mssql-jdbc-*.jar ada di classpath.", e);
        }

        // Buat koneksi baru jika belum ada atau sudah ditutup
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("[DB] Koneksi berhasil ke database: " + DATABASE);
        }
        return instance;
    }

    /**
     * Menutup koneksi database secara manual (panggil saat aplikasi ditutup).
     */
    public static void closeConnection() {
        if (instance != null) {
            try {
                if (!instance.isClosed()) {
                    instance.close();
                    System.out.println("[DB] Koneksi ditutup.");
                }
            } catch (SQLException e) {
                System.err.println("[DB] Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }
}
