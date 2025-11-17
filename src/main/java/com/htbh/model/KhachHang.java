package com.htbh.model;

import java.sql.Date;

public class KhachHang {
	private Integer id;
	private Integer thanhVienId;
	private String cccd;
	private ThanhVien profile;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public Integer getThanhVienId() { return thanhVienId; }
	public void setThanhVienId(Integer thanhVienId) { this.thanhVienId = thanhVienId; }

	public String getCccd() { return cccd; }
	public void setCccd(String cccd) { this.cccd = cccd; }

	public ThanhVien getProfile() { return profile; }
	public void setProfile(ThanhVien profile) { this.profile = profile; }

	public String getTen() {
		return profile != null ? profile.getTen() : null;
	}

	public Date getNgaySinh() {
		return profile != null ? profile.getNgaySinh() : null;
	}

	public String getSdt() {
		return profile != null ? profile.getSdt() : null;
	}

	public String getEmail() {
		return profile != null ? profile.getEmail() : null;
	}

	public String getDiaChi() {
		return profile != null ? profile.getDiaChi() : null;
	}
}
