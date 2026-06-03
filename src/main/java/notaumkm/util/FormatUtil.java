package notaumkm.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * FormatUtil
 * Utilitas pemformatan angka dan tanggal untuk tampilan di UI.
 */
public class FormatUtil {

    // Format mata uang Rupiah: Rp 32.500
    private static final DecimalFormat RUPIAH;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        RUPIAH = new DecimalFormat("#,###", symbols);
    }

    /** Format angka sebagai mata uang Rupiah. Contoh: 32500 → "Rp 32.500" */
    public static String rupiah(double angka) {
        return "Rp " + RUPIAH.format(angka);
    }

    /** Format angka tanpa prefix Rp. Contoh: 32500 → "32.500" */
    public static String angka(double nilai) {
        return RUPIAH.format(nilai);
    }

    /** Format tanggal-waktu ke format lokal Indonesia */
    public static String tanggalWaktu(LocalDateTime ldt) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return ldt.format(fmt);
    }

    /** Menghasilkan string tanggal-waktu sekarang */
    public static String sekarang() {
        return tanggalWaktu(LocalDateTime.now());
    }
}
