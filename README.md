# Sistem Nota UMKM – Toko Sembako & Frozen Food
## Aplikasi Java Swing dengan SQL Server

### Struktur Proyek
```
AppData/
├── pom.xml
├── run.bat
├── setup_database.sql
├── README.md
└── src/
    └── main/
        └── java/
            └── notaumkm/
                ├── Main.java
                ├── db/
                ├── model/
                ├── ui/
                └── util/
```

### Persyaratan
- JDK 21+ terpasang dan `java`/`javac` tersedia di PATH
- SQL Server dengan database `nota_umkm`
- JDBC driver Microsoft SQL Server (`mssql-jdbc-*.jar`)

### Cara Menjalankan
1. Jalankan `setup_database.sql` di SQL Server Management Studio (SSMS).
2. Buka `src/main/java/notaumkm/db/DBConnection.java` dan sesuaikan:
   - `SERVER`
   - `PORT`
   - `DATABASE`
   - `USER`
   - `PASS`
3. Buat folder `lib` di direktori proyek dan letakkan `mssql-jdbc-*.jar` di dalamnya.
4. Jalankan `run.bat` dari root proyek.

> `run.bat` akan mengompilasi semua file Java ke `build\classes` lalu menjalankan `notaumkm.Main`.

### Jika Menggunakan Maven
Jika Anda menginstal Maven, Anda bisa menjalankan:
```powershell
mvn compile
mvn exec:java -Dexec.mainClass=notaumkm.Main
```

### Akun Demo
- Kasir: `kasir1` / `kasir123`
- Pengelola: `pengelola` / `kelola123`
- Admin: `admin` / `admin123`

### Catatan
- Aplikasi menggunakan SQL Server, bukan MySQL.
- Jika driver JDBC tidak ditemukan, tambahkan file `mssql-jdbc-*.jar` ke folder `lib`.
