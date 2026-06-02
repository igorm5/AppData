package org.gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.database.DBConnection;

import java.awt.*;
import java.sql.*;

public class PanelCustomer extends JPanel {

    private JTextField tfNama, tfAlamat;
    private JTable table;
    private DefaultTableModel model;

    public PanelCustomer() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.C_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel judul = new JLabel("👤  Manajemen Customer");
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

        tfNama   = MainFrame.makeTextField();
        tfAlamat = MainFrame.makeTextField();
        tfAlamat.setPreferredSize(new Dimension(340, 30));

        gbc.gridx = 0; gbc.gridy = 0; formBox.add(MainFrame.makeFieldLabel("Nama Customer *"), gbc);
        gbc.gridx = 1;                formBox.add(tfNama, gbc);
        gbc.gridx = 2;                formBox.add(MainFrame.makeFieldLabel("Alamat"), gbc);
        gbc.gridx = 3;                formBox.add(tfAlamat, gbc);

        // Tombol
        JButton btnTambah   = MainFrame.makeSuccessBtn("➕  Tambah");
        JButton btnUpdate   = MainFrame.makeWarnBtn("✏️  Update");
        JButton btnHapus    = MainFrame.makeDangerBtn("🗑  Hapus");
        JButton btnTampil   = MainFrame.makePrimaryBtn("🔍  Tampilkan");
        JButton btnBersih   = new JButton("✖  Bersihkan");
        btnBersih.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBersih.setPreferredSize(new Dimension(110, 32));
        btnBersih.setFocusPainted(false);

        gbc.gridx = 0; gbc.gridy = 1;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnTambah);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnHapus);
        btnPanel.add(btnTampil);
        btnPanel.add(btnBersih);
        gbc.gridwidth = 4;
        formBox.add(btnPanel, gbc);

        // ── Tabel ───────────────────────────────────────────────────────────
        model = new DefaultTableModel(new String[]{"Nama Customer", "Alamat"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        MainFrame.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                tfNama  .setText(model.getValueAt(row, 0).toString());
                tfAlamat.setText(model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "");
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0, 0xE5, 0xEF), 1, true));

        JPanel tableBox = MainFrame.makeWhiteBox();
        tableBox.setLayout(new BorderLayout());
        JLabel tblTitle = new JLabel("  Daftar Customer");
        tblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        tableBox.add(tblTitle, BorderLayout.NORTH);
        tableBox.add(scroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formBox, tableBox);
        split.setDividerLocation(110);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setBackground(MainFrame.C_BG);
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
                .executeQuery("SELECT * FROM customer ORDER BY nama_customer");
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("nama_customer"), rs.getString("alamat_customer")});
            }
        } catch (SQLException e) { showErr(e); }
    }

    private void tambah() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        String nama = tfNama.getText().trim();
        if (nama.isEmpty()) { JOptionPane.showMessageDialog(this, "Nama Customer wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE); return; }
        try {
            PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("INSERT INTO customer (nama_customer, alamat_customer) VALUES (?,?)");
            ps.setString(1, nama);
            ps.setString(2, tfAlamat.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (SQLException e) { showErr(e); }
    }

    private void update() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this, "Pilih baris yang akan diupdate!", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        String oldNama = model.getValueAt(table.getSelectedRow(), 0).toString();
        String newNama = tfNama.getText().trim();
        if (newNama.isEmpty()) { JOptionPane.showMessageDialog(this, "Nama Customer wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE); return; }
        try {
            PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("UPDATE customer SET nama_customer=?, alamat_customer=? WHERE nama_customer=?");
            ps.setString(1, newNama);
            ps.setString(2, tfAlamat.getText().trim());
            ps.setString(3, oldNama);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (SQLException e) { showErr(e); }
    }

    private void hapus() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        if (table.getSelectedRow() < 0) { JOptionPane.showMessageDialog(this, "Pilih baris yang akan dihapus!", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
        String nama = model.getValueAt(table.getSelectedRow(), 0).toString();
        int ok = JOptionPane.showConfirmDialog(this, "Hapus customer '" + nama + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("DELETE FROM customer WHERE nama_customer=?");
            ps.setString(1, nama);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (SQLException e) { showErr(e); }
    }

    private void clearForm() {
        tfNama.setText(""); tfAlamat.setText(""); table.clearSelection();
    }

    private void showNotConn() { JOptionPane.showMessageDialog(this, "Database belum terhubung!\nKlik 'Koneksi Database' di sidebar.", "Perhatian", JOptionPane.WARNING_MESSAGE); }
    private void showErr(SQLException e) { JOptionPane.showMessageDialog(this, "Error SQL:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
}
