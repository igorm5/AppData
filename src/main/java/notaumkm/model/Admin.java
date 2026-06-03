package notaumkm.model;

/**
 * Model Admin
 * Merepresentasikan data pengguna sistem (kasir / pengelola stok).
 */
public class Admin {

    private int    idAdmin;
    private String username;
    private String password;
    private String role; // "kasir" atau "pengelola_stok"

    // ── Konstruktor ─────────────────────────────────────────────────────────

    public Admin() {}

    public Admin(int idAdmin, String username, String role) {
        this.idAdmin  = idAdmin;
        this.username = username;
        this.role     = role;
    }

    // ── Getter & Setter ──────────────────────────────────────────────────────

    public int    getIdAdmin()            { return idAdmin; }
    public void   setIdAdmin(int id)      { this.idAdmin = id; }

    public String getUsername()           { return username; }
    public void   setUsername(String u)   { this.username = u; }

    public String getPassword()           { return password; }
    public void   setPassword(String p)   { this.password = p; }

    public String getRole()               { return role; }
    public void   setRole(String r)       { this.role = r; }

    /** Kembalikan true jika role adalah kasir */
    public boolean isKasir()              { return "kasir".equalsIgnoreCase(role); }

    /** Kembalikan true jika role adalah pengelola stok */
    public boolean isPengelola()          { return "pengelola_stok".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
