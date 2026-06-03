package notaumkm.model;

/**
 * Model Barang
 * Merepresentasikan data produk/barang yang dijual.
 */
public class Barang {

    private int    idBarang;
    private String namaBarang;
    private double hargaSatuan;
    private int    stok;

    // ── Konstruktor ─────────────────────────────────────────────────────────

    public Barang() {}

    public Barang(int idBarang, String namaBarang, double hargaSatuan, int stok) {
        this.idBarang    = idBarang;
        this.namaBarang  = namaBarang;
        this.hargaSatuan = hargaSatuan;
        this.stok        = stok;
    }

    // ── Getter & Setter ──────────────────────────────────────────────────────

    public int    getIdBarang()               { return idBarang; }
    public void   setIdBarang(int id)         { this.idBarang = id; }

    public String getNamaBarang()             { return namaBarang; }
    public void   setNamaBarang(String n)     { this.namaBarang = n; }

    public double getHargaSatuan()            { return hargaSatuan; }
    public void   setHargaSatuan(double h)    { this.hargaSatuan = h; }

    public int    getStok()                   { return stok; }
    public void   setStok(int s)              { this.stok = s; }

    /** Kembalikan true jika stok tersedia */
    public boolean tersedia()                 { return stok > 0; }

    @Override
    public String toString() {
        return namaBarang; // Digunakan ComboBox / JList agar tampil nama barang
    }
}
