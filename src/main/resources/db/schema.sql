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

-- Khach hang (placeholder for next features)
CREATE TABLE IF NOT EXISTS tblKhachHang (
  id INT PRIMARY KEY AUTO_INCREMENT,
  cccd VARCHAR(255) UNIQUE,
  ten VARCHAR(255),
  sdt VARCHAR(255),
  diaChi VARCHAR(255),
  tblThanhVienid INT,
  CONSTRAINT fk_kh_created_by_tv FOREIGN KEY (tblThanhVienid) REFERENCES tblThanhVien(id)
);

-- Seed minimal data (optional): create one store
INSERT INTO tblCuaHang (ten, diaChi, moTa)
VALUES ('Cua hang Trung tam', '1 Nguyen Hue, Q1, HCM', 'Cua hang mac dinh')
ON DUPLICATE KEY UPDATE ten = VALUES(ten);


