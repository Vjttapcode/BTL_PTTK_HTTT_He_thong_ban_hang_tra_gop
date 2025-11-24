package com.htbh.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ContractDraft implements Serializable {
	public static final long serialVersionUID = 1L;

	private KhachHang customer;
	private SanPham product;
	private DoiTac partner;
	private final List<KyThanhToan> schedule = new ArrayList<>();
	private BigDecimal mucTraTruoc;
	private String soHopDong;
	private BigDecimal tongVay;
	private BigDecimal laiSuat;
	private BigDecimal duNoConLai;
	private Date ngayKy;
	private String trangThai;

	public KhachHang getCustomer() { return customer; }
	public void setCustomer(KhachHang customer) { this.customer = customer; }

	public SanPham getProduct() { return product; }
	public void setProduct(SanPham product) { this.product = product; }

	public DoiTac getPartner() { return partner; }
	public void setPartner(DoiTac partner) { this.partner = partner; }

	public List<KyThanhToan> getSchedule() { return schedule; }

	public BigDecimal getMucTraTruoc() { return mucTraTruoc; }
	public void setMucTraTruoc(BigDecimal mucTraTruoc) { this.mucTraTruoc = mucTraTruoc; }

	public String getSoHopDong() { return soHopDong; }
	public void setSoHopDong(String soHopDong) { this.soHopDong = soHopDong; }

	public BigDecimal getTongVay() { return tongVay; }
	public void setTongVay(BigDecimal tongVay) { this.tongVay = tongVay; }

	public BigDecimal getLaiSuat() { return laiSuat; }
	public void setLaiSuat(BigDecimal laiSuat) { this.laiSuat = laiSuat; }

	public BigDecimal getDuNoConLai() { return duNoConLai; }
	public void setDuNoConLai(BigDecimal duNoConLai) { this.duNoConLai = duNoConLai; }

	public Date getNgayKy() { return ngayKy; }
	public void setNgayKy(Date ngayKy) { this.ngayKy = ngayKy; }

	public String getTrangThai() { return trangThai; }
	public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

	public void resetAll() {
		customer = null;
		resetAfterCustomer();
	}

	public void resetAfterCustomer() {
		product = null;
		partner = null;
		schedule.clear();
		mucTraTruoc = null;
		soHopDong = null;
		tongVay = null;
		laiSuat = null;
		duNoConLai = null;
		ngayKy = null;
		trangThai = null;
	}

	public void resetAfterProduct() {
		partner = null;
		schedule.clear();
		mucTraTruoc = null;
		tongVay = null;
		laiSuat = null;
		duNoConLai = null;
		ngayKy = null;
		trangThai = null;
	}

	public boolean isReadyForReview() {
		return customer != null && product != null && partner != null && !schedule.isEmpty() && mucTraTruoc != null;
	}
}

