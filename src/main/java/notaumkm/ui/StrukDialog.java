package notaumkm.ui;

import notaumkm.model.ItemNota;
import notaumkm.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.*;
import java.time.LocalDateTime;
import java.util.List;

public class StrukDialog extends JDialog {

    private final String         noNota;
    private final String         namaCustomer;
    private final String         kasir;
    private final List<ItemNota> items;
    private final double         total;
    private final LocalDateTime  waktu = LocalDateTime.now();

    private static final Color CLR_GREEN_DARK = new Color(5,  150, 105);  // Emerald-600
    private static final Color CLR_GREEN_LT   = new Color(209, 250, 229); // Emerald-100
    private static final Color CLR_ACCENT     = new Color(99, 102, 241);  // Indigo-500
    private static final Color CLR_WHITE      = Color.WHITE;
    private static final Color CLR_TXT        = new Color(15, 23, 42);    // Slate-900
    private static final Color CLR_MUTED      = new Color(100, 116, 139); // Slate-500
    private static final Color CLR_BORDER     = new Color(209, 250, 229); // Emerald-100
    private static final Color CLR_BG         = new Color(249, 250, 251); // Gray-50
    private static final int   LEBAR          = 40;

    public StrukDialog(Frame parent, String noNota, String namaCustomer,
                       String kasir, List<ItemNota> items, double total) {
        super(parent, "Struk Belanja — " + noNota, true);
        this.noNota       = noNota;
        this.namaCustomer = namaCustomer;
        this.kasir        = kasir;
        this.items        = items;
        this.total        = total;
        initUI();
    }

    private void initUI() {
        setSize(430, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(CLR_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(CLR_BG);

        // ---- HEADER SUKSES (gradient) ----
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(5, 150, 105),
                                                     getWidth(), 0, new Color(16, 185, 129));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblHeaderTxt = new JLabel("Transaksi Berhasil!", SwingConstants.CENTER);
        lblHeaderTxt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeaderTxt.setForeground(CLR_WHITE);

        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        headerRow.setOpaque(false);
        headerRow.add(lblHeaderTxt);
        headerPanel.add(headerRow, BorderLayout.CENTER);

        // ---- BODY STRUK ----
        JTextArea txtStruk = new JTextArea(buatTeksStruk());
        txtStruk.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtStruk.setEditable(false);
        txtStruk.setBackground(CLR_WHITE);
        txtStruk.setForeground(CLR_TXT);
        txtStruk.setBorder(new EmptyBorder(18, 22, 18, 22));
        txtStruk.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(txtStruk);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 12, 10, 12),
            BorderFactory.createLineBorder(new Color(203, 213, 225))
        ));
        scroll.setBackground(CLR_WHITE);
        scroll.getViewport().setBackground(CLR_WHITE);

        // ---- FOOTER TOMBOL ----
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 14));
        btnPanel.setBackground(CLR_BG);
        btnPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
            new EmptyBorder(4, 0, 4, 0)
        ));

        JButton btnCetak = buatTombol("Cetak Struk", CLR_ACCENT);
        JButton btnTutup = buatTombol("Tutup", new Color(71, 85, 105));

        btnCetak.addActionListener(e -> cetakStruk(txtStruk));
        btnTutup.addActionListener(e -> dispose());

        btnPanel.add(btnCetak);
        btnPanel.add(btnTutup);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scroll,      BorderLayout.CENTER);
        mainPanel.add(btnPanel,    BorderLayout.SOUTH);

        add(mainPanel);
    }

    private String buatTeksStruk() {
        StringBuilder sb = new StringBuilder();
        sb.append(tengah("================================")).append("\n");
        sb.append(tengah("      TOKO SERBA ADA UMKM")).append("\n");
        sb.append(tengah("   Jl. Contoh No.1, Kota ABC")).append("\n");
        sb.append(tengah("================================")).append("\n\n");
        sb.append(String.format("%-15s: %s%n", "No. Nota",  noNota));
        sb.append(String.format("%-15s: %s%n", "Tanggal",   FormatUtil.tanggalWaktu(waktu)));
        sb.append(String.format("%-15s: %s%n", "Customer",  namaCustomer));
        sb.append(String.format("%-15s: %s%n", "Kasir",     kasir));
        sb.append("\n----------------------------------------\n");
        sb.append(String.format("%-22s %4s %12s%n", "Barang", "Qty", "Subtotal"));
        sb.append("----------------------------------------\n");
        for (ItemNota item : items) {
            String nama = item.getNamaBarang();
            if (nama.length() > 22) nama = nama.substring(0, 19) + "...";
            sb.append(String.format("%-22s %4d %12s%n", nama, item.getQty(),
                FormatUtil.rupiah(item.getSubtotal())));
            sb.append(String.format("  @ %s%n", FormatUtil.rupiah(item.getHargaSatuan())));
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-22s %17s%n", "TOTAL BAYAR", FormatUtil.rupiah(total)));
        sb.append("========================================\n");
        sb.append(tengah("Terima kasih atas kunjungan Anda!")).append("\n");
        sb.append(tengah("Barang yang sudah dibeli")).append("\n");
        sb.append(tengah("tidak dapat dikembalikan.")).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    private String tengah(String teks) {
        if (teks.length() >= LEBAR) return teks;
        int pad = (LEBAR - teks.length()) / 2;
        return " ".repeat(pad) + teks;
    }

    private void cetakStruk(JTextArea txtArea) {
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf  = job.defaultPage();
        Paper paper    = pf.getPaper();
        paper.setImageableArea(10, 10, pf.getWidth() - 20, pf.getHeight() - 20);
        pf.setPaper(paper);
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.setFont(new Font("Courier New", Font.PLAIN, 10));
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            int lineHeight = fm.getHeight();
            int y = lineHeight;
            for (String line : txtArea.getText().split("\n")) {
                g2d.drawString(line, 0, y);
                y += lineHeight;
                if (y > pageFormat.getImageableHeight()) break;
            }
            return Printable.PAGE_EXISTS;
        }, pf);
        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Struk berhasil dicetak!", "Cetak",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Gagal mencetak: " + ex.getMessage(),
                        "Error Cetak", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton buatTombol(String teks, Color warna) {
        JButton btn = new JButton(teks) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                Color bg;
                if (getModel().isPressed())        bg = warna.darker();
                else if (getModel().isRollover())  {
                    float[] hsb = Color.RGBtoHSB(warna.getRed(), warna.getGreen(), warna.getBlue(), null);
                    bg = Color.getHSBColor(hsb[0], Math.max(0f, hsb[1]-0.08f), Math.min(1f, hsb[2]+0.12f));
                }
                else bg = warna;
                // shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 3, getWidth()-4, getHeight()-2, 10, 10);
                // bg
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-2, 10, 10);
                // text
                g2.setColor(CLR_WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 1;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(160, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setRolloverEnabled(true);
        return btn;
    }
}
