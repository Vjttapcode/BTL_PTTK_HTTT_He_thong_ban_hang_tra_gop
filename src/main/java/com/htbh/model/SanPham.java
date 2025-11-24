package com.htbh.model;

import java.math.BigDecimal;

public class SanPham {
	private Integer id;
	private String tenSp;
	private Integer soLuong;
	private BigDecimal donGia;
	private String moTa;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public String getTenSp() { return tenSp; }
	public void setTenSp(String tenSp) { this.tenSp = tenSp; }

	public Integer getSoLuong() { return soLuong; }
	public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

	public BigDecimal getDonGia() { return donGia; }
	public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }

	public String getMoTa() { return moTa; }
	public void setMoTa(String moTa) { this.moTa = moTa; }
}

