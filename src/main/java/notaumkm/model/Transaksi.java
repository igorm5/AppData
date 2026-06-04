package notaumkm.model;

public class Transaksi {

    private String noNota;
    private String tanggalJam;
    private String namaCustomer;
    private String kasir;
    private double total;

    public Transaksi() {}

    public Transaksi(String noNota, String tanggalJam, String namaCustomer,
                     String kasir, double total) {
        this.noNota       = noNota;
        this.tanggalJam   = tanggalJam;
        this.namaCustomer = namaCustomer;
        this.kasir        = kasir;
        this.total        = total;
    }

    public String getNoNota()       { return noNota; }
    public void   setNoNota(String noNota) { this.noNota = noNota; }

    public String getTanggalJam()   { return tanggalJam; }
    public void   setTanggalJam(String tanggalJam) { this.tanggalJam = tanggalJam; }

    public String getNamaCustomer() { return namaCustomer; }
    public void   setNamaCustomer(String namaCustomer) { this.namaCustomer = namaCustomer; }

    public String getKasir()       { return kasir; }
    public void   setKasir(String kasir) { this.kasir = kasir; }

    public double getTotal()        { return total; }
    public void   setTotal(double total) { this.total = total; }
}
