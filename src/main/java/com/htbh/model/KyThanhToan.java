package com.htbh.model;

import java.math.BigDecimal;
import java.sql.Date;

public class KyThanhToan {
	private Integer id;
	private Integer hopDongId;
	private String tenKy;
	private Date ngayDenHan;
	private BigDecimal tienGoc;
	private BigDecimal tienLai;
	private String trangThai;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public Integer getHopDongId() { return hopDongId; }
	public void setHopDongId(Integer hopDongId) { this.hopDongId = hopDongId; }

	public String getTenKy() { return tenKy; }
	public void setTenKy(String tenKy) { this.tenKy = tenKy; }

	public Date getNgayDenHan() { return ngayDenHan; }
	public void setNgayDenHan(Date ngayDenHan) { this.ngayDenHan = ngayDenHan; }

	public BigDecimal getTienGoc() { return tienGoc; }
	public void setTienGoc(BigDecimal tienGoc) { this.tienGoc = tienGoc; }

	public BigDecimal getTienLai() { return tienLai; }
	public void setTienLai(BigDecimal tienLai) { this.tienLai = tienLai; }

	public String getTrangThai() { return trangThai; }
	public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}

