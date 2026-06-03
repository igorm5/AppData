package notaumkm.ui;

import notaumkm.db.BarangDAO;
import notaumkm.model.Admin;
import notaumkm.model.Barang;
import notaumkm.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * PengelolaStokFrame
 * Halaman manajemen barang untuk role Pengelola Stok.
 * Fitur: tambah, ubah, hapus barang dan kelola stok.
 */
public class PengelolaStokFrame extends JFrame {

    // ── Komponen UI ──────────────────────────────────────────────────────────
    private JTable          tblBarang;
    private DefaultTableModel tabelModel;
    private JTextField      txtNama, txtHarga, txtStok, txtCari;
    private JButton         btnTambah, btnUpdate, btnHapus, btnBersihkan, btnLogout;
    private JLabel          lblStatus;

    // ── Data & DAO ────────────────────────────────────────────────────────────
    private final Admin     adminLogin;
    private final BarangDAO barangDAO = new BarangDAO();
    private List<Barang>    dataBarang;
    private Barang          selectedBarang = null;

    // ── Warna tema ────────────────────────────────────────────────────────────
    private static final Color CLR_DARK    = new Color(27,  44,  56);
    private static final Color CLR_GREEN   = new Color(52, 120,  77);
    private static final Color CLR_ORANGE  = new Color(200, 100,  30);
    private static final Color CLR_RED     = new Color(190,  50,  50);
    private static final Color CLR_BG      = new Color(245, 247, 250);
    private static final Color CLR_WHITE   = Color.WHITE;

    // ── Konstruktor ───────────────────────────────────────────────────────────

    public PengelolaStokFrame(Admin admin) {
        this.adminLogin = admin;
        initUI();
        muatDataBarang();
    }

    // ── Inisialisasi UI ───────────────────────────────────────────────────────

