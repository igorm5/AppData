package notaumkm.ui;

import notaumkm.db.BarangDAO;
import notaumkm.db.TransaksiDAO;
import notaumkm.model.Admin;
import notaumkm.model.Barang;
import notaumkm.model.ItemNota;
import notaumkm.model.Transaksi;
import notaumkm.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;


public class KasirFrame extends JFrame {

    private JComboBox<Barang> cmbBarang;
    private JSpinner          spnQty;
    private JButton           btnTambahItem, btnHapusItem, btnBayar, btnBersihkan, btnLogout;
    private JTable            tblKeranjang;
    private DefaultTableModel keranjangModel;
    private JTable            tblRiwayat;
    private DefaultTableModel riwayatModel;
    private JButton           btnRefreshRiwayat;
    private JLabel            lblTotal, lblStokInfo, lblStatus;
    private JTextField        txtCustomer;

    private final Admin          adminLogin;
    private final BarangDAO      barangDAO    = new BarangDAO();
    private final TransaksiDAO   transaksiDAO = new TransaksiDAO();
    private final List<ItemNota> keranjang    = new ArrayList<>();

    private static final Color CLR_DARK     = new Color(27,  44,  56);
    private static final Color CLR_GREEN    = new Color(52, 120,  77);
    private static final Color CLR_RED      = new Color(190,  50,  50);
    private static final Color CLR_ORANGE   = new Color(200, 100,  30);
    private static final Color CLR_BG       = new Color(245, 247, 250);
    private static final Color CLR_WHITE    = Color.WHITE;

    public KasirFrame(Admin admin) {
        this.adminLogin = admin;
        initUI();
        muatDaftarBarang();
        muatRiwayatTransaksi();
    }

    private void initUI() {
        setTitle("Kasir POS — " + adminLogin.getUsername());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CLR_DARK);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblJudul = new JLabel("Kasir — Point of Sale");
        lblJudul.setForeground(CLR_WHITE);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(lblJudul, BorderLayout.WEST);

        JLabel lblUser = new JLabel("Kasir: " + adminLogin.getUsername()
                                    + "   |   " + FormatUtil.sekarang());
        lblUser.setForeground(new Color(160, 190, 170));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        header.add(lblUser, BorderLayout.EAST);

