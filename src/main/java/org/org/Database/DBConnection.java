package org.org.database;

import java.sql.*;
import javax.swing.JOptionPane;

public class DBConnection {
   
    private static final String SERVER   = "localhost";   
    private static final String PORT     = "1433";        
    private static final String DB       = "nota_umkm";
    private static final String USER     = "sa";         
    private static final String PASS     = "12345";           


    private static final boolean INTEGRATED_SECURITY = false;

    private static Connection conn = null;

    public static Connection getConnection() {
        return conn;
    }

    public static boolean connect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            String url;
            if (INTEGRATED_SECURITY) {
                url = "jdbc:sqlserver://" + SERVER + ":" + PORT
                    + ";databaseName=" + DB
                    + ";integratedSecurity=true"
                    + ";trustServerCertificate=true";
                conn = DriverManager.getConnection(url);
            } else {
                url = "jdbc:sqlserver://" + SERVER + ":" + PORT
                    + ";databaseName=" + DB
                    + ";trustServerCertificate=true";
                conn = DriverManager.getConnection(url, USER, PASS);
            }

            return true;

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Driver SQL Server tidak ditemukan!\n\n" +
                "Tambahkan file berikut ke Libraries project:\n" +
                "  mssql-jdbc-12.x.x.jre11.jar\n\n" +
                "Download di:\n" +
                "https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server",
                "Error Driver", JOptionPane.ERROR_MESSAGE);
            return false;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal konek ke SQL Server!\n\n" +
                "Pesan error:\n" + e.getMessage() + "\n\n" +
                "Pastikan:\n" +
                "  1. SQL Server sudah berjalan\n" +
                "  2. Database 'nota_umkm' sudah dibuat\n" +
                "  3. TCP/IP aktif di SQL Server Configuration Manager\n" +
                "  4. User/password benar di DBConnection.java",
                "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void initTables() {
        if (!isConnected()) return;
        try {
            Statement st = conn.createStatement();

            st.execute(
                "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='customer' AND xtype='U') " +
                "CREATE TABLE customer (" +
                "  nama_customer   VARCHAR(100) NOT NULL PRIMARY KEY," +
                "  alamat_customer VARCHAR(255)" +
                ")"
            );

            st.execute(
                "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='barang' AND xtype='U') " +
                "CREATE TABLE barang (" +
                "  nama_barang  VARCHAR(150) NOT NULL PRIMARY KEY," +
                "  harga_satuan DECIMAL(12,2) NOT NULL" +
                ")"
            );

            st.execute(
                "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='transaksi' AND xtype='U') " +
                "CREATE TABLE transaksi (" +
                "  no_nota       VARCHAR(30)  NOT NULL PRIMARY KEY," +
                "  tanggal_jam   DATETIME     NOT NULL," +
                "  nama_customer VARCHAR(100)," +
                "  kasir         VARCHAR(100)," +
                "  total         DECIMAL(14,2)," +
                "  FOREIGN KEY (nama_customer) REFERENCES customer(nama_customer)" +
                "    ON UPDATE CASCADE ON DELETE SET NULL" +
                ")"
            );

            st.execute(
                "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='nota' AND xtype='U') " +
                "CREATE TABLE nota (" +
                "  no_nota     VARCHAR(30)  NOT NULL," +
                "  nama_barang VARCHAR(150) NOT NULL," +
                "  qty         INT          NOT NULL DEFAULT 1," +
                "  subtotal    DECIMAL(14,2)," +
                "  PRIMARY KEY (no_nota, nama_barang)," +
                "  FOREIGN KEY (no_nota)     REFERENCES transaksi(no_nota)" +
                "    ON UPDATE CASCADE ON DELETE CASCADE," +
                "  FOREIGN KEY (nama_barang) REFERENCES barang(nama_barang)" +
                "    ON UPDATE NO ACTION ON DELETE NO ACTION" +
                ")"
            );

            st.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal membuat tabel!\n" + e.getMessage(),
                "Error Init DB", JOptionPane.ERROR_MESSAGE);
        }
    }
}
