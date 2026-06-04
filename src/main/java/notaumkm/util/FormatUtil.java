package notaumkm.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatUtil {

    private static final DecimalFormat RUPIAH;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        RUPIAH = new DecimalFormat("#,###", symbols);
    }

    public static String rupiah(double angka) {
        return "Rp " + RUPIAH.format(angka);
    }

    public static String angka(double nilai) {
        return RUPIAH.format(nilai);
    }

    public static String tanggalWaktu(LocalDateTime ldt) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return ldt.format(fmt);
    }

    public static String sekarang() {
        return tanggalWaktu(LocalDateTime.now());
    }
}
