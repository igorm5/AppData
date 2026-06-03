-- ============================================================
-- SETUP DATABASE: Sistem Nota UMKM
-- Untuk SQL Server (SSMS)
-- Jalankan file ini di SSMS sebelum menjalankan aplikasi
-- ============================================================

-- Buat database
CREATE DATABASE nota_umkm;
GO

USE nota_umkm;
GO

-- ============================================================
-- TABEL ADMIN
-- ============================================================
CREATE TABLE admin (
    id_admin  INT IDENTITY(1,1) PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(30)  NOT NULL CHECK (role IN ('kasir', 'pengelola_stok'))
);
GO

-- ============================================================
-- TABEL CUSTOMER
-- ============================================================
CREATE TABLE customer (
    id_customer     INT IDENTITY(1,1) PRIMARY KEY,
    nama_customer   VARCHAR(100) NOT NULL,
    alamat_customer VARCHAR(255)
);
GO

-- ============================================================
-- TABEL BARANG
-- ============================================================
CREATE TABLE barang (
    id_barang    INT IDENTITY(1,1) PRIMARY KEY,
    nama_barang  VARCHAR(150) NOT NULL UNIQUE,
    harga_satuan DECIMAL(12,2) NOT NULL,
    stok         INT NOT NULL DEFAULT 0
);
GO

-- ============================================================
-- TABEL TRANSAKSI
-- ============================================================
CREATE TABLE transaksi (
    no_nota       VARCHAR(30)  NOT NULL PRIMARY KEY,
    tanggal_jam   DATETIME     NOT NULL DEFAULT GETDATE(),
    nama_customer VARCHAR(100),
    kasir         VARCHAR(100),
    total         DECIMAL(14,2) DEFAULT 0
);
GO

-- ============================================================
-- TABEL NOTA (Detail Transaksi)
-- ============================================================
CREATE TABLE nota (
    no_nota     VARCHAR(30)  NOT NULL,
    nama_barang VARCHAR(150) NOT NULL,
    qty         INT          NOT NULL DEFAULT 1,
    subtotal    DECIMAL(14,2),
    PRIMARY KEY (no_nota, nama_barang),
    FOREIGN KEY (no_nota)     REFERENCES transaksi(no_nota)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (nama_barang) REFERENCES barang(nama_barang)
        ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

-- ============================================================
-- DATA SAMPLE: Admin
-- ============================================================
INSERT INTO admin (username, password, role) VALUES
    ('kasir1',    'kasir123',    'kasir'),
    ('kasir2',    'kasir456',    'kasir'),
    ('pengelola', 'kelola123',   'pengelola_stok'),
    ('admin',     'admin123',    'pengelola_stok');
GO

-- ============================================================
-- DATA SAMPLE: Customer
-- ============================================================
INSERT INTO customer (nama_customer, alamat_customer) VALUES
    ('Husnul', 'Muharto GG.7'),
    ('Umum',   '-');
GO

-- ============================================================
-- DATA SAMPLE: Barang (dengan stok awal)
-- ============================================================
INSERT INTO barang (nama_barang, harga_satuan, stok) VALUES
    ('MISTER MAX COCKTAIL 500G',       32500, 50),
    ('GO BINTANG',                     12000, 100),
    ('SALAM SOSIS AYAM 750G',          25000, 40),
    ('INDOMINA DUMPLING AYAM 500G',    25500, 30),
    ('INDOMINA BENTUK IKAN 500G',      25500, 30),
    ('INDOMINA CUMI FLOWER 500GR',     26500, 25),
    ('INDOMINA ORIENTAL CARTOON 500G', 26000, 25),
    ('CUANKI GEPREK 100G',             12000, 80),
    ('PILUS KERITING 250G',            11000, 60),
    ('KOBE TEP. CRISPY KENTUCKY 850G', 21500, 20),
    ('HOK GELAS UK. 18/25BJ',           8000, 150),
    ('MASAKO SAPI 250G',               10500, 70),
    ('KANJI PANDA 1KG',                 9500, 45),
    ('JOLLY KULINER 240S',             11000, 55);
GO
select*from transaksi