        JPanel pilihPanel = new JPanel();
        pilihPanel.setLayout(new BoxLayout(pilihPanel, BoxLayout.Y_AXIS));
        pilihPanel.setBackground(CLR_WHITE);
        pilihPanel.setPreferredSize(new Dimension(280, 0));
        pilihPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 225, 230)),
            new EmptyBorder(16, 16, 16, 16)
        ));

        pilihPanel.add(buatLabel("Nama Customer"));
        pilihPanel.add(Box.createVerticalStrut(5));
        txtCustomer = buatTextField();
        txtCustomer.setToolTipText("Kosongkan untuk pelanggan umum");
        pilihPanel.add(txtCustomer);
        pilihPanel.add(Box.createVerticalStrut(14));

        pilihPanel.add(buatLabel("Pilih Barang"));
        pilihPanel.add(Box.createVerticalStrut(5));
        cmbBarang = new JComboBox<>();
        cmbBarang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbBarang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbBarang.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbBarang.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null && index == -1) {
                    setText("Pilih Barang...");
                    setForeground(new Color(120, 120, 120));
                } else if (value instanceof Barang) {
                    setText(((Barang) value).getNamaBarang());
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });
        cmbBarang.addActionListener(e -> tampilInfoStok());
        cmbBarang.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                cmbBarang.setPopupVisible(false);
            }
        });
        cmbBarang.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {
                cmbBarang.setPopupVisible(false);
            }
        });
        pilihPanel.add(cmbBarang);
        pilihPanel.add(Box.createVerticalStrut(6));

        lblStokInfo = new JLabel(" ");
        lblStokInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStokInfo.setForeground(new Color(100, 120, 130));
        pilihPanel.add(lblStokInfo);
        pilihPanel.add(Box.createVerticalStrut(14));

        pilihPanel.add(buatLabel("Jumlah (Qty)"));
        pilihPanel.add(Box.createVerticalStrut(5));
        SpinnerNumberModel spinModel = new SpinnerNumberModel(1, 1, 9999, 1);
        spnQty = new JSpinner(spinModel);
        spnQty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spnQty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnQty.setAlignmentX(Component.LEFT_ALIGNMENT);
        pilihPanel.add(spnQty);
        pilihPanel.add(Box.createVerticalStrut(20));

        btnTambahItem = buatTombol("+ Tambah ke Keranjang", CLR_GREEN);
        pilihPanel.add(btnTambahItem);
        pilihPanel.add(Box.createVerticalStrut(8));

        btnHapusItem = buatTombol("x Hapus Item Terpilih", CLR_RED);
        btnHapusItem.setEnabled(false);
        pilihPanel.add(btnHapusItem);
        pilihPanel.add(Box.createVerticalStrut(8));

        btnBersihkan = buatTombol("Kosongkan Keranjang", new Color(90, 100, 115));
        pilihPanel.add(btnBersihkan);
        pilihPanel.add(Box.createVerticalStrut(12));
        pilihPanel.add(Box.createVerticalGlue());

        btnLogout = buatTombol("Logout", new Color(90, 100, 115));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setPreferredSize(new Dimension(140, 44));
        btnLogout.setMaximumSize(new Dimension(140, 44));
        pilihPanel.add(btnLogout);
        pilihPanel.add(Box.createVerticalStrut(10));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        pilihPanel.add(lblStatus);

        String[] kolom = {"Nama Barang", "Harga Satuan", "Qty", "Subtotal"};
        keranjangModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKeranjang = new JTable(keranjangModel);
        styleTable(tblKeranjang);

        JScrollPane scrollKeranjang = new JScrollPane(tblKeranjang);
        scrollKeranjang.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(CLR_WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 220)),
            new EmptyBorder(14, 20, 14, 20)
        ));

        lblTotal = new JLabel("TOTAL:  Rp 0", SwingConstants.LEFT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotal.setForeground(CLR_DARK);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(CLR_WHITE);
        btnBayar  = buatTombolKecil("BAYAR", CLR_GREEN);
        btnBayar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnBayar.setPreferredSize(new Dimension(140, 44));
        btnPanel.add(btnBayar);

        bottomPanel.add(lblTotal,  BorderLayout.CENTER);
        bottomPanel.add(btnPanel,  BorderLayout.EAST);

        JPanel panelKeranjang = new JPanel(new BorderLayout(0, 10));
        panelKeranjang.setBackground(CLR_BG);
        panelKeranjang.setBorder(new EmptyBorder(12, 12, 12, 12));
        panelKeranjang.add(scrollKeranjang, BorderLayout.CENTER);
        panelKeranjang.add(bottomPanel, BorderLayout.SOUTH);

        String[] kolomRiwayat = {"No. Nota", "Tanggal", "Customer", "Kasir", "Total"};
        riwayatModel = new DefaultTableModel(kolomRiwayat, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRiwayat = new JTable(riwayatModel);
        styleTable(tblRiwayat);

        JScrollPane scrollRiwayat = new JScrollPane(tblRiwayat);
        scrollRiwayat.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));

        btnRefreshRiwayat = buatTombol("Refresh Riwayat", new Color(80, 120, 160));
        btnRefreshRiwayat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel riwayatHeader = new JPanel(new BorderLayout());
        riwayatHeader.setBackground(CLR_BG);
        riwayatHeader.setBorder(new EmptyBorder(12, 0, 10, 0));
        JLabel lblRiwayat = new JLabel("Riwayat Transaksi Terakhir", SwingConstants.LEFT);
        lblRiwayat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRiwayat.setForeground(CLR_DARK);
        riwayatHeader.add(lblRiwayat, BorderLayout.WEST);
        riwayatHeader.add(btnRefreshRiwayat, BorderLayout.EAST);

        JPanel panelRiwayat = new JPanel(new BorderLayout(0, 10));
        panelRiwayat.setBackground(CLR_BG);
        panelRiwayat.setBorder(new EmptyBorder(12, 12, 12, 12));
        panelRiwayat.add(riwayatHeader, BorderLayout.NORTH);
        panelRiwayat.add(scrollRiwayat, BorderLayout.CENTER);

        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Keranjang", panelKeranjang);
        tabPane.addTab("Riwayat Transaksi", panelRiwayat);
        tabPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel body = new JPanel(new BorderLayout());
        body.add(pilihPanel, BorderLayout.WEST);
        body.add(tabPane, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);

        btnTambahItem.addActionListener(e -> {
            cmbBarang.setPopupVisible(false);
            tambahKeKeranjang();
        });
        btnHapusItem.addActionListener(e -> {
            cmbBarang.setPopupVisible(false);
            hapusItemTerpilih();
        });
        btnBersihkan.addActionListener(e -> {
            cmbBarang.setPopupVisible(false);
            kosongkanKeranjang();
        });
        btnBayar.addActionListener(e -> {
            cmbBarang.setPopupVisible(false);
            prosesBayar();
        });
        btnLogout.addActionListener(e -> {
            cmbBarang.setPopupVisible(false);
            logout();
        });
        btnRefreshRiwayat.addActionListener(e -> {
            cmbBarang.setPopupVisible(false);
            btnRefreshRiwayat.setEnabled(false);
            muatRiwayatTransaksi();
        });

        tblKeranjang.getSelectionModel().addListSelectionListener(evt -> {
            btnHapusItem.setEnabled(tblKeranjang.getSelectedRow() >= 0);
        });
    }

    private void muatDaftarBarang() {
        cmbBarang.removeAllItems();
        List<Barang> list = barangDAO.getAll();
        for (Barang b : list) {
            cmbBarang.addItem(b);
        }
        cmbBarang.setSelectedIndex(-1);
        cmbBarang.setPopupVisible(false);
        tampilInfoStok();
    }

    private void tampilInfoStok() {
        Barang terpilih = (Barang) cmbBarang.getSelectedItem();
        if (terpilih == null) return;
        lblStokInfo.setText("Stok tersedia: " + terpilih.getStok()
                            + "   |   " + FormatUtil.rupiah(terpilih.getHargaSatuan()) + "/satuan");
        lblStokInfo.setForeground(terpilih.getStok() < 5
            ? CLR_RED
            : new Color(80, 120, 90));
    }

    private void tambahKeKeranjang() {
        Barang barang = (Barang) cmbBarang.getSelectedItem();
        if (barang == null) return;

        int qty = (int) spnQty.getValue();

        if (barang.getStok() < qty) {
            tampilStatus("Stok " + barang.getNamaBarang() + " tidak mencukupi! "
                         + "(Stok: " + barang.getStok() + ")", true);
            return;
        }

        for (int i = 0; i < keranjang.size(); i++) {
            if (keranjang.get(i).getNamaBarang().equals(barang.getNamaBarang())) {
                int qtyBaru = keranjang.get(i).getQty() + qty;
                if (qtyBaru > barang.getStok()) {
                    tampilStatus("Total qty melebihi stok! (Stok: " + barang.getStok() + ")", true);
                    return;
                }
                keranjang.get(i).setQty(qtyBaru);
                refreshKeranjang();
                cmbBarang.setSelectedItem(barang);
                tampilStatus("Qty " + barang.getNamaBarang() + " diperbarui.", false);
                return;
            }
        }

        keranjang.add(new ItemNota(barang.getNamaBarang(), barang.getHargaSatuan(), qty));
        refreshKeranjang();
        cmbBarang.setSelectedItem(barang);
        tampilStatus(barang.getNamaBarang() + " ditambahkan.", false);
    }

    private void hapusItemTerpilih() {
        int row = tblKeranjang.getSelectedRow();
        if (row >= 0 && row < keranjang.size()) {
            keranjang.remove(row);
            refreshKeranjang();
            btnHapusItem.setEnabled(false);
        }
    }

    private void kosongkanKeranjang() {
        if (keranjang.isEmpty()) return;
        int ok = JOptionPane.showConfirmDialog(this,
            "Kosongkan semua item keranjang?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            keranjang.clear();
            refreshKeranjang();
            tampilStatus("Keranjang dikosongkan.", false);
        }
    }

    private void refreshKeranjang() {
        keranjangModel.setRowCount(0);
        double total = 0;
        for (ItemNota item : keranjang) {
            keranjangModel.addRow(new Object[]{
                item.getNamaBarang(),
                FormatUtil.rupiah(item.getHargaSatuan()),
                item.getQty(),
                FormatUtil.rupiah(item.getSubtotal())
            });
            total += item.getSubtotal();
        }
        lblTotal.setText("TOTAL:  " + FormatUtil.rupiah(total));
    }

    private void muatRiwayatTransaksi() {
        riwayatModel.setRowCount(0);
        List<Transaksi> daftar = transaksiDAO.getRiwayatTransaksi();
        for (Transaksi trx : daftar) {
            riwayatModel.addRow(new Object[]{
                trx.getNoNota(),
                trx.getTanggalJam(),
                trx.getNamaCustomer(),
                trx.getKasir(),
                FormatUtil.rupiah(trx.getTotal())
            });
        }
        btnRefreshRiwayat.setEnabled(true);
    }

    private void prosesBayar() {
        if (keranjang.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Keranjang masih kosong!", "Perhatian",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String namaCustomer = txtCustomer.getText().trim();
        if (namaCustomer.isEmpty()) namaCustomer = "Umum";

        double total = keranjang.stream().mapToDouble(ItemNota::getSubtotal).sum();

        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Proses pembayaran sebesar " + FormatUtil.rupiah(total) + "?",
            "Konfirmasi Bayar", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        btnBayar.setEnabled(false);
        btnBayar.setText("Memproses...");

        final List<ItemNota> itemsCopy = new ArrayList<>(keranjang);
        final String customerFinal     = namaCustomer;
        final double totalFinal        = total;

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return transaksiDAO.simpanTransaksi(
                    customerFinal, adminLogin.getUsername(), itemsCopy, totalFinal);
            }

            @Override
            protected void done() {
                try {
                    String hasil = get();

                    if (hasil != null && !hasil.startsWith("STOK_KURANG")) {
                        String noNota = hasil;
                        tampilStatus("Transaksi berhasil: " + noNota, false);

                        new StrukDialog(KasirFrame.this, noNota, customerFinal,
                                        adminLogin.getUsername(), itemsCopy, totalFinal)
                                .setVisible(true);

                        keranjang.clear();
                        refreshKeranjang();
                        txtCustomer.setText("");
                        muatDaftarBarang(); 
                        muatRiwayatTransaksi();
                        spnQty.setValue(1);

                    } else if (hasil != null && hasil.startsWith("STOK_KURANG")) {
                        String namaBarang = hasil.replace("STOK_KURANG:", "");
                        tampilStatus("Stok tidak cukup: " + namaBarang, true);
                        JOptionPane.showMessageDialog(KasirFrame.this,
                            "Stok barang '" + namaBarang + "' tidak mencukupi!",
                            "Stok Habis", JOptionPane.ERROR_MESSAGE);
                    } else {
                        tampilStatus("Transaksi gagal disimpan!", true);
                    }
                } catch (Exception ex) {
                    tampilStatus("Error: " + ex.getMessage(), true);
                } finally {
                    btnBayar.setEnabled(true);
                    btnBayar.setText("BAYAR");
                }
            }
        };
        worker.execute();
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void tampilStatus(String pesan, boolean error) {
        lblStatus.setText(pesan);
        lblStatus.setForeground(error ? CLR_RED : CLR_GREEN);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(CLR_DARK);
        table.getTableHeader().setForeground(CLR_DARK);
        table.setSelectionBackground(new Color(200, 230, 210));
        table.setGridColor(new Color(230, 235, 240));
        table.setFillsViewportHeight(true);

        if (table == tblKeranjang) {
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        }
    }

    private JLabel buatLabel(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(60, 70, 80));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField buatTextField() {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 215)),
            new EmptyBorder(4, 8, 4, 8)
        ));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JButton buatTombol(String teks, Color warna) {
        JButton btn = new JButton(teks);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setBackground(warna);
        btn.setForeground(CLR_WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buatTombolKecil(String teks, Color warna) {
        JButton btn = new JButton(teks);
        btn.setBackground(warna);
        btn.setForeground(CLR_WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 36));
        return btn;
    }
}
