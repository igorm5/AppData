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

public class PengelolaStokFrame extends JFrame {

    private JTable            tblBarang;
    private DefaultTableModel tabelModel;
    private JTextField        txtNama, txtHarga, txtStok, txtCari;
    private JButton           btnTambah, btnUpdate, btnHapus, btnBersihkan, btnLogout;
    private JLabel            lblStatus;

    private final Admin       adminLogin;
    private final BarangDAO   barangDAO = new BarangDAO();
    private List<Barang>      dataBarang;
    private Barang            selectedBarang = null;

    // === PALET WARNA (konsisten dengan KasirFrame) ===
    private static final Color CLR_HDR_TOP   = new Color(17, 24, 39);
    private static final Color CLR_HDR_BOT   = new Color(30, 58, 138);
    private static final Color CLR_ACCENT    = new Color(99, 102, 241);  // Indigo-500
    private static final Color CLR_GREEN     = new Color(16, 185, 129);  // Emerald-500
    private static final Color CLR_RED       = new Color(239, 68, 68);   // Red-500
    private static final Color CLR_AMBER     = new Color(245, 158, 11);  // Amber-500
    private static final Color CLR_SIDEBAR   = new Color(30, 41, 59);    // Slate-800
    private static final Color CLR_BG        = new Color(248, 250, 252); // Slate-50
    private static final Color CLR_WHITE     = Color.WHITE;
    private static final Color CLR_CARD      = new Color(255, 255, 255);
    private static final Color CLR_TXT_MAIN  = new Color(15, 23, 42);
    private static final Color CLR_TXT_MUTED = new Color(100, 116, 139);
    private static final Color CLR_ROW_EVEN  = new Color(255, 255, 255);
    private static final Color CLR_ROW_ODD   = new Color(241, 245, 249);
    private static final Color CLR_ROW_SEL   = new Color(224, 231, 255);

    public PengelolaStokFrame(Admin admin) {
        this.adminLogin = admin;
        initUI();
        muatDataBarang();
    }

    private void initUI() {
        setTitle("Pengelola Stok — " + adminLogin.getUsername());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 680);
        setMinimumSize(new Dimension(800, 540));
        setLocationRelativeTo(null);

