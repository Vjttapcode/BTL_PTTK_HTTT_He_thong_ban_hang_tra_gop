package com.htbh.model;

import java.math.BigDecimal;

public class DoiTac {
	private Integer id;
	private String tenDoiTac;
	private String maSoThue;
	private String loaiHinhToChuc;
	private BigDecimal mucTraTruocToiThieu;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public String getTenDoiTac() { return tenDoiTac; }
	public void setTenDoiTac(String tenDoiTac) { this.tenDoiTac = tenDoiTac; }

	public String getMaSoThue() { return maSoThue; }
	public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }

	public String getLoaiHinhToChuc() { return loaiHinhToChuc; }
	public void setLoaiHinhToChuc(String loaiHinhToChuc) { this.loaiHinhToChuc = loaiHinhToChuc; }

	public BigDecimal getMucTraTruocToiThieu() { return mucTraTruocToiThieu; }
	public void setMucTraTruocToiThieu(BigDecimal mucTraTruocToiThieu) { this.mucTraTruocToiThieu = mucTraTruocToiThieu; }
}

