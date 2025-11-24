package com.htbh.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.htbh.dao.HopDongDAO;
import com.htbh.dao.KyThanhToanDAO;
import com.htbh.model.ContractDraft;
import com.htbh.model.HopDong;
import com.htbh.model.KyThanhToan;

public class ContractService {

	private final HopDongDAO hopDongDAO = new HopDongDAO();
	private final KyThanhToanDAO kyThanhToanDAO = new KyThanhToanDAO();

	public int createContract(ContractDraft draft, Integer nhanVienId) {
		if (draft == null || !draft.isReadyForReview()) {
			throw new IllegalStateException("Thiếu thông tin hợp đồng");
		}
		HopDong hd = new HopDong();
		hd.setSoHopDong(draft.getSoHopDong());
		hd.setMucTraTruoc(draft.getMucTraTruoc());
		hd.setLaiSuat(draft.getLaiSuat());
		hd.setNgayKy(draft.getNgayKy());
		hd.setTrangThai(draft.getTrangThai());
		hd.setKhachHangId(draft.getCustomer().getId());
		if (draft.getProduct() != null) hd.setSanPhamId(draft.getProduct().getId());
		if (draft.getPartner() != null) hd.setDoiTacId(draft.getPartner().getId());
		hd.setNhanVienId(nhanVienId);

		BigDecimal tongVay = draft.getTongVay();
		if (tongVay == null && draft.getProduct() != null && draft.getProduct().getDonGia() != null && draft.getMucTraTruoc() != null) {
			tongVay = draft.getProduct().getDonGia().subtract(draft.getMucTraTruoc());
		}
		hd.setTongVay(tongVay);
		if (hd.getTongVay() == null) {
			throw new IllegalStateException("Chưa có tổng vay cho hợp đồng");
		}
		BigDecimal duNoConLai = draft.getDuNoConLai() != null ? draft.getDuNoConLai() : hd.getTongVay();
		hd.setDuNoConLai(duNoConLai);

		if (hd.getNgayKy() == null) {
			hd.setNgayKy(new Date(System.currentTimeMillis()));
		}
		if (hd.getTrangThai() == null || hd.getTrangThai().isEmpty()) {
			hd.setTrangThai("Mới");
		}
		if (hd.getSoHopDong() == null || hd.getSoHopDong().isEmpty()) {
			hd.setSoHopDong("HD-" + System.currentTimeMillis());
		}
		if (hd.getLaiSuat() == null) {
			hd.setLaiSuat(BigDecimal.ZERO);
		}

		int hopDongId = hopDongDAO.insert(hd);
		List<KyThanhToan> schedule = draft.getSchedule();
		kyThanhToanDAO.insertBatch(hopDongId, schedule);
		return hopDongId;
	}
}

