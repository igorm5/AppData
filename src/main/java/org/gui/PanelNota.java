package org.gui;
import org.database.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelNota extends JPanel {

    private JComboBox<String> cbNoNota, cbBarang;
    private JTextField tfQty, tfSubtotal;
    private JTable table;
    private DefaultTableModel model;

    public PanelNota() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.C_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel judul = new JLabel("📋  Detail Nota (Item Transaksi)");
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

        cbNoNota = new JComboBox<>();
        cbBarang = new JComboBox<>();
        tfQty = MainFrame.makeTextField();
        tfQty.setPreferredSize(new Dimension(100, 30));
        tfSubtotal = MainFrame.makeTextField();

        cbNoNota.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbNoNota.setPreferredSize(new Dimension(220, 30));
        cbBarang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbBarang.setPreferredSize(new Dimension(260, 30));

        JButton btnLoadRef = MainFrame.makePrimaryBtn("🔄 Load");
        btnLoadRef.setPreferredSize(new Dimension(75, 30));
        btnLoadRef.addActionListener(e -> {
            loadNoNotaCombo();
            loadBarangCombo();
        });

        // Hitung subtotal otomatis saat qty berubah
        cbBarang.addActionListener(e -> hitungSubtotal());
        tfQty.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                hitungSubtotal();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                hitungSubtotal();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
            }
        });

        int row = 0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        formBox.add(MainFrame.makeFieldLabel("No. Nota *"), gbc);
        gbc.gridx = 1;
        formBox.add(cbNoNota, gbc);
        gbc.gridx = 2;
        formBox.add(MainFrame.makeFieldLabel("Nama Barang *"), gbc);
        gbc.gridx = 3;
        formBox.add(cbBarang, gbc);
        gbc.gridx = 4;
        formBox.add(btnLoadRef, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formBox.add(MainFrame.makeFieldLabel("Qty *"), gbc);
        gbc.gridx = 1;
        formBox.add(tfQty, gbc);
        gbc.gridx = 2;
        formBox.add(MainFrame.makeFieldLabel("Subtotal (Rp)"), gbc);
        gbc.gridx = 3;
        formBox.add(tfSubtotal, gbc);

        JLabel hint = new JLabel("  * Subtotal dihitung otomatis dari harga × qty");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(MainFrame.C_TEXT_G);
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 5;
        formBox.add(hint, gbc);

        JButton btnTambah = MainFrame.makeSuccessBtn("➕  Tambah");
        JButton btnUpdate = MainFrame.makeWarnBtn("✏️  Update");
        JButton btnHapus = MainFrame.makeDangerBtn("🗑  Hapus");
        JButton btnTampil = MainFrame.makePrimaryBtn("🔍  Tampilkan");
        JButton btnBersih = new JButton("✖  Bersihkan");
        btnBersih.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBersih.setPreferredSize(new Dimension(110, 32));
        btnBersih.setFocusPainted(false);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnTambah);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnHapus);
        btnPanel.add(btnTampil);
        btnPanel.add(btnBersih);
        gbc.gridx = 0;
        gbc.gridy = ++row;
        gbc.gridwidth = 5;
        formBox.add(btnPanel, gbc);

        // ── Filter by no_nota ────────────────────────────────────────────────
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterPanel.setBackground(MainFrame.C_BG);
        JLabel lFilter = new JLabel("Filter Tampilan berdasarkan No. Nota:");
        lFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JComboBox<String> cbFilter = new JComboBox<>();
        cbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFilter.setPreferredSize(new Dimension(220, 28));
        JButton btnFilter = MainFrame.makePrimaryBtn("Tampilkan Filter");
        JButton btnAll = new JButton("Tampilkan Semua");
        btnAll.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAll.setFocusPainted(false);
        filterPanel.add(lFilter);
        filterPanel.add(cbFilter);
        filterPanel.add(btnFilter);
        filterPanel.add(btnAll);

        // ── Tabel ───────────────────────────────────────────────────────────
        model = new DefaultTableModel(new String[] { "No. Nota", "Nama Barang", "Qty", "Subtotal (Rp)" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        MainFrame.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                String nota = model.getValueAt(r, 0).toString();
                String brg = model.getValueAt(r, 1).toString();
                for (int i = 0; i < cbNoNota.getItemCount(); i++) {
                    if (cbNoNota.getItemAt(i).equals(nota)) {
                        cbNoNota.setSelectedIndex(i);
                        break;
                    }
                }
                for (int i = 0; i < cbBarang.getItemCount(); i++) {
                    if (cbBarang.getItemAt(i).contains(brg)) {
                        cbBarang.setSelectedIndex(i);
                        break;
                    }
                }
                tfQty.setText(model.getValueAt(r, 2).toString());
                tfSubtotal.setText(model.getValueAt(r, 3).toString().replace(",", ""));
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0, 0xE5, 0xEF), 1, true));

        JPanel tableBox = MainFrame.makeWhiteBox();
        tableBox.setLayout(new BorderLayout());
        JLabel tblTitle = new JLabel("  Daftar Item Nota");
        tblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        tableBox.add(tblTitle, BorderLayout.NORTH);
        tableBox.add(filterPanel, BorderLayout.CENTER);
        tableBox.add(scroll, BorderLayout.SOUTH);
        scroll.setPreferredSize(new Dimension(0, 320));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formBox, tableBox);
        split.setDividerLocation(170);
        split.setDividerSize(6);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        // ── Listeners ───────────────────────────────────────────────────────
        btnTampil.addActionListener(e -> loadData(null));
        btnBersih.addActionListener(e -> clearForm());
        btnTambah.addActionListener(e -> tambah());
        btnUpdate.addActionListener(e -> update());
        btnHapus.addActionListener(e -> hapus());

        // Filter listeners
        btnFilter.addActionListener(e -> {
            String selected = cbFilter.getSelectedItem() != null ? cbFilter.getSelectedItem().toString() : "";
            if (!selected.isEmpty())
                loadData(selected);
        });
        btnAll.addActionListener(e -> loadData(null));

        // Sync filter combo
        btnTampil.addActionListener(e -> {
            cbFilter.removeAllItems();
            cbFilter.addItem("");
            for (int i = 0; i < cbNoNota.getItemCount(); i++) {
                String item = cbNoNota.getItemAt(i);
                if (!item.isEmpty())
                    cbFilter.addItem(item);
            }
        });
    }

    private void hitungSubtotal() {
        if (!DBConnection.isConnected())
            return;
        String barangItem = cbBarang.getSelectedItem() != null ? cbBarang.getSelectedItem().toString() : "";
        if (barangItem.isEmpty())
            return;
        // Format di combo: "NAMA_BARANG | Rp HARGA"
        String nama = barangItem.split("\\|")[0].trim();
        try {
            int qty = Integer.parseInt(tfQty.getText().trim());
            PreparedStatement ps = DBConnection.getConnection()
                    .prepareStatement("SELECT harga_satuan FROM barang WHERE nama_barang=?");
            ps.setString(1, nama);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double harga = rs.getDouble(1);
                tfSubtotal.setText(String.format("%.2f", harga * qty));
            }
        } catch (NumberFormatException ex) {
            // qty belum angka, ignore
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNoNotaCombo() {
        if (!DBConnection.isConnected()) {
            showNotConn();
            return;
        }
        cbNoNota.removeAllItems();
        cbNoNota.addItem("");
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                    .executeQuery("SELECT no_nota FROM transaksi ORDER BY tanggal_jam DESC");
            while (rs.next())
                cbNoNota.addItem(rs.getString(1));
        } catch (SQLException e) {
            showErr(e);
        }
    }

    private void loadBarangCombo() {
        if (!DBConnection.isConnected()) {
            showNotConn();
            return;
        }
        cbBarang.removeAllItems();
        cbBarang.addItem("");
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                    .executeQuery("SELECT nama_barang, harga_satuan FROM barang ORDER BY nama_barang");
            while (rs.next()) {
                cbBarang.addItem(
                        rs.getString("nama_barang") + " | Rp " + String.format("%,.0f", rs.getDouble("harga_satuan")));
            }
        } catch (SQLException e) {
            showErr(e);
        }
    }

    private void loadData(String filterNoNota) {
        if (!DBConnection.isConnected()) {
            showNotConn();
            return;
        }
        model.setRowCount(0);
        try {
            String sql = "SELECT * FROM nota" +
                    (filterNoNota != null && !filterNoNota.isEmpty() ? " WHERE no_nota=?" : "") +
                    " ORDER BY no_nota, nama_barang";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            if (filterNoNota != null && !filterNoNota.isEmpty())
                ps.setString(1, filterNoNota);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("no_nota"),
                        rs.getString("nama_barang"),
                        rs.getInt("qty"),
                        String.format("%,.2f", rs.getDouble("subtotal"))
                });
            }
        } catch (SQLException e) {
            showErr(e);
        }
    }

    private String getSelectedBarangNama() {
        String item = cbBarang.getSelectedItem() != null ? cbBarang.getSelectedItem().toString() : "";
        if (item.isEmpty())
            return "";
        return item.split("\\|")[0].trim();
    }

    private void tambah() {
        if (!DBConnection.isConnected()) {
            showNotConn();
            return;
        }
        String nota = cbNoNota.getSelectedItem() != null ? cbNoNota.getSelectedItem().toString() : "";
        String brg = getSelectedBarangNama();
        String qtyStr = tfQty.getText().trim();
        String subStr = tfSubtotal.getText().trim();
        if (nota.isEmpty() || brg.isEmpty() || qtyStr.isEmpty()) {
            warn("No. Nota, Barang, dan Qty wajib diisi!");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyStr);
            double sub = subStr.isEmpty() ? 0 : Double.parseDouble(subStr);
            PreparedStatement ps = DBConnection.getConnection()
                    .prepareStatement("INSERT INTO nota (no_nota,nama_barang,qty,subtotal) VALUES(?,?,?,?)");
            ps.setString(1, nota);
            ps.setString(2, brg);
            ps.setInt(3, qty);
            ps.setDouble(4, sub);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item nota berhasil ditambahkan!", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData(null);
        } catch (NumberFormatException ex) {
            warn("Qty dan Subtotal harus berupa angka!");
        } catch (SQLException e) {
            showErr(e);
        }
    }

    private void update() {
        if (!DBConnection.isConnected()) {
            showNotConn();
            return;
        }
        if (table.getSelectedRow() < 0) {
            warn("Pilih baris yang akan diupdate!");
            return;
        }
        String oldNota = model.getValueAt(table.getSelectedRow(), 0).toString();
        String oldBrg = model.getValueAt(table.getSelectedRow(), 1).toString();
        String nota = cbNoNota.getSelectedItem() != null ? cbNoNota.getSelectedItem().toString() : "";
        String brg = getSelectedBarangNama();
        String qtyStr = tfQty.getText().trim();
        String subStr = tfSubtotal.getText().trim();
        if (nota.isEmpty() || brg.isEmpty() || qtyStr.isEmpty()) {
            warn("No. Nota, Barang, dan Qty wajib diisi!");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyStr);
            double sub = subStr.isEmpty() ? 0 : Double.parseDouble(subStr);
            PreparedStatement ps = DBConnection.getConnection()
                    .prepareStatement(
                            "UPDATE nota SET no_nota=?,nama_barang=?,qty=?,subtotal=? WHERE no_nota=? AND nama_barang=?");
            ps.setString(1, nota);
            ps.setString(2, brg);
            ps.setInt(3, qty);
            ps.setDouble(4, sub);
            ps.setString(5, oldNota);
            ps.setString(6, oldBrg);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item nota berhasil diupdate!", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData(null);
        } catch (NumberFormatException ex) {
            warn("Qty dan Subtotal harus berupa angka!");
        } catch (SQLException e) {
            showErr(e);
        }
    }

    private void hapus() {
        if (!DBConnection.isConnected()) {
            showNotConn();
            return;
        }
        if (table.getSelectedRow() < 0) {
            warn("Pilih baris yang akan dihapus!");
            return;
        }
        String nota = model.getValueAt(table.getSelectedRow(), 0).toString();
        String brg = model.getValueAt(table.getSelectedRow(), 1).toString();
        int ok = JOptionPane.showConfirmDialog(this,
                "Hapus item '" + brg + "' dari nota '" + nota + "'?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION)
            return;
        try {
            PreparedStatement ps = DBConnection.getConnection()
                    .prepareStatement("DELETE FROM nota WHERE no_nota=? AND nama_barang=?");
            ps.setString(1, nota);
            ps.setString(2, brg);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item nota berhasil dihapus!", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData(null);
        } catch (SQLException e) {
            showErr(e);
        }
    }

    private void clearForm() {
        if (cbNoNota.getItemCount() > 0)
            cbNoNota.setSelectedIndex(0);
        if (cbBarang.getItemCount() > 0)
            cbBarang.setSelectedIndex(0);
        tfQty.setText("");
        tfSubtotal.setText("");
        table.clearSelection();
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validasi", JOptionPane.WARNING_MESSAGE);
    }

    private void showNotConn() {
        JOptionPane.showMessageDialog(this, "Database belum terhubung!\nKlik 'Koneksi Database' di sidebar.",
                "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    private void showErr(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error SQL:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
