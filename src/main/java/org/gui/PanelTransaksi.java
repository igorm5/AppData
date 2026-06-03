package org.gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PanelTransaksi extends JPanel {

    private JTextField tfNoNota, tfTglJam, tfKasir, tfTotal;
    private JComboBox<String> cbCustomer;
    private JTable table;
    private DefaultTableModel model;

    public PanelTransaksi() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.C_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel judul = new JLabel("🧾  Manajemen Transaksi");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 20));
        judul.setForeground(new Color(0x1A, 0x1A, 0x2E));
        judul.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        add(judul, BorderLayout.NORTH);

        // ── Form ────────────────────────────────────────────────────────────
        JPanel formBox = MainFrame.makeWhiteBox();
        formBox.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        tfNoNota   = MainFrame.makeTextField();
        tfTglJam   = MainFrame.makeTextField();
        tfKasir    = MainFrame.makeTextField();
        tfTotal    = MainFrame.makeTextField();
        cbCustomer = new JComboBox<>();
        cbCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbCustomer.setPreferredSize(new Dimension(220, 30));

        // Isi tanggal otomatis
        tfTglJam.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        int row = 0;
        addRow(formBox, gbc, row++, "No. Nota *", tfNoNota, "Customer *", cbCustomer);
        addRow(formBox, gbc, row++, "Tanggal & Jam *", tfTglJam, "Kasir", tfKasir);
        addRow(formBox, gbc, row++, "Total (Rp) *", tfTotal, null, null);

        JButton btnLoadCust = MainFrame.makePrimaryBtn("🔄 Load");
        btnLoadCust.setPreferredSize(new Dimension(75, 30));
        btnLoadCust.addActionListener(e -> loadCustomerCombo());
        gbc.gridx = 3; gbc.gridy = 0;
        formBox.add(btnLoadCust, gbc);

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
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        formBox.add(btnPanel, gbc);

        // ── Tabel ───────────────────────────────────────────────────────────
        model = new DefaultTableModel(new String[]{"No. Nota", "Tanggal & Jam", "Customer", "Kasir", "Total (Rp)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        MainFrame.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                tfNoNota.setText(model.getValueAt(r, 0).toString());
                tfTglJam.setText(model.getValueAt(r, 1).toString());
                String cust = model.getValueAt(r, 2) != null ? model.getValueAt(r, 2).toString() : "";
                for (int i = 0; i < cbCustomer.getItemCount(); i++) {
                    if (cbCustomer.getItemAt(i).equals(cust)) { cbCustomer.setSelectedIndex(i); break; }
                }
                tfKasir.setText(model.getValueAt(r, 3) != null ? model.getValueAt(r, 3).toString() : "");
                tfTotal.setText(model.getValueAt(r, 4).toString().replace(",", ""));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0, 0xE5, 0xEF), 1, true));

        JPanel tableBox = MainFrame.makeWhiteBox();
        tableBox.setLayout(new BorderLayout());
        JLabel tblTitle = new JLabel("  Daftar Transaksi");
        tblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        tableBox.add(tblTitle, BorderLayout.NORTH);
        tableBox.add(scroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formBox, tableBox);
        split.setDividerLocation(150);
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

    private void addRow(JPanel p, GridBagConstraints gbc, int row,
                        String l1, JComponent c1, String l2, JComponent c2) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; p.add(MainFrame.makeFieldLabel(l1), gbc);
        gbc.gridx = 1; p.add(c1, gbc);
        if (l2 != null) {
            gbc.gridx = 2; p.add(MainFrame.makeFieldLabel(l2), gbc);
            gbc.gridx = 3; p.add(c2, gbc);
        }
    }

    private void loadCustomerCombo() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        cbCustomer.removeAllItems();
        cbCustomer.addItem("");
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT nama_customer FROM customer ORDER BY nama_customer");
            while (rs.next()) cbCustomer.addItem(rs.getString(1));
        } catch (SQLException e) { showErr(e); }
    }

    private void loadData() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        model.setRowCount(0);
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT * FROM transaksi ORDER BY tanggal_jam DESC");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("no_nota"),
                    rs.getString("tanggal_jam"),
                    rs.getString("nama_customer"),
                    rs.getString("kasir"),
                    String.format("%,.2f", rs.getDouble("total"))
                });
            }
        } catch (SQLException e) { showErr(e); }
    }

    private void tambah() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        String no = tfNoNota.getText().trim(), tgl = tfTglJam.getText().trim(),
               kasir = tfKasir.getText().trim(), totalStr = tfTotal.getText().trim();
        String cust = cbCustomer.getSelectedItem() != null ? cbCustomer.getSelectedItem().toString() : "";
        if (no.isEmpty() || tgl.isEmpty() || totalStr.isEmpty()) { warn("No. Nota, Tanggal, dan Total wajib diisi!"); return; }
        try {
            double total = Double.parseDouble(totalStr);
            PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("INSERT INTO transaksi (no_nota,tanggal_jam,nama_customer,kasir,total) VALUES(?,?,?,?,?)");
            ps.setString(1, no); ps.setString(2, tgl);
            ps.setString(3, cust.isEmpty() ? null : cust);
            ps.setString(4, kasir.isEmpty() ? null : kasir);
            ps.setDouble(5, total);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Transaksi berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (NumberFormatException ex) { warn("Total harus berupa angka!"); }
        catch (SQLException e) { showErr(e); }
    }

    private void update() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        if (table.getSelectedRow() < 0) { warn("Pilih baris yang akan diupdate!"); return; }
        String oldNo = model.getValueAt(table.getSelectedRow(), 0).toString();
        String no = tfNoNota.getText().trim(), tgl = tfTglJam.getText().trim(),
               kasir = tfKasir.getText().trim(), totalStr = tfTotal.getText().trim();
        String cust = cbCustomer.getSelectedItem() != null ? cbCustomer.getSelectedItem().toString() : "";
        if (no.isEmpty() || tgl.isEmpty() || totalStr.isEmpty()) { warn("No. Nota, Tanggal, dan Total wajib diisi!"); return; }
        try {
            double total = Double.parseDouble(totalStr);
            PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("UPDATE transaksi SET no_nota=?,tanggal_jam=?,nama_customer=?,kasir=?,total=? WHERE no_nota=?");
            ps.setString(1, no); ps.setString(2, tgl);
            ps.setString(3, cust.isEmpty() ? null : cust);
            ps.setString(4, kasir.isEmpty() ? null : kasir);
            ps.setDouble(5, total); ps.setString(6, oldNo);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Transaksi berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (NumberFormatException ex) { warn("Total harus berupa angka!"); }
        catch (SQLException e) { showErr(e); }
    }

    private void hapus() {
        if (!DBConnection.isConnected()) { showNotConn(); return; }
        if (table.getSelectedRow() < 0) { warn("Pilih baris yang akan dihapus!"); return; }
        String no = model.getValueAt(table.getSelectedRow(), 0).toString();
        int ok = JOptionPane.showConfirmDialog(this,
            "Hapus transaksi '" + no + "'?\n(Semua detail nota terkait akan ikut terhapus!)",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("DELETE FROM transaksi WHERE no_nota=?");
            ps.setString(1, no); ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadData();
        } catch (SQLException e) { showErr(e); }
    }

    private void clearForm() {
        tfNoNota.setText(""); tfTglJam.setText(""); tfKasir.setText(""); tfTotal.setText("");
        if (cbCustomer.getItemCount() > 0) cbCustomer.setSelectedIndex(0);
        table.clearSelection();
    }

    private void warn(String msg) { JOptionPane.showMessageDialog(this, msg, "Validasi", JOptionPane.WARNING_MESSAGE); }
    private void showNotConn() { JOptionPane.showMessageDialog(this, "Database belum terhubung!\nKlik 'Koneksi Database' di sidebar.", "Perhatian", JOptionPane.WARNING_MESSAGE); }
    private void showErr(SQLException e) { JOptionPane.showMessageDialog(this, "Error SQL:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
}