        // ---------- HEADER gradient ----------
        GradientPanel header = new GradientPanel(CLR_HDR_TOP, CLR_HDR_BOT, true);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(13, 22, 13, 22));

        JLabel lblJudul = new JLabel("Manajemen Data Barang");
        lblJudul.setForeground(CLR_WHITE);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 19));

        JLabel lblUser = new JLabel("Login: " + adminLogin.getUsername() + "  |  Role: Pengelola Stok");
        lblUser.setForeground(new Color(165, 180, 252));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        header.add(lblJudul, BorderLayout.WEST);
        header.add(lblUser, BorderLayout.EAST);

        // ---------- SIDEBAR FORM (dark) ----------
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(CLR_SIDEBAR);
        formWrapper.setPreferredSize(new Dimension(270, 0));
        formWrapper.setBorder(new EmptyBorder(20, 16, 20, 16));

        JPanel formInner = new JPanel();
        formInner.setLayout(new BoxLayout(formInner, BoxLayout.Y_AXIS));
        formInner.setOpaque(false);

        JLabel lblFormTitle = new JLabel("Form Barang");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(new Color(165, 180, 252));
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formInner.add(lblFormTitle);
        formInner.add(Box.createVerticalStrut(18));

        formInner.add(buatSeksiLabel("NAMA BARANG"));
        formInner.add(Box.createVerticalStrut(6));
        txtNama = buatTextField();
        formInner.add(txtNama);
        formInner.add(Box.createVerticalStrut(14));

        formInner.add(buatSeksiLabel("HARGA SATUAN (Rp)"));
        formInner.add(Box.createVerticalStrut(6));
        txtHarga = buatTextField();
        formInner.add(txtHarga);
        formInner.add(Box.createVerticalStrut(14));

        formInner.add(buatSeksiLabel("STOK"));
        formInner.add(Box.createVerticalStrut(6));
        txtStok = buatTextField();
        formInner.add(txtStok);
        formInner.add(Box.createVerticalStrut(22));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        formInner.add(sep);
        formInner.add(Box.createVerticalStrut(16));

        btnTambah    = buatTombol("Tambah Baru",          CLR_GREEN);
        btnUpdate    = buatTombol("Simpan Perubahan",      CLR_AMBER);
        btnHapus     = buatTombol("Hapus Barang",          CLR_RED);
        btnBersihkan = buatTombol("Bersihkan Form",       new Color(71, 85, 105));

        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);

        for (JButton btn : new JButton[]{btnTambah, btnUpdate, btnHapus, btnBersihkan}) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            formInner.add(btn);
            formInner.add(Box.createVerticalStrut(8));
        }
        formInner.add(Box.createVerticalGlue());

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(new Color(110, 231, 183));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        formInner.add(lblStatus);

        formWrapper.add(formInner, BorderLayout.CENTER);

        // ---------- PANEL KANAN (tabel + search + logout) ----------
        JPanel rightPanel = new JPanel(new BorderLayout(0, 12));
        rightPanel.setBackground(CLR_BG);
        rightPanel.setBorder(new EmptyBorder(16, 12, 16, 16));

        // Search bar + Logout row
        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.setOpaque(false);

        JPanel searchBox = new JPanel(new BorderLayout(8, 0));
        searchBox.setOpaque(false);

        JLabel lblCariIcon = new JLabel("Cari:");
        lblCariIcon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCariIcon.setForeground(CLR_TXT_MUTED);

        txtCari = new JTextField();
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCari.setBackground(CLR_WHITE);
        txtCari.setForeground(CLR_TXT_MAIN);
        txtCari.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            new EmptyBorder(7, 10, 7, 10)
        ));
        txtCari.setToolTipText("Cari nama barang...");
        txtCari.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                txtCari.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CLR_ACCENT, 2),
                    new EmptyBorder(6, 9, 6, 9)));
            }
            @Override public void focusLost(FocusEvent e) {
                txtCari.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    new EmptyBorder(7, 10, 7, 10)));
            }
        });
        txtCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filterTabel(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filterTabel(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTabel(); }
        });

        searchBox.add(lblCariIcon, BorderLayout.WEST);
        searchBox.add(txtCari,     BorderLayout.CENTER);

        btnLogout = buatTombol("Logout", new Color(127, 29, 29));
        btnLogout.setPreferredSize(new Dimension(100, 38));

        topRow.add(searchBox, BorderLayout.CENTER);
        topRow.add(btnLogout, BorderLayout.EAST);

        // Tabel card
        RoundedPanel tableCard = new RoundedPanel(14, CLR_CARD);
        tableCard.setLayout(new BorderLayout(0, 0));
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel lblTableTitle = new JLabel("  Daftar Barang");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTableTitle.setForeground(new Color(30, 41, 59));
        lblTableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        String[] kolom = {"#", "Nama Barang", "Harga Satuan", "Stok"};
        tabelModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBarang = new JTable(tabelModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(tblBarang);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        scrollPane.getViewport().setBackground(CLR_WHITE);

        tableCard.add(lblTableTitle, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        rightPanel.add(topRow,     BorderLayout.NORTH);
        rightPanel.add(tableCard,  BorderLayout.CENTER);

        // ---------- BODY ----------
        JPanel body = new JPanel(new BorderLayout());
        body.add(formWrapper, BorderLayout.WEST);
        body.add(rightPanel,  BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(body,   BorderLayout.CENTER);

        // ---------- LISTENERS ----------
        btnTambah.addActionListener   (e -> prosesTabah());
        btnUpdate.addActionListener   (e -> prosesUpdate());
        btnHapus.addActionListener    (e -> prosesHapus());
        btnBersihkan.addActionListener(e -> bersihkanForm());
        btnLogout.addActionListener   (e -> logout());
        tblBarang.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) isiFormDariTabel();
        });
    }

    // ===================================================================
    // BUSINESS LOGIC (tidak diubah)
    // ===================================================================
    private void muatDataBarang() {
        dataBarang = barangDAO.getAll();
        refreshTabel(dataBarang);
    }

    private void refreshTabel(List<Barang> list) {
        tabelModel.setRowCount(0);
        int no = 1;
        for (Barang b : list)
            tabelModel.addRow(new Object[]{ no++, b.getNamaBarang(), FormatUtil.rupiah(b.getHargaSatuan()), b.getStok() });
    }

    private void filterTabel() {
        String kata = txtCari.getText().trim().toLowerCase();
        if (kata.isEmpty()) { refreshTabel(dataBarang); return; }
        refreshTabel(dataBarang.stream()
            .filter(b -> b.getNamaBarang().toLowerCase().contains(kata))
            .toList());
    }

    private void prosesTabah() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) { tampilStatus("Nama barang wajib diisi!", true); return; }
        double harga; int stok;
        try {
            harga = Double.parseDouble(txtHarga.getText().trim().replace(".", "").replace(",", "."));
            stok  = Integer.parseInt(txtStok.getText().trim());
        } catch (NumberFormatException ex) { tampilStatus("Harga dan stok harus berupa angka!", true); return; }
        if (barangDAO.insert(new Barang(0, nama, harga, stok))) {
            tampilStatus("Barang berhasil ditambahkan.", false);
            bersihkanForm(); muatDataBarang();
        } else tampilStatus("Gagal menambahkan barang (nama mungkin sudah ada).", true);
    }

    private void prosesUpdate() {
        if (selectedBarang == null) return;
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) { tampilStatus("Nama barang wajib diisi!", true); return; }
        double harga; int stok;
        try {
            harga = Double.parseDouble(txtHarga.getText().trim().replace(".", "").replace(",", "."));
            stok  = Integer.parseInt(txtStok.getText().trim());
        } catch (NumberFormatException ex) { tampilStatus("Harga dan stok harus berupa angka!", true); return; }
        selectedBarang.setNamaBarang(nama);
        selectedBarang.setHargaSatuan(harga);
        selectedBarang.setStok(stok);
        if (barangDAO.update(selectedBarang)) {
            tampilStatus("Barang berhasil diperbarui.", false);
            bersihkanForm(); muatDataBarang();
        } else tampilStatus("Gagal memperbarui barang.", true);
    }

    private void prosesHapus() {
        if (selectedBarang == null) return;
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin hapus barang: " + selectedBarang.getNamaBarang() + "?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (barangDAO.delete(selectedBarang.getIdBarang())) {
                tampilStatus("Barang berhasil dihapus.", false);
                bersihkanForm(); muatDataBarang();
            } else tampilStatus("Gagal hapus (barang mungkin ada di transaksi).", true);
        }
    }

    private void bersihkanForm() {
        txtNama.setText(""); txtHarga.setText(""); txtStok.setText("");
        selectedBarang = null;
        btnUpdate.setEnabled(false); btnHapus.setEnabled(false); btnTambah.setEnabled(true);
        tblBarang.clearSelection();
        lblStatus.setText(" ");
    }

    private void isiFormDariTabel() {
        int row = tblBarang.getSelectedRow();
        if (row < 0) return;
        String nama = (String) tabelModel.getValueAt(row, 1);
        selectedBarang = dataBarang.stream()
            .filter(b -> b.getNamaBarang().equals(nama)).findFirst().orElse(null);
        if (selectedBarang != null) {
            txtNama.setText(selectedBarang.getNamaBarang());
            txtHarga.setText(String.valueOf((int) selectedBarang.getHargaSatuan()));
            txtStok.setText(String.valueOf(selectedBarang.getStok()));
            btnUpdate.setEnabled(true); btnHapus.setEnabled(true); btnTambah.setEnabled(false);
        }
    }

    private void logout() { dispose(); new LoginFrame().setVisible(true); }

    private void tampilStatus(String pesan, boolean error) {
        lblStatus.setText(pesan);
        lblStatus.setForeground(error ? new Color(252, 165, 165) : new Color(110, 231, 183));
    }

    // ===================================================================
    // STYLING
    // ===================================================================
    private void styleTable() {
        tblBarang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblBarang.setRowHeight(34);
        tblBarang.setGridColor(new Color(226, 232, 240));
        tblBarang.setFillsViewportHeight(true);
        tblBarang.setShowGrid(true);
        tblBarang.setIntercellSpacing(new Dimension(0, 0));
        tblBarang.setSelectionBackground(CLR_ROW_SEL);
        tblBarang.setSelectionForeground(CLR_TXT_MAIN);

        tblBarang.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, isSel, hasFocus, r, c);
                lbl.setOpaque(true);
                lbl.setBackground(new Color(30, 41, 59));
                lbl.setForeground(new Color(199, 210, 254));
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, CLR_ACCENT),
                    BorderFactory.createEmptyBorder(9, 12, 9, 12)));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });

        // Zebra striping
        DefaultTableCellRenderer zebraRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, isSel, hasFocus, r, c);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (isSel) {
                    lbl.setBackground(CLR_ROW_SEL);
                    lbl.setForeground(new Color(55, 48, 163));
                } else {
                    lbl.setBackground(r % 2 == 0 ? CLR_ROW_EVEN : CLR_ROW_ODD);
                    lbl.setForeground(CLR_TXT_MAIN);
                }
                return lbl;
            }
        };
        for (int i = 0; i < tblBarang.getColumnCount(); i++)
            tblBarang.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);

        tblBarang.getColumnModel().getColumn(0).setMaxWidth(45); // nomor urut

        // Kolom Stok: centred + merah jika stok < 10
        tblBarang.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, isSel, hasFocus, r, c);
                lbl.setHorizontalAlignment(CENTER);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                int stok = (int) v;
                if (isSel) {
                    lbl.setBackground(CLR_ROW_SEL);
                    lbl.setForeground(stok < 10 ? CLR_RED : new Color(55, 48, 163));
                } else {
                    lbl.setBackground(r % 2 == 0 ? CLR_ROW_EVEN : CLR_ROW_ODD);
                    lbl.setForeground(stok < 10 ? CLR_RED : CLR_TXT_MAIN);
                }
                if (stok < 10) lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                return lbl;
            }
        });
    }

    private JLabel buatSeksiLabel(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(165, 180, 252)); // Indigo-300 untuk kontras lebih baik di sidebar gelap
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField buatTextField() {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(new Color(51, 65, 85));
        tf.setForeground(new Color(226, 232, 240));
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105)),
            new EmptyBorder(8, 12, 8, 12)));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CLR_ACCENT, 2),
                    new EmptyBorder(7, 11, 7, 11)));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(71, 85, 105)),
                    new EmptyBorder(8, 12, 8, 12)));
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

    // ===================================================================
    // CUSTOM COMPONENTS (ditto KasirFrame)
    // ===================================================================
    static class RoundedPanel extends JPanel {
        private final int radius; private Color bgColor;
        RoundedPanel(int r, Color bg) { this.radius = r; this.bgColor = bg; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.setColor(new Color(203, 213, 225, 120));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.dispose(); super.paintComponent(g);
        }
    }

    static class GradientPanel extends JPanel {
        private final Color c1, c2; private final boolean horiz;
        GradientPanel(Color c1, Color c2, boolean horiz) { this.c1=c1; this.c2=c2; this.horiz=horiz; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(horiz ? new GradientPaint(0, 0, c1, getWidth(), 0, c2)
                              : new GradientPaint(0, 0, c1, 0, getHeight(), c2));
            g2.fillRect(0, 0, getWidth(), getHeight()); g2.dispose(); super.paintComponent(g);
        }
    }

    static class RoundedButton extends JButton {
        private final Color base; private final int radius;
        RoundedButton(String text, Color color, int r) {
            super(text); this.base=color; this.radius=r;
            setOpaque(false); setContentAreaFilled(false); setBorderPainted(false);
            setFocusPainted(false); setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); setRolloverEnabled(true);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            Color bg, fg;
            if (!isEnabled()) { bg = new Color(51,65,85); fg = new Color(100,116,139); }
            else if (getModel().isPressed()) { bg = base.darker().darker(); fg = Color.WHITE; }
            else if (getModel().isRollover()) {
                float[] hsb = Color.RGBtoHSB(base.getRed(), base.getGreen(), base.getBlue(), null);
                bg = Color.getHSBColor(hsb[0], Math.max(0f, hsb[1]-0.08f), Math.min(1f, hsb[2]+0.12f));
                fg = Color.WHITE;
            } else { bg = base; fg = Color.WHITE; }
            g2.setColor(new Color(0,0,0,35));
            g2.fillRoundRect(2, 3, getWidth()-4, getHeight()-2, radius, radius);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-2, radius, radius);
            g2.setColor(fg); g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 1;
            g2.drawString(getText(), x, y); g2.dispose();
        }
    }
}
