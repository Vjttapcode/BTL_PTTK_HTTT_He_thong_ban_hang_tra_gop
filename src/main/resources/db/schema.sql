-- MySQL schema for He thong ban hang tra gop (subset for auth + core tables)
-- Run this script in MySQL, then update DB credentials in ConnectionFactory.

CREATE DATABASE IF NOT EXISTS htbhtg CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE htbhtg;

-- Cua hang
CREATE TABLE IF NOT EXISTS tblCuaHang (
  id INT PRIMARY KEY AUTO_INCREMENT,
  ten VARCHAR(255) NOT NULL,
  diaChi VARCHAR(255),
  moTa VARCHAR(255)
);

-- Thanh vien (account)
CREATE TABLE IF NOT EXISTS tblThanhVien (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  ten VARCHAR(255),
  email VARCHAR(255),
  ngaySinh DATE,
  diaChi VARCHAR(255),
  sdt VARCHAR(255)
);

-- Nhan vien (employee)
CREATE TABLE IF NOT EXISTS tblNhanVien (
  id INT PRIMARY KEY AUTO_INCREMENT,
  chiNhanh VARCHAR(255),
  tblThanhVienid INT NOT NULL UNIQUE,
  tblCuaHangid INT,
  CONSTRAINT fk_nv_tv FOREIGN KEY (tblThanhVienid) REFERENCES tblThanhVien(id) ON DELETE CASCADE,
  CONSTRAINT fk_nv_ch FOREIGN KEY (tblCuaHangid) REFERENCES tblCuaHang(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS tblKhachHang (
  id INT PRIMARY KEY AUTO_INCREMENT,
  cccd VARCHAR(255) UNIQUE,
  tblThanhVienid INT NOT NULL UNIQUE,
  CONSTRAINT fk_kh_created_by_tv FOREIGN KEY (tblThanhVienid) REFERENCES tblThanhVien(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tblSanPham (
  id INT PRIMARY KEY AUTO_INCREMENT,
  tenSp VARCHAR(255) NOT NULL,
  soLuong INT NOT NULL DEFAULT 0,
  donGia DECIMAL(19,2) NOT NULL,
  moTa VARCHAR(255),
  UNIQUE KEY uk_sanpham_ten (tenSp)
);

CREATE TABLE IF NOT EXISTS tblDoiTac (
  id INT PRIMARY KEY AUTO_INCREMENT,
  tenDoiTac VARCHAR(255) NOT NULL,
  maSoThue VARCHAR(50),
  loaiHinhToChuc VARCHAR(255),
  mucTraTruocToiThieu DECIMAL(5,2) NOT NULL DEFAULT 0,
  UNIQUE KEY uk_doitac_mst (maSoThue)
);

CREATE TABLE IF NOT EXISTS tblHopDong (
  id INT PRIMARY KEY AUTO_INCREMENT,
  soHopDong VARCHAR(255) NOT NULL,
  tongVay DECIMAL(19,2) NOT NULL,
  mucTraTruoc DECIMAL(19,2),
  laiSuat DECIMAL(5,2),
  duNoConLai DECIMAL(19,2),
  ngayKy DATE,
  trangThai VARCHAR(255),
  tblKhachHangid INT NOT NULL,
  tblNhanVienid INT,
  tblSanPhamid INT,
  tblDoiTacid INT,
  CONSTRAINT fk_hd_khach FOREIGN KEY (tblKhachHangid) REFERENCES tblKhachHang(id) ON DELETE CASCADE,
  CONSTRAINT fk_hd_nhanvien FOREIGN KEY (tblNhanVienid) REFERENCES tblNhanVien(id) ON DELETE SET NULL,
  CONSTRAINT fk_hd_sanpham FOREIGN KEY (tblSanPhamid) REFERENCES tblSanPham(id),
  CONSTRAINT fk_hd_doitac FOREIGN KEY (tblDoiTacid) REFERENCES tblDoiTac(id)
);

CREATE TABLE IF NOT EXISTS tblKyThanhToan (
  id INT PRIMARY KEY AUTO_INCREMENT,
  tblHopDongid INT NOT NULL,
  tenKy VARCHAR(255),
  ngayDenHan DATE,
  tienGoc DECIMAL(19,2),
  tienLai DECIMAL(19,2),
  trangThai VARCHAR(100),
  CONSTRAINT fk_kythanhtoan_hopdong FOREIGN KEY (tblHopDongid) REFERENCES tblHopDong(id) ON DELETE CASCADE
);

-- Seed minimal data (optional): create one store
INSERT INTO tblCuaHang (ten, diaChi, moTa)
VALUES ('Cua hang Trung tam', '1 Nguyen Hue, Q1, HCM', 'Cua hang mac dinh')
ON DUPLICATE KEY UPDATE ten = VALUES(ten);

-- Sample customers (insert into ThanhVien then map to KhachHang)
INSERT INTO tblThanhVien (username, password, ten, email, ngaySinh, diaChi, sdt)
VALUES ('khach1', '$2a$10$Dow1Qxq8NQvZk90un0n0ue5H1ZiZsaAJ2bI7IuAvV38DSHLVQQP4.', 'Đào Ngọc Đức', 'duc@gmail.com', '2004-06-14', 'Đống Đa, Hà Nội', '0925346463')
ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id);
SET @tv_khach1_id = LAST_INSERT_ID();

INSERT INTO tblKhachHang (cccd, tblThanhVienid)
VALUES ('001204001256', @tv_khach1_id)
ON DUPLICATE KEY UPDATE tblThanhVienid = VALUES(tblThanhVienid);
SET @khachHang1 = (SELECT id FROM tblKhachHang WHERE cccd = '001204001256');

INSERT INTO tblThanhVien (username, password, ten, email, ngaySinh, diaChi, sdt)
VALUES ('khach2', '$2a$10$Dow1Qxq8NQvZk90un0n0ue5H1ZiZsaAJ2bI7IuAvV38DSHLVQQP4.', 'Đào Ngọc Quang', 'quangdn@gmail.com', '2002-01-19', 'Hoàng Mai, Hà Nội', '0911234444')
ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id);
SET @tv_khach2_id = LAST_INSERT_ID();

INSERT INTO tblKhachHang (cccd, tblThanhVienid)
VALUES ('001234001222', @tv_khach2_id)
ON DUPLICATE KEY UPDATE tblThanhVienid = VALUES(tblThanhVienid);
SET @khachHang2 = (SELECT id FROM tblKhachHang WHERE cccd = '001234001222');

INSERT INTO tblHopDong (soHopDong, tongVay, mucTraTruoc, laiSuat, duNoConLai, ngayKy, trangThai, tblKhachHangid)
VALUES
('HD01', 20000000, 4000000, 1.7, 0, '2024-06-21', 'Đã hoàn thành', @khachHang1),
('HD02', 100000000, 10000000, 2.2, 0, '2025-03-13', 'Đã hoàn thành', @khachHang1),
('HD03', 50000000, 5000000, 0.5, 45000000, '2025-10-09', 'Còn nợ', @khachHang1);

INSERT INTO tblSanPham (tenSp, soLuong, donGia, moTa) VALUES
('Iphone 17 Pro Max 512GB', 15, 44490000, 'Màu bạc'),
('Iphone 17 Pro Max 1TB', 10, 50990000, 'Màu cam'),
('Iphone 17 Pro Max 2TB', 5, 63990000, 'Màu xanh đậm')
ON DUPLICATE KEY UPDATE tenSp = VALUES(tenSp);

INSERT INTO tblDoiTac (tenDoiTac, maSoThue, loaiHinhToChuc, mucTraTruocToiThieu) VALUES
('Công ty Tài chính FE Credit', '0313586501', 'Công ty TNHH', 20),
('Ngân hàng TMCP VPBank', '0100233583', 'Ngân hàng thương mại', 10),
('Công ty HD SAISON Finance', '0312486105', 'Công ty liên doanh', 15)
ON DUPLICATE KEY UPDATE tenDoiTac = VALUES(tenDoiTac);


