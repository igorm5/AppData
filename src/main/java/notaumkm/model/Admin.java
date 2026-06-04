package notaumkm.model;

public class Admin {

    private int    idAdmin;
    private String username;
    private String password;
    private String role;

    public Admin() {}

    public Admin(int idAdmin, String username, String role) {
        this.idAdmin  = idAdmin;
        this.username = username;
        this.role     = role;
    }


    public int    getIdAdmin(){ 
        return idAdmin; 
    }
    public void   setIdAdmin(int id){ 
        this.idAdmin = id; 
    }

    public String getUsername(){ 
        return username; 
    }
    public void   setUsername(String u){ 
        this.username = u; 
    }

    public String getPassword(){ 
        return password; 
    }
    public void   setPassword(String p){ 
        this.password = p; 
    }

    public String getRole(){ 
        return role; 
    }
    public void   setRole(String r){ 
        this.role = r; 
    }

    public boolean isKasir(){ 
        return "kasir".equalsIgnoreCase(role); 
    }

    public boolean isPengelola(){ 
        return "pengelola_stok".equalsIgnoreCase(role); 
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
