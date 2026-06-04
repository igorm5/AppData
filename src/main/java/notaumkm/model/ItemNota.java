package notaumkm.model;

public class ItemNota {

    private String namaBarang;
    private double hargaSatuan;
    private int    qty;
    private double subtotal;

    public ItemNota() {}

    public ItemNota(String namaBarang, double hargaSatuan, int qty) {
        this.namaBarang  = namaBarang;
        this.hargaSatuan = hargaSatuan;
        this.qty         = qty;
        hitungSubtotal();
    }

    public void hitungSubtotal() {
        this.subtotal = this.hargaSatuan * this.qty;
    }

    public String getNamaBarang()           { return namaBarang; }
    public void   setNamaBarang(String n)   { this.namaBarang = n; }

    public double getHargaSatuan()          { return hargaSatuan; }
    public void   setHargaSatuan(double h)  { this.hargaSatuan = h; hitungSubtotal(); }

    public int    getQty()                  { return qty; }
    public void   setQty(int q)             { this.qty = q; hitungSubtotal(); }

    public double getSubtotal()             { return subtotal; }

    @Override
    public String toString() {
        return namaBarang + " x" + qty + " = Rp " + subtotal;
    }
}
