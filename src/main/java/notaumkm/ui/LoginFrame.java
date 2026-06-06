package notaumkm.ui;

import notaumkm.db.AdminDAO;
import notaumkm.model.Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class LoginFrame extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JLabel         lblStatus;

    private final AdminDAO adminDAO = new AdminDAO();

    private static final Color CLR_DARK     = new Color(27,  44,  56);  
    private static final Color CLR_GREEN    = new Color(52, 120,  77);    
    private static final Color CLR_BG       = new Color(245, 247, 250);  
    private static final Color CLR_WHITE    = Color.WHITE;
    private static final Color CLR_ERROR    = new Color(200, 50, 50);

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Login — SISTEM KASIR");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 500);
        setLocationRelativeTo(null); 
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CLR_BG);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CLR_DARK);
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("SISTEM KASIR", SwingConstants.CENTER);
        lblTitle.setForeground(CLR_WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel lblSub = new JLabel("Point of Sale & Manajemen Database Stock", SwingConstants.CENTER);
        lblSub.setForeground(new Color(180, 200, 190));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblSub,   BorderLayout.SOUTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CLR_WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        formPanel.add(buatLabel("Username"));
        formPanel.add(Box.createVerticalStrut(6));
        txtUsername = buatTextField("Masukkan username...");
        formPanel.add(txtUsername);
        formPanel.add(Box.createVerticalStrut(16));

        formPanel.add(buatLabel("Password"));
        formPanel.add(Box.createVerticalStrut(6));
        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        formPanel.add(txtPassword);
        formPanel.add(Box.createVerticalStrut(24));

        btnLogin = new JButton("MASUK");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setBackground(CLR_GREEN);
        btnLogin.setForeground(CLR_WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> prosesLogin());
        formPanel.add(btnLogin);
        formPanel.add(Box.createVerticalStrut(14));

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setForeground(CLR_ERROR);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(lblStatus);

        JLabel lblFooter = new JLabel("© 2026 Sistem Nota UMKM", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(150, 160, 165));
        lblFooter.setBorder(new EmptyBorder(8, 0, 8, 0));

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setBackground(CLR_BG);
        centerWrapper.add(Box.createVerticalGlue());
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerWrapper.add(formPanel);
        centerWrapper.add(Box.createVerticalGlue());

        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        mainPanel.add(lblFooter,   BorderLayout.SOUTH);

        add(mainPanel);

        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) prosesLogin();
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);

        btnLogin.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(40, 100, 60));
            }
            @Override public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(CLR_GREEN);
            }
        });
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Username dan password wajib diisi!");
            return;
        }

        btnLogin.setEnabled(false);
        lblStatus.setText("Memverifikasi...");

        SwingWorker<Admin, Void> worker = new SwingWorker<>() {
            @Override
            protected Admin doInBackground() throws Exception {
                return adminDAO.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    Admin admin = get();
                    if (admin != null) {
                        lblStatus.setForeground(new Color(50, 150, 80));
                        lblStatus.setText("Login berhasil! Memuat aplikasi...");

                        Timer timer = new Timer(600, evt -> {
                            dispose(); 
                            if (admin.isKasir()) {
                                new KasirFrame(admin).setVisible(true);
                            } else {
                                new PengelolaStokFrame(admin).setVisible(true);
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();

                    } else {
                        lblStatus.setForeground(new Color(200, 50, 50));
                        lblStatus.setText("Username atau password salah!");
                        txtPassword.setText("");
                        btnLogin.setEnabled(true);
                    }
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    lblStatus.setForeground(new Color(200, 50, 50));
                    if (cause instanceof SQLException) {
                        lblStatus.setText("Error koneksi DB: " + cause.getMessage());
                    } else {
                        lblStatus.setText("Error internal: " + cause.getMessage());
                    }
                    btnLogin.setEnabled(true);
                } catch (InterruptedException ex) {
                    lblStatus.setForeground(new Color(200, 50, 50));
                    lblStatus.setText("Proses login terinterupsi.");
                    btnLogin.setEnabled(true);
                    Thread.currentThread().interrupt();
                }
            }
        };
        worker.execute();
    }

    private JLabel buatLabel(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(60, 70, 80));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private JTextField buatTextField(String placeholder) {
        JTextField tf = new JTextField();
        styleTextField(tf);
        tf.setForeground(new Color(160, 170, 180));
        tf.setText(placeholder);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setForeground(new Color(160, 170, 180));
                    tf.setText(placeholder);
                }
            }
        });
        return tf;
    }

    private void styleTextField(JTextField tf) {
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setPreferredSize(new Dimension(300, 40));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 215), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        tf.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}
