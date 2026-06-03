package com.notaumkm.ui;

import com.notaumkm.model.ItemNota;
import com.notaumkm.util.FormatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * StrukDialog
 * Dialog modal yang menampilkan struk/nota belanja setelah transaksi berhasil.
 * Mendukung tampilan preview dan cetak via Java PrinterJob.
 */
public class StrukDialog extends JDialog {

    private final String          noNota;
    private final String          namaCustomer;
    private final String          kasir;
    private final List<ItemNota>  items;
    private final double          total;
    private final LocalDateTime   waktu = LocalDateTime.now();

    // ── Warna tema struk ─────────────────────────────────────────────────────
    private static final Color CLR_DARK  = new Color(27, 44, 56);
    private static final Color CLR_GREEN = new Color(52, 120, 77);
    private static final Color CLR_WHITE = Color.WHITE;
    private static final Color CLR_LINE  = new Color(200, 210, 205);

    // Lebar struk dalam karakter (font monospaced)
    private static final int LEBAR = 40;

    // ── Konstruktor ───────────────────────────────────────────────────────────

    public StrukDialog(Frame parent, String noNota, String namaCustomer,
                       String kasir, List<ItemNota> items, double total) {
        super(parent, "Struk Belanja — " + noNota, true); // Modal dialog
        this.noNota       = noNota;
        this.namaCustomer = namaCustomer;
        this.kasir        = kasir;
        this.items        = items;
        this.total        = total;
        initUI();
    }

    // ── Inisialisasi UI ───────────────────────────────────────────────────────

    private void initUI() {
        setSize(420, 580);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(CLR_WHITE);

        // ── Area struk ────────────────────────────────────────────────────────
        JTextArea txtStruk = new JTextArea(buatTeksStruk());
        txtStruk.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtStruk.setEditable(false);
        txtStruk.setBackground(new Color(252, 252, 250));
        txtStruk.setBorder(new EmptyBorder(16, 20, 16, 20));
        txtStruk.setForeground(new Color(30, 40, 50));

        JScrollPane scroll = new JScrollPane(txtStruk);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(12, 12, 0, 12),
            BorderFactory.createLineBorder(CLR_LINE)
        ));

        // ── Panel tombol ──────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        btnPanel.setBackground(CLR_WHITE);

        JButton btnCetak = buatTombol("🖨  Cetak Struk",  CLR_GREEN);
        JButton btnTutup = buatTombol("✕  Tutup",        new Color(90, 100, 115));

        btnCetak.addActionListener(e -> cetakStruk(txtStruk));
        btnTutup.addActionListener(e -> dispose());

        btnPanel.add(btnCetak);
        btnPanel.add(btnTutup);

        // ── Header kecil ──────────────────────────────────────────────────────
        JLabel lblHeader = new JLabel("  Transaksi Berhasil  ✓", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(CLR_WHITE);
        lblHeader.setBackground(CLR_GREEN);
        lblHeader.setOpaque(true);
        lblHeader.setBorder(new EmptyBorder(10, 0, 10, 0));

        mainPanel.add(lblHeader, BorderLayout.NORTH);
        mainPanel.add(scroll,    BorderLayout.CENTER);
        mainPanel.add(btnPanel,  BorderLayout.SOUTH);

        add(mainPanel);
    }

    // ── Generate teks struk ───────────────────────────────────────────────────

    /**
     * Membangun teks struk dalam format monospaced.
     * Format mengikuti struk thermal printer standar UMKM.
     */
    private String buatTeksStruk() {
        StringBuilder sb = new StringBuilder();

        sb.append(tengah("================================")).append("\n");
        sb.append(tengah("      TOKO SERBA ADA UMKM")).append("\n");
        sb.append(tengah("   Jl. Contoh No.1, Kota ABC")).append("\n");
        sb.append(tengah("================================")).append("\n");
        sb.append("\n");

        // Header nota
        sb.append(String.format("%-15s: %s%n",  "No. Nota",  noNota));
        sb.append(String.format("%-15s: %s%n",  "Tanggal",   FormatUtil.tanggalWaktu(waktu)));
        sb.append(String.format("%-15s: %s%n",  "Customer",  namaCustomer));
        sb.append(String.format("%-15s: %s%n",  "Kasir",     kasir));
        sb.append("\n");

        // Garis pemisah
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-22s %4s %12s%n", "Barang", "Qty", "Subtotal"));
        sb.append("----------------------------------------\n");

        // Detail item
        for (ItemNota item : items) {
            // Nama barang (potong jika terlalu panjang)
            String nama = item.getNamaBarang();
            if (nama.length() > 22) nama = nama.substring(0, 19) + "...";

            sb.append(String.format("%-22s %4d %12s%n",
                nama,
                item.getQty(),
                FormatUtil.rupiah(item.getSubtotal())
            ));

            // Harga satuan di baris kedua (sub-info)
            sb.append(String.format("  @ %s%n",
                FormatUtil.rupiah(item.getHargaSatuan())
            ));
        }

        sb.append("----------------------------------------\n");

        // Total
        sb.append(String.format("%-22s %17s%n",
            "TOTAL BAYAR",
            FormatUtil.rupiah(total)
        ));

        sb.append("========================================\n");
        sb.append(tengah("Terima kasih atas kunjungan Anda!")).append("\n");
        sb.append(tengah("Barang yang sudah dibeli")).append("\n");
        sb.append(tengah("tidak dapat dikembalikan.")).append("\n");
        sb.append("========================================\n");

        return sb.toString();
    }

    /** Membuat teks rata tengah untuk lebar struk */
    private String tengah(String teks) {
        if (teks.length() >= LEBAR) return teks;
        int pad = (LEBAR - teks.length()) / 2;
        return " ".repeat(pad) + teks;
    }

    // ── Fungsi cetak ──────────────────────────────────────────────────────────

    /**
     * Mencetak struk ke printer default menggunakan Java PrinterJob.
     * Teks dirender baris per baris ke Graphics2D.
     *
     * @param txtArea komponen teks yang berisi konten struk
     */
    private void cetakStruk(JTextArea txtArea) {
        PrinterJob job = PrinterJob.getPrinterJob();

        // Pengaturan halaman (ukuran kertas kecil / A4 default)
        PageFormat pf = job.defaultPage();
        Paper paper    = pf.getPaper();
        // Margin kecil untuk printer thermal
        paper.setImageableArea(10, 10,
            pf.getWidth() - 20, pf.getHeight() - 20);
        pf.setPaper(paper);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            Font printFont = new Font("Courier New", Font.PLAIN, 10);
            g2d.setFont(printFont);
            g2d.setColor(Color.BLACK);

            FontMetrics fm   = g2d.getFontMetrics();
            int lineHeight   = fm.getHeight();
            int y            = lineHeight;

            // Cetak tiap baris teks
            String[] baris = txtArea.getText().split("\n");
            for (String line : baris) {
                g2d.drawString(line, 0, y);
                y += lineHeight;
                // Hentikan jika melebihi area cetak
                if (y > pageFormat.getImageableHeight()) break;
            }
            return Printable.PAGE_EXISTS;
        }, pf);

        // Tampilkan dialog cetak OS
        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this,
                    "Struk berhasil dicetak!", "Cetak",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this,
                    "Gagal mencetak: " + ex.getMessage(), "Error Cetak",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Helper UI ─────────────────────────────────────────────────────────────

    private JButton buatTombol(String teks, Color warna) {
        JButton btn = new JButton(teks);
        btn.setBackground(warna);
        btn.setForeground(CLR_WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 38));
        return btn;
    }
}
