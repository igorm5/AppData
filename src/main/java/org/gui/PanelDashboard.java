import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PanelDashboard extends JPanel {

    private JLabel lblTotalCustomer, lblTotalBarang, lblTotalTransaksi, lblTotalOmzet;
    private JTextArea taRecentTx;

    public PanelDashboard() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.C_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Header
        JLabel judul = new JLabel("🏠  Dashboard");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        judul.setForeground(new Color(0x1A, 0x1A, 0x2E));
        judul.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel subjudul = new JLabel("Selamat datang di Sistem Nota UMKM – Toko Sembako & Frozen Food");
        subjudul.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subjudul.setForeground(MainFrame.C_TEXT_G);
        subjudul.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(MainFrame.C_BG);
        headerPanel.add(judul);
        headerPanel.add(subjudul);

        // Stat cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setBackground(MainFrame.C_BG);

        lblTotalCustomer = new JLabel("–");
        lblTotalBarang = new JLabel("–");
        lblTotalTransaksi = new JLabel("–");
        lblTotalOmzet = new JLabel("–");

        statsPanel.add(makeStatCard("👤 Total Customer", lblTotalCustomer, new Color(0x17, 0x76, 0xD2)));
        statsPanel.add(makeStatCard("📦 Total Barang", lblTotalBarang, new Color(0x2E, 0x7D, 0x32)));
        statsPanel.add(makeStatCard("🧾 Total Transaksi", lblTotalTransaksi, new Color(0xF5, 0x7C, 0x00)));
        statsPanel.add(makeStatCard("💰 Total Omzet (Rp)", lblTotalOmzet, new Color(0x6A, 0x1B, 0x9A)));

        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setBackground(MainFrame.C_BG);
        statsWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        statsWrapper.add(statsPanel, BorderLayout.CENTER);

        // Info singkat tentang normalisasi
        JPanel infoBox = MainFrame.makeWhiteBox();
        infoBox.setLayout(new BorderLayout());
        JLabel infoTitle = new JLabel("  📖  Struktur Database – Hasil Normalisasi 3NF");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoTitle.setForeground(new Color(0x1A, 0x73, 0xE8));
        infoTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        String info = "  ┌─ customer (nama_customer PK, alamat_customer)\n" +
                "  │      ↓ 1:N\n" +
                "  ├─ transaksi (no_nota PK, tanggal_jam, nama_customer FK, kasir, total)\n" +
                "  │      ↓ 1:N\n" +
                "  ├─ nota (no_nota PK+FK, nama_barang PK+FK, qty, subtotal)\n" +
                "  │      ↑ N:1\n" +
                "  └─ barang (nama_barang PK, harga_satuan)\n\n" +
                "  Klik 'Koneksi Database' di sidebar kiri, lalu navigasi ke tiap menu untuk mengelola data.";

        JTextArea ta = new JTextArea(info);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setBackground(MainFrame.C_WHITE);
        ta.setForeground(new Color(0x20, 0x30, 0x40));
        infoBox.add(infoTitle, BorderLayout.NORTH);
        infoBox.add(ta, BorderLayout.CENTER);

        // Refresh button
        JButton btnRefresh = MainFrame.makePrimaryBtn("🔄  Refresh Statistik");
        btnRefresh.setPreferredSize(new Dimension(180, 34));
        btnRefresh.addActionListener(e -> loadStats());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setBackground(MainFrame.C_BG);
        btnPanel.add(btnRefresh);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(MainFrame.C_BG);
        center.add(statsWrapper, BorderLayout.NORTH);
        center.add(infoBox, BorderLayout.CENTER);
        center.add(btnPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private JPanel makeStatCard(String label, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MainFrame.C_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0, 0xE5, 0xEF), 1, true),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 5));

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(MainFrame.C_TEXT_G);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(lblTitle, BorderLayout.CENTER);
        card.add(valueLabel, BorderLayout.SOUTH);
        return card;
    }

    public void loadStats() {
        if (!DBConnection.isConnected())
            return;
        try {
            Connection conn = DBConnection.getConnection();
            lblTotalCustomer.setText(getCount(conn, "SELECT COUNT(*) FROM customer"));
            lblTotalBarang.setText(getCount(conn, "SELECT COUNT(*) FROM barang"));
            lblTotalTransaksi.setText(getCount(conn, "SELECT COUNT(*) FROM transaksi"));

            ResultSet rs = conn.createStatement().executeQuery("SELECT SUM(total) FROM transaksi");
            if (rs.next()) {
                double sum = rs.getDouble(1);
                lblTotalOmzet.setText(String.format("%,.0f", sum));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getCount(Connection conn, String sql) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery(sql);
        if (rs.next())
            return String.valueOf(rs.getInt(1));
        return "–";
    }
}
