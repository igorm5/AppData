package notaumkm.ui;

import notaumkm.db.AdminDAO;
import notaumkm.model.Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class LoginFrame extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JLabel         lblStatus;

    private final AdminDAO adminDAO = new AdminDAO();

    // === PALET WARNA ===
    private static final Color CLR_BG_TOP    = new Color(17, 24, 39);    // Gray-900
    private static final Color CLR_BG_BOT    = new Color(30, 58, 138);   // Blue-900
    private static final Color CLR_CARD      = new Color(255, 255, 255);
    private static final Color CLR_ACCENT    = new Color(99, 102, 241);  // Indigo-500
    private static final Color CLR_GREEN     = new Color(16, 185, 129);  // Emerald-500
    private static final Color CLR_ERROR     = new Color(239, 68, 68);   // Red-500
    private static final Color CLR_WHITE     = Color.WHITE;
    private static final Color CLR_TXT       = new Color(15, 23, 42);    // Slate-900
    private static final Color CLR_MUTED     = new Color(100, 116, 139); // Slate-500
    private static final Color CLR_BORDER    = new Color(226, 232, 240); // Slate-200

    public LoginFrame() { initUI(); }

    private void initUI() {
        setTitle("Login — SISTEM KASIR");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel utama dengan gradient
        GradientPanel mainPanel = new GradientPanel(CLR_BG_TOP, CLR_BG_BOT);
        mainPanel.setLayout(new GridBagLayout());

        // === KARTU LOGIN ===
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Drop shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(4, 6, getWidth() - 4, getHeight() - 4, 24, 24);
                // Card body
                g2.setColor(CLR_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 40, 36, 40));
        card.setPreferredSize(new Dimension(360, 440));

        // --- Logo / Icon area ---
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        JLabel lblIcon = new JLabel("POS") {
            @Override public Dimension getPreferredSize() { return new Dimension(72, 72); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Circle background
                g2.setColor(new Color(238, 242, 255)); // Indigo-50
                g2.fillOval(0, 0, 68, 68);
                g2.setColor(CLR_ACCENT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(1, 1, 66, 66);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblIcon.setForeground(CLR_ACCENT);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setVerticalAlignment(SwingConstants.CENTER);
        logoPanel.add(lblIcon);
        card.add(logoPanel);
        card.add(Box.createVerticalStrut(12));

        // --- Judul ---
        JLabel lblTitle = new JLabel("SISTEM KASIR", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(CLR_TXT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);

        JLabel lblSub = new JLabel("Point of Sale & Manajemen Database Stock", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(CLR_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSub);
        card.add(Box.createVerticalStrut(28));

        // --- Divider ---
        card.add(buatDivider());
        card.add(Box.createVerticalStrut(22));

        // --- Username ---
        card.add(buatLabelForm("Username"));
        card.add(Box.createVerticalStrut(6));
        txtUsername = buatTextField("Masukkan username...", false);
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(16));

        // --- Password ---
        card.add(buatLabelForm("Password"));
        card.add(Box.createVerticalStrut(6));
        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(24));

        // --- Tombol Login ---
        btnLogin = new JButton("MASUK") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

                Color bg;
                if (!isEnabled()) bg = new Color(203, 213, 225);
                else if (getModel().isPressed()) bg = CLR_ACCENT.darker();
                else if (getModel().isRollover()) bg = new Color(79, 70, 229); // Indigo-600
                else bg = CLR_ACCENT;

                // Drop shadow
                g2.setColor(new Color(99, 102, 241, 60));
                g2.fillRoundRect(2, 4, getWidth() - 2, getHeight() - 2, 12, 12);

                // Background
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 12, 12);

                // Text
                g2.setColor(isEnabled() ? CLR_WHITE : CLR_MUTED);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnLogin.setOpaque(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setRolloverEnabled(true);
        btnLogin.addActionListener(e -> prosesLogin());
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(14));

        // --- Status label ---
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setForeground(CLR_ERROR);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblStatus);

        // --- Footer ---
        card.add(Box.createVerticalGlue());
        JLabel lblFooter = new JLabel("© 2026 Sistem Nota UMKM", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFooter.setForeground(new Color(203, 213, 225));
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblFooter);

        mainPanel.add(card);
        add(mainPanel);

        // --- Enter key ---
        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) prosesLogin();
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setForeground(CLR_ERROR);
            lblStatus.setText("Username dan password wajib diisi!");
            return;
        }
        btnLogin.setEnabled(false);
        lblStatus.setForeground(CLR_MUTED);
        lblStatus.setText("Memverifikasi...");

        SwingWorker<Admin, Void> worker = new SwingWorker<>() {
            @Override protected Admin doInBackground() throws Exception { return adminDAO.login(username, password); }
            @Override protected void done() {
                try {
                    Admin admin = get();
                    if (admin != null) {
                        lblStatus.setForeground(CLR_GREEN);
                        lblStatus.setText("Login berhasil! Memuat aplikasi...");
                        Timer timer = new Timer(600, evt -> {
                            dispose();
                            if (admin.isKasir()) new KasirFrame(admin).setVisible(true);
                            else                 new PengelolaStokFrame(admin).setVisible(true);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        lblStatus.setForeground(CLR_ERROR);
                        lblStatus.setText("Username atau password salah!");
                        txtPassword.setText("");
                        btnLogin.setEnabled(true);
                    }
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    lblStatus.setForeground(CLR_ERROR);
                    lblStatus.setText(cause instanceof SQLException
                        ? "Error koneksi DB: " + cause.getMessage()
                        : "Error internal: " + cause.getMessage());
                    btnLogin.setEnabled(true);
                } catch (InterruptedException ex) {
                    lblStatus.setForeground(CLR_ERROR);
                    lblStatus.setText("Proses login terinterupsi.");
                    btnLogin.setEnabled(true);
                    Thread.currentThread().interrupt();
                }
            }
        };
        worker.execute();
    }

    // ===================================================================
    // HELPERS
    // ===================================================================
    private JLabel buatLabelForm(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(55, 65, 81)); // Gray-700
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        return lbl;
    }

    private JTextField buatTextField(String placeholder, boolean isPassword) {
        JTextField tf = new JTextField();
        styleTextField(tf);
        tf.setForeground(CLR_MUTED);
        tf.setText(placeholder);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText(""); tf.setForeground(CLR_TXT);
                }
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CLR_ACCENT, 2),
                    new EmptyBorder(7, 11, 7, 11)));
            }
            @Override public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setForeground(CLR_MUTED); tf.setText(placeholder);
                }
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CLR_BORDER),
                    new EmptyBorder(8, 12, 8, 12)));
            }
        });
        return tf;
    }

    private void styleTextField(JTextField tf) {
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.setPreferredSize(new Dimension(300, 42));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(CLR_TXT);
        tf.setBackground(new Color(249, 250, 251)); // Gray-50
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CLR_BORDER),
            new EmptyBorder(8, 12, 8, 12)
        ));
        tf.setAlignmentX(Component.CENTER_ALIGNMENT);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CLR_ACCENT, 2),
                    new EmptyBorder(7, 11, 7, 11)));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CLR_BORDER),
                    new EmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    private JPanel buatDivider() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
        JSeparator left = new JSeparator(); left.setForeground(CLR_BORDER);
        JSeparator right = new JSeparator(); right.setForeground(CLR_BORDER);
        JLabel mid = new JLabel("·", SwingConstants.CENTER);
        mid.setForeground(CLR_MUTED);
        mid.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(left, BorderLayout.WEST);
        p.add(mid, BorderLayout.CENTER);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ===================================================================
    // CUSTOM COMPONENTS
    // ===================================================================
    static class GradientPanel extends JPanel {
        private final Color c1, c2;
        GradientPanel(Color c1, Color c2) { this.c1 = c1; this.c2 = c2; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
