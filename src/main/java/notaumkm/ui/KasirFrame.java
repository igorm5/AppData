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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
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

    // === WARNA UTAMA ===
    // Header gradient: Indigo/Teal gelap
    private static final Color CLR_HDR_TOP   = new Color(17, 24, 39);    // Gray-900
    private static final Color CLR_HDR_BOT   = new Color(30, 58, 138);   // Blue-900
    // Aksen & tombol
    private static final Color CLR_ACCENT    = new Color(99, 102, 241);  // Indigo-500
    private static final Color CLR_GREEN     = new Color(16, 185, 129);  // Emerald-500
    private static final Color CLR_RED       = new Color(239, 68, 68);   // Red-500
    private static final Color CLR_AMBER     = new Color(245, 158, 11);  // Amber-500
    private static final Color CLR_SLATE_BTN = new Color(71, 85, 105);   // Slate-600
    // Background canvas
    private static final Color CLR_BG        = new Color(248, 250, 252); // Slate-50
    private static final Color CLR_SIDEBAR   = new Color(30, 41, 59);    // Slate-800
    // Kartu & teks
    private static final Color CLR_WHITE     = Color.WHITE;
    private static final Color CLR_CARD      = new Color(255, 255, 255);
    private static final Color CLR_TXT_MAIN  = new Color(15, 23, 42);    // Slate-900
    private static final Color CLR_TXT_MUTED = new Color(100, 116, 139); // Slate-500
    // Baris tabel selang-seling
    private static final Color CLR_ROW_EVEN  = new Color(255, 255, 255);
    private static final Color CLR_ROW_ODD   = new Color(241, 245, 249); // Slate-100
    private static final Color CLR_ROW_SEL   = new Color(224, 231, 255); // Indigo-100

    public KasirFrame(Admin admin) {
        this.adminLogin = admin;
        initUI();
        muatDaftarBarang();
        muatRiwayatTransaksi();
    }

    // ===================================================================
    // initUI
    // ===================================================================
    private void initUI() {
        setTitle("Kasir POS — " + adminLogin.getUsername());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1080, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // ---------- HEADER (gradient) ----------
        GradientPanel header = new GradientPanel(CLR_HDR_TOP, CLR_HDR_BOT, true);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(13, 22, 13, 22));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        titlePanel.setOpaque(false);

        JLabel lblJudul = new JLabel("[ POS ]  Kasir — Point of Sale");
        lblJudul.setForeground(CLR_WHITE);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 19));
        titlePanel.add(lblJudul);

        JPanel tabButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tabButtonsPanel.setOpaque(false);
        TabButton btnTabKasir    = new TabButton("  Kasir POS  ");
        TabButton btnTabRiwayat  = new TabButton("  Riwayat Transaksi  ");
        btnTabKasir.setActive(true);
        tabButtonsPanel.add(btnTabKasir);
        tabButtonsPanel.add(btnTabRiwayat);
        titlePanel.add(tabButtonsPanel);
        header.add(titlePanel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        userPanel.setOpaque(false);
        JLabel lblUser = new JLabel("Kasir: " + adminLogin.getUsername() + "   |   " + FormatUtil.sekarang());
        lblUser.setForeground(new Color(165, 180, 252)); // Indigo-300
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userPanel.add(lblUser);
        header.add(userPanel, BorderLayout.EAST);

        // ---------- SIDEBAR KIRI (dark) ----------
        JPanel pilihPanelWrapper = new JPanel(new BorderLayout());
        pilihPanelWrapper.setBackground(CLR_SIDEBAR);
        pilihPanelWrapper.setPreferredSize(new Dimension(285, 0));
        pilihPanelWrapper.setBorder(new EmptyBorder(20, 16, 20, 16));

        JPanel formInner = new JPanel();
        formInner.setLayout(new BoxLayout(formInner, BoxLayout.Y_AXIS));
        formInner.setOpaque(false);

        // -- Section label helper --
        formInner.add(buatSeksiLabel("CUSTOMER"));
        formInner.add(Box.createVerticalStrut(6));
        txtCustomer = buatTextField();
        txtCustomer.setToolTipText("Kosongkan untuk pelanggan umum");
        formInner.add(txtCustomer);
        formInner.add(Box.createVerticalStrut(18));

        formInner.add(buatSeksiLabel("PILIH BARANG"));
        formInner.add(Box.createVerticalStrut(6));

        cmbBarang = new JComboBox<>();
        cmbBarang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbBarang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbBarang.setPreferredSize(new Dimension(0, 36));
        cmbBarang.setBackground(Color.WHITE);
        cmbBarang.setForeground(CLR_TXT_MAIN);
        cmbBarang.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbBarang.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                if (value == null && index == -1) {
                    setText("Pilih Barang...");
                    setForeground(new Color(148, 163, 184));
                } else if (value instanceof Barang) {
                    setText(((Barang) value).getNamaBarang());
                }
                if (isSelected) {
                    setBackground(new Color(99, 102, 241));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(CLR_TXT_MAIN);
                }
                return this;
            }
        });
        cmbBarang.addActionListener(e -> tampilInfoStok());
        cmbBarang.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { cmbBarang.setPopupVisible(false); }
        });
        cmbBarang.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {}
            @Override public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
                cmbBarang.setPopupVisible(false);
            }
        });
        formInner.add(cmbBarang);
        formInner.add(Box.createVerticalStrut(7));

        lblStokInfo = new JLabel("Stok tersedia: 0   |   Rp 0/satuan");
        lblStokInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStokInfo.setForeground(new Color(186, 200, 220)); // lebih terang agar terbaca di sidebar gelap
        lblStokInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formInner.add(lblStokInfo);
        formInner.add(Box.createVerticalStrut(18));

        formInner.add(buatSeksiLabel("JUMLAH (QTY)"));
        formInner.add(Box.createVerticalStrut(6));

        spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spnQty.setFont(new Font("Segoe UI", Font.BOLD, 15));
        spnQty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        spnQty.setPreferredSize(new Dimension(0, 38));
        spnQty.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComponent editor = spnQty.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField stf = ((JSpinner.DefaultEditor) editor).getTextField();
            stf.setBackground(new Color(51, 65, 85));
            stf.setForeground(new Color(226, 232, 240));
            stf.setFont(new Font("Segoe UI", Font.BOLD, 15));
            stf.setBorder(null);
            stf.setHorizontalAlignment(JTextField.CENTER);
        }
        formInner.add(spnQty);
        formInner.add(Box.createVerticalStrut(22));

        // Separator line
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        formInner.add(sep);
        formInner.add(Box.createVerticalStrut(16));

        btnTambahItem = buatTombol("+ Tambah ke Keranjang", CLR_GREEN);
        btnTambahItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        formInner.add(btnTambahItem);
        formInner.add(Box.createVerticalStrut(8));

        btnHapusItem = buatTombol("Hapus Item Terpilih", CLR_RED);
        btnHapusItem.setEnabled(false);
        btnHapusItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        formInner.add(btnHapusItem);
        formInner.add(Box.createVerticalStrut(8));

        btnBersihkan = buatTombol("Kosongkan Keranjang", CLR_SLATE_BTN);
        btnBersihkan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        formInner.add(btnBersihkan);
        formInner.add(Box.createVerticalGlue());

        // Logout di bawah
        btnLogout = buatTombol("Logout", new Color(127, 29, 29)); // Red-900
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        formInner.add(Box.createVerticalStrut(14));
        formInner.add(btnLogout);
        formInner.add(Box.createVerticalStrut(10));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(new Color(186, 200, 220)); // lebih terang agar terbaca di sidebar gelap
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        formInner.add(lblStatus);

        pilihPanelWrapper.add(formInner, BorderLayout.CENTER);

        // ---------- KERANJANG (area kanan, background terang) ----------
        JPanel panelKeranjang = new JPanel(new BorderLayout(0, 12));
        panelKeranjang.setBackground(CLR_BG);
        panelKeranjang.setBorder(new EmptyBorder(16, 12, 16, 16));

        // Label judul keranjang
        JLabel lblKeranjangTitle = new JLabel("  Keranjang Belanja");
        lblKeranjangTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblKeranjangTitle.setForeground(new Color(30, 41, 59));
        lblKeranjangTitle.setBorder(new EmptyBorder(0, 0, 8, 0));

        String[] kolom = {"Nama Barang", "Harga Satuan", "Qty", "Subtotal"};
        keranjangModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKeranjang = new JTable(keranjangModel);
        styleTable(tblKeranjang);

        JScrollPane scrollKeranjang = new JScrollPane(tblKeranjang);
        scrollKeranjang.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225))); // Slate-300
        scrollKeranjang.getViewport().setBackground(CLR_WHITE);

        // Kartu wrapper untuk tabel
        RoundedPanel tableCard = new RoundedPanel(14, CLR_CARD);
        tableCard.setLayout(new BorderLayout(0, 8));
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));
        tableCard.add(lblKeranjangTitle, BorderLayout.NORTH);
        tableCard.add(scrollKeranjang, BorderLayout.CENTER);

        // --- Panel total & bayar ---
        GradientPanel bottomPanel = new GradientPanel(new Color(30, 58, 138), new Color(67, 56, 202), true);
        bottomPanel.setLayout(new BorderLayout(16, 0));
        bottomPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel totalWrapper = new JPanel(new BorderLayout(4, 2));
        totalWrapper.setOpaque(false);

        JLabel lblTotalTitle = new JLabel("TOTAL BAYAR");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTotalTitle.setForeground(new Color(199, 210, 254)); // Indigo-200

        lblTotal = new JLabel("TOTAL:  Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotal.setForeground(CLR_WHITE);

        totalWrapper.add(lblTotalTitle, BorderLayout.NORTH);
        totalWrapper.add(lblTotal, BorderLayout.CENTER);

        btnBayar = buatTombol("BAYAR", CLR_GREEN);
        btnBayar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBayar.setPreferredSize(new Dimension(160, 52));

        bottomPanel.add(totalWrapper, BorderLayout.CENTER);
        bottomPanel.add(btnBayar, BorderLayout.EAST);

        panelKeranjang.add(tableCard, BorderLayout.CENTER);
        panelKeranjang.add(bottomPanel, BorderLayout.SOUTH);

        // ---------- RIWAYAT TRANSAKSI ----------
        JPanel panelRiwayat = new JPanel(new BorderLayout(0, 12));
        panelRiwayat.setBackground(CLR_BG);
        panelRiwayat.setBorder(new EmptyBorder(16, 16, 16, 16));

        RoundedPanel riwayatCard = new RoundedPanel(14, CLR_CARD);
        riwayatCard.setLayout(new BorderLayout(0, 10));
        riwayatCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel riwayatHeaderRow = new JPanel(new BorderLayout());
        riwayatHeaderRow.setOpaque(false);

        JLabel lblRiwayatTitle = new JLabel("  Riwayat Transaksi Terakhir");
        lblRiwayatTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRiwayatTitle.setForeground(new Color(30, 41, 59));

        btnRefreshRiwayat = buatTombol("Refresh Riwayat", CLR_ACCENT);
        btnRefreshRiwayat.setPreferredSize(new Dimension(155, 36));

        riwayatHeaderRow.add(lblRiwayatTitle, BorderLayout.WEST);
        riwayatHeaderRow.add(btnRefreshRiwayat, BorderLayout.EAST);

        String[] kolomRiwayat = {"No. Nota", "Tanggal", "Customer", "Kasir", "Total"};
        riwayatModel = new DefaultTableModel(kolomRiwayat, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRiwayat = new JTable(riwayatModel);
        styleTable(tblRiwayat);

        JScrollPane scrollRiwayat = new JScrollPane(tblRiwayat);
        scrollRiwayat.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        scrollRiwayat.getViewport().setBackground(CLR_WHITE);

        riwayatCard.add(riwayatHeaderRow, BorderLayout.NORTH);
        riwayatCard.add(scrollRiwayat, BorderLayout.CENTER);
        panelRiwayat.add(riwayatCard, BorderLayout.CENTER);

        // ---------- CARD LAYOUT ----------
        JPanel mainPosContainer = new JPanel(new BorderLayout());
        mainPosContainer.add(pilihPanelWrapper, BorderLayout.WEST);
        mainPosContainer.add(panelKeranjang, BorderLayout.CENTER);

        CardLayout cardLayout = new CardLayout();
        JPanel centerContainer = new JPanel(cardLayout);
        centerContainer.add(mainPosContainer, "POS");
        centerContainer.add(panelRiwayat, "RIWAYAT");

        getContentPane().setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);

        // ---------- LISTENERS ----------
        btnTabKasir.addActionListener(e -> {
            btnTabKasir.setActive(true);
            btnTabRiwayat.setActive(false);
            cardLayout.show(centerContainer, "POS");
        });
        btnTabRiwayat.addActionListener(e -> {
            btnTabKasir.setActive(false);
            btnTabRiwayat.setActive(true);
            cardLayout.show(centerContainer, "RIWAYAT");
            muatRiwayatTransaksi();
        });
        btnTambahItem.addActionListener(e -> { cmbBarang.setPopupVisible(false); tambahKeKeranjang(); });
        btnHapusItem.addActionListener(e -> { cmbBarang.setPopupVisible(false); hapusItemTerpilih(); });
        btnBersihkan.addActionListener(e -> { cmbBarang.setPopupVisible(false); kosongkanKeranjang(); });
        btnBayar.addActionListener(e -> { cmbBarang.setPopupVisible(false); prosesBayar(); });
        btnLogout.addActionListener(e -> { cmbBarang.setPopupVisible(false); logout(); });
        btnRefreshRiwayat.addActionListener(e -> {
            cmbBarang.setPopupVisible(false);
            btnRefreshRiwayat.setEnabled(false);
            muatRiwayatTransaksi();
        });
        tblKeranjang.getSelectionModel().addListSelectionListener(
            evt -> btnHapusItem.setEnabled(tblKeranjang.getSelectedRow() >= 0)
        );
    }

    // ===================================================================
    // BUSINESS LOGIC  (tidak diubah)
    // ===================================================================
    private void muatDaftarBarang() {
        cmbBarang.removeAllItems();
        for (Barang b : barangDAO.getAll()) cmbBarang.addItem(b);
        cmbBarang.setSelectedIndex(-1);
        cmbBarang.setPopupVisible(false);
        tampilInfoStok();
    }

    private void tampilInfoStok() {
        Barang terpilih = (Barang) cmbBarang.getSelectedItem();
        if (terpilih == null) {
            lblStokInfo.setText("Stok tersedia: 0   |   Rp 0/satuan");
            lblStokInfo.setForeground(new Color(186, 200, 220));
            return;
        }
        lblStokInfo.setText("Stok: " + terpilih.getStok()
                            + "   |   " + FormatUtil.rupiah(terpilih.getHargaSatuan()) + "/satuan");
        lblStokInfo.setForeground(terpilih.getStok() < 5 ? CLR_RED : CLR_GREEN);
    }

    private void tambahKeKeranjang() {
        Barang barang = (Barang) cmbBarang.getSelectedItem();
        if (barang == null) return;
        int qty = (int) spnQty.getValue();
        if (barang.getStok() < qty) {
            tampilStatus("Stok " + barang.getNamaBarang() + " tidak mencukupi! (Stok: " + barang.getStok() + ")", true);
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
        int ok = JOptionPane.showConfirmDialog(this, "Kosongkan semua item keranjang?",
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
        for (Transaksi trx : transaksiDAO.getRiwayatTransaksi()) {
            riwayatModel.addRow(new Object[]{
                trx.getNoNota(), trx.getTanggalJam(), trx.getNamaCustomer(),
                trx.getKasir(), FormatUtil.rupiah(trx.getTotal())
            });
        }
        btnRefreshRiwayat.setEnabled(true);
    }

    private void prosesBayar() {
        if (keranjang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!", "Perhatian", JOptionPane.WARNING_MESSAGE);
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

        final List<ItemNota> itemsCopy   = new ArrayList<>(keranjang);
        final String customerFinal       = namaCustomer;
        final double totalFinal          = total;

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override protected String doInBackground() {
                return transaksiDAO.simpanTransaksi(customerFinal, adminLogin.getUsername(), itemsCopy, totalFinal);
            }
            @Override protected void done() {
                try {
                    String hasil = get();
                    if (hasil != null && !hasil.startsWith("STOK_KURANG")) {
                        tampilStatus("Transaksi berhasil: " + hasil, false);
                        new StrukDialog(KasirFrame.this, hasil, customerFinal,
                                adminLogin.getUsername(), itemsCopy, totalFinal).setVisible(true);
                        keranjang.clear();
                        refreshKeranjang();
                        txtCustomer.setText("");
                        muatDaftarBarang();
                        muatRiwayatTransaksi();
                        spnQty.setValue(1);
                    } else if (hasil != null && hasil.startsWith("STOK_KURANG")) {
                        String nb = hasil.replace("STOK_KURANG:", "");
                        tampilStatus("Stok tidak cukup: " + nb, true);
                        JOptionPane.showMessageDialog(KasirFrame.this,
                                "Stok barang '" + nb + "' tidak mencukupi!", "Stok Habis", JOptionPane.ERROR_MESSAGE);
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
        lblStatus.setForeground(error ? new Color(252, 165, 165) : new Color(110, 231, 183)); // light red/green on dark bg
    }

    // ===================================================================
    // STYLING HELPERS
    // ===================================================================
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setGridColor(new Color(226, 232, 240));
        table.setFillsViewportHeight(true);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(CLR_ROW_SEL);
        table.setSelectionForeground(CLR_TXT_MAIN);

        // Header
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, isSel, hasFocus, r, c);
                lbl.setOpaque(true);
                lbl.setBackground(new Color(30, 41, 59));
                lbl.setForeground(new Color(199, 210, 254)); // Indigo-200
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, CLR_ACCENT),
                    BorderFactory.createEmptyBorder(9, 12, 9, 12)
                ));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });

        // Cells (selang-seling / zebra striping)
        DefaultTableCellRenderer zebraRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, isSel, hasFocus, r, c);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (isSel) {
                    lbl.setBackground(CLR_ROW_SEL);
                    lbl.setForeground(new Color(55, 48, 163)); // Indigo-800
                } else {
                    lbl.setBackground(r % 2 == 0 ? CLR_ROW_EVEN : CLR_ROW_ODD);
                    lbl.setForeground(CLR_TXT_MAIN);
                }
                return lbl;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);

        if (table == tblKeranjang) {
            table.getColumnModel().getColumn(0).setPreferredWidth(145);
            table.getColumnModel().getColumn(1).setPreferredWidth(80);
            table.getColumnModel().getColumn(2).setPreferredWidth(38);
            table.getColumnModel().getColumn(3).setPreferredWidth(85);
            // Qty centered
            DefaultTableCellRenderer centerZebra = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFocus, int r, int c) {
                    JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, isSel, hasFocus, r, c);
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                    lbl.setBackground(isSel ? CLR_ROW_SEL : (r % 2 == 0 ? CLR_ROW_EVEN : CLR_ROW_ODD));
                    lbl.setForeground(isSel ? new Color(55, 48, 163) : CLR_TXT_MAIN);
                    return lbl;
                }
            };
            table.getColumnModel().getColumn(2).setCellRenderer(centerZebra);
        }
    }

    /** Label seksi di sidebar gelap */
    private JLabel buatSeksiLabel(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(165, 180, 252)); // Indigo-300
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField buatTextField() {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(new Color(51, 65, 85)); // Slate-700
        tf.setForeground(new Color(226, 232, 240)); // Slate-200
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105)), // Slate-600
            new EmptyBorder(8, 12, 8, 12)
        ));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CLR_ACCENT, 2),
                    new EmptyBorder(7, 11, 7, 11)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(71, 85, 105)),
                    new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        return tf;
    }

    private JButton buatTombol(String teks, Color warna) {
        JButton btn = new RoundedButton(teks, warna, 10);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private JButton buatTombolKecil(String teks, Color warna) {
        JButton btn = new RoundedButton(teks, warna, 8);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }

    // ===================================================================
    // CUSTOM SWING COMPONENTS
    // ===================================================================

    /** Panel dengan latar belakang warna solid + sudut membulat */
    static class RoundedPanel extends JPanel {
        private final int radius;
        private Color bgColor;
        RoundedPanel(int r, Color bg) { this.radius = r; this.bgColor = bg; setOpaque(false); }
        public void setBackgroundColor(Color c) { bgColor = c; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(new Color(203, 213, 225, 120));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Panel dengan gradient linear sebagai latar belakang */
    static class GradientPanel extends JPanel {
        private final Color c1, c2;
        private final boolean horizontal;
        GradientPanel(Color c1, Color c2, boolean horizontal) {
            this.c1 = c1; this.c2 = c2; this.horizontal = horizontal;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = horizontal
                ? new GradientPaint(0, 0, c1, getWidth(), 0, c2)
                : new GradientPaint(0, 0, c1, 0, getHeight(), c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Tombol bulat dengan hover & pressed state, text subpixel antialiased */
    static class RoundedButton extends JButton {
        private final Color base;
        private final int radius;
        RoundedButton(String text, Color color, int radius) {
            super(text);
            this.base = color;
            this.radius = radius;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setRolloverEnabled(true);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            Color bg; Color fg;
            if (!isEnabled()) {
                bg = new Color(51, 65, 85);    // Slate-700
                fg = new Color(100, 116, 139); // Slate-500
            } else if (getModel().isPressed()) {
                bg = base.darker().darker();
                fg = Color.WHITE;
            } else if (getModel().isRollover()) {
                // brighten slightly
                float[] hsb = Color.RGBtoHSB(base.getRed(), base.getGreen(), base.getBlue(), null);
                bg = Color.getHSBColor(hsb[0], Math.max(0f, hsb[1] - 0.08f), Math.min(1f, hsb[2] + 0.12f));
                fg = Color.WHITE;
            } else {
                bg = base; fg = Color.WHITE;
            }

            // Drop shadow subtle
            g2.setColor(new Color(0, 0, 0, 35));
            g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 2, radius, radius);

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 2, radius, radius);

            g2.setColor(fg);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 1;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    /** Tombol navigasi tab header dengan active/inactive state */
    static class TabButton extends JButton {
        private boolean active;
        TabButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setRolloverEnabled(true);
        }
        public void setActive(boolean active) { this.active = active; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            Color bg, fg;
            if (active) {
                bg = new Color(99, 102, 241); fg = Color.WHITE; // Indigo-500
            } else if (getModel().isRollover()) {
                bg = new Color(55, 65, 81); fg = new Color(224, 231, 255); // Gray-700, Indigo-100
            } else {
                bg = new Color(31, 41, 55, 200); fg = new Color(200, 210, 230); // Gray-800 semi-trans, teks lebih terang
            }

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

            // bottom underline when active
            if (active) {
                g2.setColor(new Color(167, 243, 208)); // Emerald-200
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(8, getHeight() - 2, getWidth() - 8, getHeight() - 2);
            }

            g2.setColor(fg);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}
