package notaumkm;

import notaumkm.db.DBConnection;
import notaumkm.ui.LoginFrame;

import javax.swing.*;

/**
 * ════════════════════════════════════════════════════════
 *   SISTEM NOTA UMKM — Aplikasi Desktop Java Swing
 *   Entry point utama aplikasi.
 * ════════════════════════════════════════════════════════
 *
 * Cara menjalankan:
 * 1. Jalankan setup_database.sql di SQL Server Management Studio (SSMS)
 * 2. Sesuaikan konfigurasi koneksi di DBConnection.java
 *    (SERVER, PORT, USER, PASS)
 * 3. Tambahkan mssql-jdbc-*.jar ke classpath
 * 4. Compile dan jalankan kelas ini
 *
 * Akun demo:
 *   - Kasir        : kasir1 / kasir123
 *   - Pengelola    : pengelola / kelola123
 */
public class Main {

    public static void main(String[] args) {

        // ── Look & Feel: gunakan tema sistem operasi ──────────────────────────
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("[Main] Gagal set L&F: " + e.getMessage());
        }

        // ── Antialiasing teks ─────────────────────────────────────────────────
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // ── Jalankan UI di Event Dispatch Thread (EDT) ────────────────────────
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });

        // ── Hook: tutup koneksi DB saat aplikasi ditutup ──────────────────────
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DBConnection.closeConnection();
            System.out.println("[Main] Aplikasi ditutup.");
        }));
    }
}
