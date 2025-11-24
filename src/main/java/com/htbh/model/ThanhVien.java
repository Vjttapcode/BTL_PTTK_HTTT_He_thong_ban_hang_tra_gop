package com.htbh.model;

import java.sql.Date;

public class ThanhVien {
	private Integer id;
	private String username;
	private String passwordHash;
	private String ten;
	private String email;
	private Date ngaySinh;
	private String diaChi;
	private String sdt;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getPasswordHash() { return passwordHash; }
	public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

	public String getTen() { return ten; }
	public void setTen(String ten) { this.ten = ten; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public Date getNgaySinh() { return ngaySinh; }
	public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

	public String getDiaChi() { return diaChi; }
	public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

	public String getSdt() { return sdt; }
	public void setSdt(String sdt) { this.sdt = sdt; }

	// Trường tạm thời để lưu mật khẩu chưa hash (chỉ dùng khi tạo mới)
	private transient String rawPassword;

	public String getRawPassword() { return rawPassword; }
	public void setRawPassword(String rawPassword) { this.rawPassword = rawPassword; }
}


