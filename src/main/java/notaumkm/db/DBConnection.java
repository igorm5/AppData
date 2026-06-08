package notaumkm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // ssms
    private static final String SERVER = "localhost";
    private static final String PORT = "1433";
    private static final String DATABASE = "nota_umkm";
    private static final String USER = "sa";
    private static final String PASS = "KopiHitam123#";
    private static final String URL = "jdbc:sqlserver://" + SERVER + ":" + PORT
            + ";databaseName=" + DATABASE
            + ";encrypt=true"
            + ";trustServerCertificate=true";

    // igor
    // private static final String SERVER = "localhost";
    // private static final String PORT = "1433";
    // private static final String DATABASE = "nota_umkm";
    // private static final String USER = "sa";
    // private static final String PASS = "12345";
    // private static final String URL =
    // "jdbc:sqlserver://" + SERVER + ":" + PORT
    // + ";databaseName=" + DATABASE
    // + ";encrypt=false"
    // + ";trustServerCertificate=true";

    private static Connection instance = null;

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Driver SQL Server tidak ditemukan. Pastikan mssql-jdbc-*.jar ada di classpath.", e);
        }

        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("[DB] Koneksi berhasil ke database: " + DATABASE);
        }
        return instance;
    }

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
