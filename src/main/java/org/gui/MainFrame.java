package org.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    // ── Warna Tema ──────────────────────────────────────────────────────────
    public static final Color C_PRIMARY   = new Color(0x1A, 0x73, 0xE8);
    public static final Color C_PRIMARY_D = new Color(0x12, 0x57, 0xC1);
    public static final Color C_SIDEBAR   = new Color(0x1E, 0x1E, 0x2D);
    public static final Color C_SIDEBAR_H = new Color(0x2A, 0x2A, 0x3F);
    public static final Color C_SIDEBAR_A = new Color(0x1A, 0x73, 0xE8);
    public static final Color C_BG        = new Color(0xF4, 0xF6, 0xFA);
    public static final Color C_WHITE     = Color.WHITE;
    public static final Color C_TEXT_W    = Color.WHITE;
    public static final Color C_TEXT_G    = new Color(0x5F, 0x6B, 0x7C);
    public static final Color C_DANGER    = new Color(0xE5, 0x39, 0x35);
    public static final Color C_SUCCESS   = new Color(0x2E, 0x7D, 0x32);
    public static final Color C_WARN      = new Color(0xF5, 0x7C, 0x00);

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel lblStatus;
    private JButton activeNavBtn = null;

    private PanelDashboard   panelDashboard;
    private PanelCustomer    panelCustomer;
    private PanelBarang      panelBarang;
    private PanelTransaksi   panelTransaksi;
    private PanelNota        panelNota;

    public MainFrame() {
        setTitle("Sistem Nota UMKM – Toko Sembako & Frozen Food");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        setSize(1200, 730);
        setLocationRelativeTo(null);

        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // ── Sidebar ─────────────────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setBackground(C_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(210, 0));

        // Logo / header sidebar
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(C_SIDEBAR);
        logoPanel.setMaximumSize(new Dimension(210, 80));
        logoPanel.setPreferredSize(new Dimension(210, 80));
        JLabel lblLogo = new JLabel("🏪  Nota UMKM", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setForeground(C_TEXT_W);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        JLabel lblSub = new JLabel("Toko Sembako & Frozen Food", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblSub.setForeground(new Color(0xAA, 0xBB, 0xCC));
        JPanel logoInner = new JPanel(new GridLayout(2, 1));
        logoInner.setBackground(C_SIDEBAR);
        logoInner.add(lblLogo);
        logoInner.add(lblSub);
        logoPanel.add(logoInner, BorderLayout.CENTER);

        JSeparator sep0 = new JSeparator();
        sep0.setForeground(new Color(0x33, 0x33, 0x50));
        sep0.setMaximumSize(new Dimension(210, 1));

        sidebar.add(logoPanel);
        sidebar.add(sep0);
        sidebar.add(Box.createVerticalStrut(8));

        // Nav buttons
        JButton[] navBtns = {
            makeNavBtn("🏠  Dashboard",    "DASHBOARD"),
            makeNavBtn("👤  Customer",     "CUSTOMER"),
            makeNavBtn("📦  Barang",       "BARANG"),
            makeNavBtn("🧾  Transaksi",    "TRANSAKSI"),
            makeNavBtn("📋  Detail Nota",  "NOTA"),
        };

        for (JButton b : navBtns) sidebar.add(b);
        sidebar.add(Box.createVerticalGlue());

        // Tombol Koneksi DB di bawah sidebar
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(0x33, 0x33, 0x50));
        sep1.setMaximumSize(new Dimension(210, 1));
        sidebar.add(sep1);

        JButton btnKoneksi = makeSideBtn("🔌  Koneksi Database", C_PRIMARY);
        btnKoneksi.addActionListener(e -> doConnect());
        btnKoneksi.setMaximumSize(new Dimension(210, 42));
        sidebar.add(btnKoneksi);
        sidebar.add(Box.createVerticalStrut(10));

        // ── Top Bar ─────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(C_WHITE);
        topBar.setPreferredSize(new Dimension(0, 48));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0, 0xE0, 0xE0)));

        JLabel lblTitle = new JLabel("  Sistem Manajemen Nota UMKM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(0x20, 0x20, 0x30));
        topBar.add(lblTitle, BorderLayout.WEST);

        lblStatus = new JLabel("● Belum Terhubung   ", SwingConstants.RIGHT);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(C_DANGER);
        topBar.add(lblStatus, BorderLayout.EAST);

        // ── Content (CardLayout) ────────────────────────────────────────────
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(C_BG);

        panelDashboard = new PanelDashboard();
        panelCustomer  = new PanelCustomer();
        panelBarang    = new PanelBarang();
        panelTransaksi = new PanelTransaksi();
        panelNota      = new PanelNota();

        contentPanel.add(panelDashboard, "DASHBOARD");
        contentPanel.add(panelCustomer,  "CUSTOMER");
        contentPanel.add(panelBarang,    "BARANG");
        contentPanel.add(panelTransaksi, "TRANSAKSI");
        contentPanel.add(panelNota,      "NOTA");

        // Wrap content in a panel with top bar
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(topBar,        BorderLayout.NORTH);
        rightPanel.add(contentPanel,  BorderLayout.CENTER);

        add(sidebar,    BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Aktifkan dashboard pertama
        setActive(navBtns[0], "DASHBOARD");
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private JButton makeNavBtn(String text, String card) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(0xCC, 0xDD, 0xEE));
        btn.setBackground(C_SIDEBAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(210, 42));
        btn.setPreferredSize(new Dimension(210, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (activeNavBtn != btn) btn.setBackground(C_SIDEBAR_H);
            }
            public void mouseExited(MouseEvent e) {
                if (activeNavBtn != btn) btn.setBackground(C_SIDEBAR);
            }
        });
        btn.addActionListener(e -> setActive(btn, card));
        return btn;
    }

    private JButton makeSideBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setActive(JButton btn, String card) {
        if (activeNavBtn != null) {
            activeNavBtn.setBackground(C_SIDEBAR);
            activeNavBtn.setForeground(new Color(0xCC, 0xDD, 0xEE));
            activeNavBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        activeNavBtn = btn;
        btn.setBackground(C_SIDEBAR_A);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cardLayout.show(contentPanel, card);
    }

    private void doConnect() {
        boolean ok = DBConnection.connect();
        if (ok) {
            DBConnection.initTables();
            lblStatus.setText("● Terhubung ke MySQL   ");
            lblStatus.setForeground(new Color(0x1B, 0x87, 0x35));
            JOptionPane.showMessageDialog(this,
                "Berhasil terhubung ke database 'nota_umkm'!\nSemua tabel siap digunakan.",
                "Koneksi Berhasil", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ── Static factory untuk panel card (digunakan inner panels) ────────────
    public static JPanel makeCard(String judul, String ikon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MainFrame.C_BG);
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel lblJudul = new JLabel(ikon + "  " + judul);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblJudul.setForeground(new Color(0x1A, 0x1A, 0x2E));
        lblJudul.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        card.add(lblJudul, BorderLayout.NORTH);

        return card;
    }

    public static JPanel makeWhiteBox() {
        JPanel box = new JPanel();
        box.setBackground(C_WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0, 0xE5, 0xEF), 1, true),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return box;
    }

    public static JButton makePrimaryBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(C_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
        return btn;
    }

    public static JButton makeDangerBtn(String text) {
        JButton btn = makePrimaryBtn(text);
        btn.setBackground(C_DANGER);
        return btn;
    }

    public static JButton makeSuccessBtn(String text) {
        JButton btn = makePrimaryBtn(text);
        btn.setBackground(C_SUCCESS);
        return btn;
    }

    public static JButton makeWarnBtn(String text) {
        JButton btn = makePrimaryBtn(text);
        btn.setBackground(C_WARN);
        return btn;
    }

    public static JLabel makeFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(0x37, 0x47, 0x5A));
        return lbl;
    }

    public static JTextField makeTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xCC, 0xD5, 0xE0), 1, true),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        tf.setPreferredSize(new Dimension(220, 30));
        return tf;
    }

    public static void styleTable(javax.swing.JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(0xE8, 0xEE, 0xF9));
        table.getTableHeader().setForeground(new Color(0x1A, 0x1A, 0x2E));
        table.setSelectionBackground(new Color(0xBB, 0xD4, 0xFB));
        table.setSelectionForeground(new Color(0x10, 0x20, 0x40));
        table.setGridColor(new Color(0xE8, 0xEE, 0xF9));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
    }
}
