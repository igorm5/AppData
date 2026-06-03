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
        setLayout(new BorderLayout(0, 12));
        setBackground(MainFrame.C_BG);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Judul
        JLabel judul = new JLabel("Manajemen Customer");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 20));
        judul.setForeground(new Color(0x1A, 0x1A, 0x2E));
        judul.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        add(judul, BorderLayout.NORTH);

        // Form Box
        JPanel formBox = MainFrame.makeWhiteBox();
        formBox.setLayout(new BorderLayout(0, 6));

        // Baris field input
        JPanel fieldRow = new JPanel(new GridLayout(1, 4, 6, 0));
        fieldRow.setBackground(Color.WHITE);

        tfNama = new JTextField();
        tfNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfNama.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xBB, 0xC8, 0xD8), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        tfAlamat = new JTextField();
        tfAlamat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfAlamat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xBB, 0xC8, 0xD8), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JLabel lblNama = new JLabel("Nama Customer *");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNama.setForeground(new Color(0x37, 0x47, 0x5A));

        JLabel lblAlamat = new JLabel("Alamat");
        lblAlamat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAlamat.setForeground(new Color(0x37, 0x47, 0x5A));

        fieldRow.add(lblNama);
        fieldRow.add(tfNama);
        fieldRow.add(lblAlamat);
        fieldRow.add(tfAlamat);

        // Baris tombol
        JButton btnTambah = new JButton("Tambah");
        JButton btnUpdate = new JButton("Update");
        JButton btnHapus  = new JButton("Hapus");
        JButton btnTampil = new JButton("Tampilkan");
        JButton btnBersih = new JButton("Bersihkan");

        for (JButton b : new JButton[]{btnTambah, btnUpdate, btnHapus, btnTampil, btnBersih}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setOpaque(true);
            b.setPreferredSize(new Dimension(100, 34));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        btnTambah.setBackground(new Color(0x2E, 0x7D, 0x32));
        btnUpdate.setBackground(new Color(0xF5, 0x7C, 0x00));
        btnHapus .setBackground(new Color(0xE5, 0x39, 0x35));
        btnTampil.setBackground(new Color(0x1A, 0x73, 0xE8));
        btnBersih.setBackground(new Color(0x60, 0x7D, 0x8B));

        JPanel btnRow = new JPanel(new GridLayout(1, 5, 8, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.setPreferredSize(new Dimension(0, 36));
        btnRow.add(btnTambah);
        btnRow.add(btnUpdate);
        btnRow.add(btnHapus);
        btnRow.add(btnTampil);
        btnRow.add(btnBersih);

        formBox.add(fieldRow, BorderLayout.CENTER);
        formBox.add(btnRow,   BorderLayout.SOUTH);

        // Tabel
        model = new DefaultTableModel(new String[]{"Nama Customer", "Alamat"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        MainFrame.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                tfNama.setText(model.getValueAt(row, 0).toString());
                Object al = model.getValueAt(row, 1);
                tfAlamat.setText(al != null ? al.toString() : "");
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0, 0xE5, 0xEF)));

        JPanel tableBox = MainFrame.makeWhiteBox();
        tableBox.setLayout(new BorderLayout(0, 8));
        JLabel tblTitle = new JLabel("Daftar Customer");
        tblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTitle.setForeground(new Color(0x1A, 0x1A, 0x2E));
        tableBox.add(tblTitle, BorderLayout.NORTH);
        tableBox.add(scroll,   BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(MainFrame.C_BG);
        centerPanel.add(formBox,  BorderLayout.NORTH);
        centerPanel.add(tableBox, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Listeners
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
