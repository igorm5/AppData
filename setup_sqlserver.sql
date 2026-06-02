-- ============================================================
-- SETUP DATABASE: Sistem Nota UMKM
-- Untuk SQL Server (SSMS)
-- ============================================================

-- Buat database
CREATE DATABASE nota_umkm;
GO

-- Gunakan database
USE nota_umkm;
GO

-- Tabel Customer
CREATE TABLE customer (
    nama_customer   VARCHAR(100) NOT NULL PRIMARY KEY,
    alamat_customer VARCHAR(255)
);
GO

-- Tabel Barang
CREATE TABLE barang (
    nama_barang  VARCHAR(150) NOT NULL PRIMARY KEY,
    harga_satuan DECIMAL(12,2) NOT NULL
);
GO

-- Tabel Transaksi
CREATE TABLE transaksi (
    no_nota       VARCHAR(30)  NOT NULL PRIMARY KEY,
    tanggal_jam   DATETIME     NOT NULL,
    nama_customer VARCHAR(100),
    kasir         VARCHAR(100),
    total         DECIMAL(14,2),
    FOREIGN KEY (nama_customer) REFERENCES customer(nama_customer)
        ON UPDATE CASCADE ON DELETE SET NULL
);
GO

-- Tabel Nota / Detail
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
-- DATA SAMPLE (dari struk nota C032026-009804)
-- ============================================================

INSERT INTO customer (nama_customer, alamat_customer) VALUES
    ('Husnul', 'Muharto GG.7');
GO

INSERT INTO barang (nama_barang, harga_satuan) VALUES
    ('MISTER MAX COCKTAIL 500G',       32500),
    ('GO BINTANG',                     12000),
    ('SALAM SOSIS AYAM 750G',          25000),
    ('INDOMINA DUMPLING AYAM 500G',    25500),
    ('INDOMINA BENTUK IKAN 500G',      25500),
    ('INDOMINA CUMI FLOWER 500GR',     26500),
    ('INDOMINA ORIENTAL CARTOON 500G', 26000),
    ('CUANKI GEPREK 100G',             12000),
    ('PILUS KERITING 250G',            11000),
    ('KOBE TEP. CRISPY KENTUCKY 850G', 21500),
    ('HOK GELAS UK. 18/25BJ',           8000),
    ('MASAKO SAPI 250G',               10500),
    ('KANJI PANDA 1KG',                 9500),
    ('JOLLY KULINER 240S',             11000);
GO

INSERT INTO transaksi (no_nota, tanggal_jam, nama_customer, kasir, total) VALUES
    ('C032026-009804', '2026-03-30 14:48:15', 'Husnul', 'Muna New', 363500);
GO

INSERT INTO nota (no_nota, nama_barang, qty, subtotal) VALUES
    ('C032026-009804', 'MISTER MAX COCKTAIL 500G',       2,  65000),
    ('C032026-009804', 'GO BINTANG',                     1,  12000),
    ('C032026-009804', 'SALAM SOSIS AYAM 750G',          2,  50000),
    ('C032026-009804', 'INDOMINA DUMPLING AYAM 500G',    1,  25500),
    ('C032026-009804', 'INDOMINA BENTUK IKAN 500G',      1,  25500),
    ('C032026-009804', 'INDOMINA CUMI FLOWER 500GR',     1,  26500),
    ('C032026-009804', 'INDOMINA ORIENTAL CARTOON 500G', 1,  26000),
    ('C032026-009804', 'CUANKI GEPREK 100G',             2,  24000),
    ('C032026-009804', 'PILUS KERITING 250G',            1,  11000),
    ('C032026-009804', 'KOBE TEP. CRISPY KENTUCKY 850G', 2,  43000),
    ('C032026-009804', 'HOK GELAS UK. 18/25BJ',          3,  24000),
    ('C032026-009804', 'MASAKO SAPI 250G',               1,  10500),
    ('C032026-009804', 'KANJI PANDA 1KG',                1,   9500),
    ('C032026-009804', 'JOLLY KULINER 240S',             1,  11000);
GO
