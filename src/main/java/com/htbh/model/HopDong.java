package com.htbh.model;

import java.math.BigDecimal;
import java.sql.Date;

public class HopDong {
	private Integer id;
	private String soHopDong;
	private BigDecimal tongVay;
	private BigDecimal mucTraTruoc;
	private BigDecimal laiSuat;
	private BigDecimal duNoConLai;
	private Date ngayKy;
	private String trangThai;
	private Integer khachHangId;
	private Integer nhanVienId;
	private Integer sanPhamId;
	private Integer doiTacId;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public String getSoHopDong() { return soHopDong; }
	public void setSoHopDong(String soHopDong) { this.soHopDong = soHopDong; }

	public BigDecimal getTongVay() { return tongVay; }
	public void setTongVay(BigDecimal tongVay) { this.tongVay = tongVay; }

	public BigDecimal getMucTraTruoc() { return mucTraTruoc; }
	public void setMucTraTruoc(BigDecimal mucTraTruoc) { this.mucTraTruoc = mucTraTruoc; }

	public BigDecimal getLaiSuat() { return laiSuat; }
	public void setLaiSuat(BigDecimal laiSuat) { this.laiSuat = laiSuat; }

	public BigDecimal getDuNoConLai() { return duNoConLai; }
	public void setDuNoConLai(BigDecimal duNoConLai) { this.duNoConLai = duNoConLai; }

	public Date getNgayKy() { return ngayKy; }
	public void setNgayKy(Date ngayKy) { this.ngayKy = ngayKy; }

	public String getTrangThai() { return trangThai; }
	public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

	public Integer getKhachHangId() { return khachHangId; }
	public void setKhachHangId(Integer khachHangId) { this.khachHangId = khachHangId; }

	public Integer getNhanVienId() { return nhanVienId; }
	public void setNhanVienId(Integer nhanVienId) { this.nhanVienId = nhanVienId; }

	public Integer getSanPhamId() { return sanPhamId; }
	public void setSanPhamId(Integer sanPhamId) { this.sanPhamId = sanPhamId; }

	public Integer getDoiTacId() { return doiTacId; }
	public void setDoiTacId(Integer doiTacId) { this.doiTacId = doiTacId; }
}