    private void initUI() {
        setTitle("Pengelola Stok — " + adminLogin.getUsername());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 640);
        setLocationRelativeTo(null);
        setBackground(CLR_BG);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CLR_DARK);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblJudul = new JLabel("⚙  Manajemen Data Barang");
        lblJudul.setForeground(CLR_WHITE);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(lblJudul, BorderLayout.WEST);

        JLabel lblUser = new JLabel("Login: " + adminLogin.getUsername()
                                    + "  |  Role: Pengelola Stok");
        lblUser.setForeground(new Color(160, 190, 170));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        header.add(lblUser, BorderLayout.EAST);

        // ── Panel kiri: form input ─────────────────────────────────────────────
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CLR_WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 225, 230)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setPreferredSize(new Dimension(260, 0));

        formPanel.add(buatLabelForm("Nama Barang"));
        formPanel.add(Box.createVerticalStrut(5));
        txtNama  = buatField();
        formPanel.add(txtNama);
        formPanel.add(Box.createVerticalStrut(12));

        formPanel.add(buatLabelForm("Harga Satuan (Rp)"));
        formPanel.add(Box.createVerticalStrut(5));
        txtHarga = buatField();
        formPanel.add(txtHarga);
        formPanel.add(Box.createVerticalStrut(12));

        formPanel.add(buatLabelForm("Stok"));
        formPanel.add(Box.createVerticalStrut(5));
        txtStok  = buatField();
        formPanel.add(txtStok);
        formPanel.add(Box.createVerticalStrut(20));

        // Tombol-tombol aksi
        btnTambah    = buatTombol("Tambah Baru",    CLR_GREEN);
        btnUpdate    = buatTombol("Simpan Perubahan", CLR_ORANGE);
        btnHapus     = buatTombol("Hapus Barang",   CLR_RED);
        btnBersihkan = buatTombol("Bersihkan Form", new Color(100, 110, 120));

        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);

        formPanel.add(btnTambah);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(btnUpdate);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(btnHapus);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(btnBersihkan);
        formPanel.add(Box.createVerticalGlue());

        // Label status
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(CLR_GREEN);
        formPanel.add(lblStatus);

        // ── Panel kanan: tabel barang ──────────────────────────────────────────
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(CLR_BG);
        rightPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(CLR_BG);
        JLabel lblCari = new JLabel("🔍 Cari:");
        lblCari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCari = new JTextField();
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCari.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 215)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate (javax.swing.event.DocumentEvent e) { filterTabel(); }
            public void removeUpdate (javax.swing.event.DocumentEvent e) { filterTabel(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTabel(); }
        });
        searchPanel.add(lblCari, BorderLayout.WEST);
        searchPanel.add(txtCari, BorderLayout.CENTER);

        // Logout di kanan
        btnLogout = buatTombol("⏻ Logout", new Color(80, 90, 100));
        btnLogout.setPreferredSize(new Dimension(90, 36));
        searchPanel.add(btnLogout, BorderLayout.EAST);

        // Tabel
        String[] kolom = {"#", "Nama Barang", "Harga Satuan", "Stok"};
        tabelModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBarang = new JTable(tabelModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(tblBarang);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));

        rightPanel.add(searchPanel,  BorderLayout.NORTH);
        rightPanel.add(scrollPane,   BorderLayout.CENTER);

        // ── Layout utama ──────────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout());
        body.add(formPanel,  BorderLayout.WEST);
        body.add(rightPanel, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(body,   BorderLayout.CENTER);

        // ── Event listener ────────────────────────────────────────────────────
        btnTambah.addActionListener   (e -> prosesTabah());
        btnUpdate.addActionListener   (e -> prosesUpdate());
        btnHapus.addActionListener    (e -> prosesHapus());
        btnBersihkan.addActionListener(e -> bersihkanForm());
        btnLogout.addActionListener   (e -> logout());

        // Klik baris tabel → isi form
        tblBarang.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) isiFormDariTabel();
        });
    }

    // ── Muat data ─────────────────────────────────────────────────────────────

    private void muatDataBarang() {
        dataBarang = barangDAO.getAll();
        refreshTabel(dataBarang);
    }

    private void refreshTabel(List<Barang> list) {
        tabelModel.setRowCount(0);
        int no = 1;
        for (Barang b : list) {
            tabelModel.addRow(new Object[]{
                no++,
                b.getNamaBarang(),
                FormatUtil.rupiah(b.getHargaSatuan()),
                b.getStok()
            });
        }
    }

    private void filterTabel() {
        String kata = txtCari.getText().trim().toLowerCase();
        if (kata.isEmpty()) {
            refreshTabel(dataBarang);
            return;
        }
        List<Barang> filtered = dataBarang.stream()
            .filter(b -> b.getNamaBarang().toLowerCase().contains(kata))
            .toList();
        refreshTabel(filtered);
    }

    // ── Aksi CRUD ──────────────────────────────────────────────────────────────

    private void prosesTabah() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) { tampilStatus("Nama barang wajib diisi!", true); return; }

        double harga;
        int    stok;
        try {
            harga = Double.parseDouble(txtHarga.getText().trim().replace(".", "").replace(",", "."));
            stok  = Integer.parseInt(txtStok.getText().trim());
        } catch (NumberFormatException ex) {
            tampilStatus("Harga dan stok harus berupa angka!", true);
            return;
        }

        Barang baru = new Barang(0, nama, harga, stok);
        if (barangDAO.insert(baru)) {
            tampilStatus("Barang berhasil ditambahkan.", false);
            bersihkanForm();
            muatDataBarang();
        } else {
            tampilStatus("Gagal menambahkan barang (nama mungkin sudah ada).", true);
        }
    }

    private void prosesUpdate() {
        if (selectedBarang == null) return;

        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) { tampilStatus("Nama barang wajib diisi!", true); return; }

        double harga;
        int    stok;
        try {
            harga = Double.parseDouble(txtHarga.getText().trim().replace(".", "").replace(",", "."));
            stok  = Integer.parseInt(txtStok.getText().trim());
        } catch (NumberFormatException ex) {
            tampilStatus("Harga dan stok harus berupa angka!", true);
            return;
        }

        selectedBarang.setNamaBarang(nama);
        selectedBarang.setHargaSatuan(harga);
        selectedBarang.setStok(stok);

        if (barangDAO.update(selectedBarang)) {
            tampilStatus("Barang berhasil diperbarui.", false);
            bersihkanForm();
            muatDataBarang();
        } else {
            tampilStatus("Gagal memperbarui barang.", true);
        }
    }

    private void prosesHapus() {
        if (selectedBarang == null) return;

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Yakin hapus barang: " + selectedBarang.getNamaBarang() + "?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (barangDAO.delete(selectedBarang.getIdBarang())) {
                tampilStatus("Barang berhasil dihapus.", false);
                bersihkanForm();
                muatDataBarang();
            } else {
                tampilStatus("Gagal hapus (barang mungkin ada di transaksi).", true);
            }
        }
    }

    private void bersihkanForm() {
        txtNama.setText("");
        txtHarga.setText("");
        txtStok.setText("");
        selectedBarang = null;
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
        btnTambah.setEnabled(true);
        tblBarang.clearSelection();
        lblStatus.setText(" ");
    }

    /** Isi form dari baris yang dipilih di tabel */
    private void isiFormDariTabel() {
        int row = tblBarang.getSelectedRow();
        if (row < 0) return;

        String nama = (String) tabelModel.getValueAt(row, 1);
        selectedBarang = dataBarang.stream()
            .filter(b -> b.getNamaBarang().equals(nama))
            .findFirst().orElse(null);

        if (selectedBarang != null) {
            txtNama.setText(selectedBarang.getNamaBarang());
            txtHarga.setText(String.valueOf((int) selectedBarang.getHargaSatuan()));
            txtStok.setText(String.valueOf(selectedBarang.getStok()));
            btnUpdate.setEnabled(true);
            btnHapus.setEnabled(true);
            btnTambah.setEnabled(false);
        }
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void tampilStatus(String pesan, boolean error) {
        lblStatus.setText(pesan);
        lblStatus.setForeground(error ? CLR_RED : CLR_GREEN);
    }

    // ── Helper UI ─────────────────────────────────────────────────────────────

    private void styleTable() {
        tblBarang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblBarang.setRowHeight(28);
        tblBarang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblBarang.getTableHeader().setBackground(CLR_DARK);
        tblBarang.getTableHeader().setForeground(CLR_DARK);
        tblBarang.setSelectionBackground(new Color(200, 230, 210));
        tblBarang.setGridColor(new Color(230, 235, 240));
        tblBarang.setShowGrid(true);
        tblBarang.setFillsViewportHeight(true);

        // Kolom # sempit
        tblBarang.getColumnModel().getColumn(0).setMaxWidth(40);
        // Kolom stok: warna merah jika rendah
        tblBarang.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                int stok = (int) v;
                c.setForeground(stok < 10 ? Color.RED : CLR_DARK);
                setHorizontalAlignment(CENTER);
                return c;
            }
        });
    }

    private JLabel buatLabelForm(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(80, 90, 100));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField buatField() {
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
}
