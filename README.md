# Sistem Nota UMKM – Toko Sembako & Frozen Food
## Aplikasi Java Swing CRUD berbasis Normalisasi 3NF

### Anggota Kelompok
1. Amalia Siti Khansa (255150207111072)
2. Igor Mecca Muhammed (2255150200111051)
3. Ivander Raissa Haidar (255150207111081)
4. Muhammad Hanif Alrasyid (255150200111046)

---

## Struktur Project
```
NotaUMKM/
├── src/
│   ├── MainApp.java          ← Entry point (main method)
│   ├── MainFrame.java        ← Window utama + sidebar navigasi
│   ├── DBConnection.java     ← Koneksi & inisialisasi database
│   ├── PanelDashboard.java   ← Halaman dashboard & statistik
│   ├── PanelCustomer.java    ← CRUD tabel customer
│   ├── PanelBarang.java      ← CRUD tabel barang
│   ├── PanelTransaksi.java   ← CRUD tabel transaksi (header nota)
│   └── PanelNota.java        ← CRUD tabel nota (detail item)
├── setup_database.sql        ← Script SQL untuk setup & data sample
└── README.md
```

---

## Cara Setup (NetBeans)

### 1. Siapkan Database MySQL
```sql
-- Buka MySQL Workbench / phpMyAdmin / terminal, jalankan:
source setup_database.sql
-- atau copy-paste isi file tersebut
```

### 2. Setup Project di NetBeans
1. Buka NetBeans → **File → New Project → Java → Java Application**
2. Nama project: `NotaUMKM`
3. Copy semua file `.java` dari folder `src/` ke dalam package project
4. Tambahkan **mysql-connector-java.jar** ke Libraries:
   - Klik kanan project → **Properties → Libraries → Add JAR/Folder**
   - Pilih file `mysql-connector-j-X.X.X.jar`
   - Download dari: https://dev.mysql.com/downloads/connector/j/

### 3. Konfigurasi Koneksi DB
Buka `DBConnection.java`, sesuaikan jika perlu:
```java
private static final String HOST = "localhost";
private static final String PORT = "3306";
private static final String DB   = "nota_umkm";
private static final String USER = "root";
private static final String PASS = "";   // ganti dengan password MySQL kamu
```

### 4. Jalankan
- Klik kanan `MainApp.java` → **Run File**
- Atau tekan **F6** setelah set MainApp sebagai main class

---

## Cara Menggunakan Aplikasi

1. **Klik "Koneksi Database"** di sidebar kiri bawah → konfirmasi sukses
2. **Dashboard** → klik "Refresh Statistik" untuk lihat ringkasan data
3. **Customer** → isi form → klik Tambah/Update/Hapus, lalu Tampilkan
4. **Barang** → masukkan nama & harga satuan → kelola data barang
5. **Transaksi** → klik "🔄 Load" untuk load customer, isi header nota
6. **Detail Nota** → klik "🔄 Load" untuk load No. Nota & Barang,
   masukkan qty → subtotal otomatis terhitung dari harga × qty

---

## Relasi Database (3NF)
```
customer (1) ──── (N) transaksi (1) ──── (N) nota (N) ──── (1) barang
```

- **customer**: nama_customer (PK), alamat_customer
- **barang**: nama_barang (PK), harga_satuan
- **transaksi**: no_nota (PK), tanggal_jam, nama_customer (FK), kasir, total
- **nota**: no_nota (PK, FK), nama_barang (PK, FK), qty, subtotal
