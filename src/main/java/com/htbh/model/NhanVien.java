package com.htbh.model;

public class NhanVien {
	private Integer id;
	private String chiNhanh;
	private Integer thanhVienId;
	private Integer cuaHangId;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public String getChiNhanh() { return chiNhanh; }
	public void setChiNhanh(String chiNhanh) { this.chiNhanh = chiNhanh; }

	public Integer getThanhVienId() { return thanhVienId; }
	public void setThanhVienId(Integer thanhVienId) { this.thanhVienId = thanhVienId; }

	public Integer getCuaHangId() { return cuaHangId; }
	public void setCuaHangId(Integer cuaHangId) { this.cuaHangId = cuaHangId; }
}


