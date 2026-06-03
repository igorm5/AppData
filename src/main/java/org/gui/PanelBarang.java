package org.gui;
import org.database.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelBarang extends JPanel {

    private JTextField tfNama, tfHarga;
    private JTable table;
    private DefaultTableModel model;

    public PanelBarang() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.C_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel judul = new JLabel("📦  Manajemen Barang");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 20));
        judul.setForeground(new Color(0x1A, 0x1A, 0x2E));
        judul.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        add(judul, BorderLayout.NORTH);

        // ── Form ────────────────────────────────────────────────────────────
        JPanel formBox = MainFrame.makeWhiteBox();
        formBox.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        tfNama  = MainFrame.makeTextField();
        tfNama.setPreferredSize(new Dimension(300, 30));
        tfHarga = MainFrame.makeTextField();

        gbc.gridx = 0; gbc.gridy = 0; formBox.add(MainFrame.makeFieldLabel("Nama Barang *"), gbc);
        gbc.gridx = 1;                formBox.add(tfNama, gbc);
        gbc.gridx = 2;                formBox.add(MainFrame.makeFieldLabel("Harga Satuan *"), gbc);
        gbc.gridx = 3;                formBox.add(tfHarga, gbc);

        JButton btnTambah = MainFrame.makeSuccessBtn("➕  Tambah");
        JButton btnUpdate = MainFrame.makeWarnBtn("✏️  Update");
        JButton btnHapus  = MainFrame.makeDangerBtn("🗑  Hapus");
        JButton btnTampil = MainFrame.makePrimaryBtn("🔍  Tampilkan");
        JButton btnBersih = new JButton("✖  Bersihkan");
        btnBersih.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBersih.setPreferredSize(new Dimension(110, 32));
        btnBersih.setFocusPainted(false);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnTambah); btnPanel.add(btnUpdate); btnPanel.add(btnHapus);
        btnPanel.add(btnTampil); btnPanel.add(btnBersih);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        formBox.add(btnPanel, gbc);

        // ── Tabel ───────────────────────────────────────────────────────────
        model = new DefaultTableModel(new String[]{"Nama Barang", "Harga Satuan (Rp)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        MainFrame.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                tfNama .setText(model.getValueAt(row, 0).toString());
                tfHarga.setText(model.getValueAt(row, 1).toString().replace(",", "").replace(".", ""));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0, 0xE5, 0xEF), 1, true));

        JPanel tableBox = MainFrame.makeWhiteBox();
        tableBox.setLayout(new BorderLayout());
        JLabel tblTitle = new JLabel("  Daftar Barang");
        tblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        tableBox.add(tblTitle, BorderLayout.NORTH);
        tableBox.add(scroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formBox, tableBox);
        split.setDividerLocation(110);
        split.setDividerSize(6);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        // ── Listeners ───────────────────────────────────────────────────────
        btnTampil.addActionListener(e -> loadData());
        btnBersih.addActionListener(e -> clearForm());
        btnTambah.addActionListener(e -> tambah());
        btnUpdate.addActionListener(e -> update());
        btnHapus .addActionListener(e -> hapus());
    }

    private void loadData() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        model.setRowCount(0);
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT * FROM barang ORDER BY nama_barang");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nama_barang"),
                    String.format("%,.2f", rs.getDouble("harga_satuan"))
                });
            }
        } catch (SQLException e) { showErr(e); }
    }

    private void tambah() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        String nama = tfNama.getText().trim();
        String hargaStr = tfHarga.getText().trim();
        if (nama.isEmpty() || hargaStr.isEmpty()) { warn("Nama Barang dan Harga wajib diisi!"); return; }
        try {
            double harga = Double.parseDouble(hargaStr);
            PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("INSERT INTO barang (nama_barang, harga_satuan) VALUES (?,?)");
            ps.setString(1, nama);
            ps.setDouble(2, harga);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Barang berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (NumberFormatException ex) { warn("Harga harus berupa angka!"); }
        catch (SQLException e) { showErr(e); }
    }

    private void update() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        if (table.getSelectedRow() < 0) { warn("Pilih baris yang akan diupdate!"); return; }
        String oldNama = model.getValueAt(table.getSelectedRow(), 0).toString();
        String nama = tfNama.getText().trim();
        String hargaStr = tfHarga.getText().trim();
        if (nama.isEmpty() || hargaStr.isEmpty()) { warn("Semua field wajib diisi!"); return; }
        try {
            double harga = Double.parseDouble(hargaStr);
            PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("UPDATE barang SET nama_barang=?, harga_satuan=? WHERE nama_barang=?");
            ps.setString(1, nama); ps.setDouble(2, harga); ps.setString(3, oldNama);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Barang berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (NumberFormatException ex) { warn("Harga harus berupa angka!"); }
        catch (SQLException e) { showErr(e); }
    }

    private void hapus() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        if (table.getSelectedRow() < 0) { warn("Pilih baris yang akan dihapus!"); return; }
        String nama = model.getValueAt(table.getSelectedRow(), 0).toString();
        int ok = JOptionPane.showConfirmDialog(this, "Hapus barang '" + nama + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("DELETE FROM barang WHERE nama_barang=?");
            ps.setString(1, nama); ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Barang berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (SQLException e) { showErr(e); }
    }

    private void clearForm() { tfNama.setText(""); tfHarga.setText(""); table.clearSelection(); }
    private void warn(String msg) { JOptionPane.showMessageDialog(this, msg, "Validasi", JOptionPane.WARNING_MESSAGE); }
    private void showNotConn() { JOptionPane.showMessageDialog(this, "Database belum terhubung!\nKlik 'Koneksi Database' di sidebar.", "Perhatian", JOptionPane.WARNING_MESSAGE); }
    private void showErr(SQLException e) { JOptionPane.showMessageDialog(this, "Error SQL:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
}
