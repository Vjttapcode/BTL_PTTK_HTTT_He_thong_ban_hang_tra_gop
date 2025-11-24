package com.htbh.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.htbh.dao.DoiTacDAO;
import com.htbh.dao.KhachHangDAO;
import com.htbh.dao.NhanVienDAO;
import com.htbh.dao.SanPhamDAO;
import com.htbh.model.ContractDraft;
import com.htbh.model.DoiTac;
import com.htbh.model.KhachHang;
import com.htbh.model.KyThanhToan;
import com.htbh.model.NhanVien;
import com.htbh.model.SanPham;
import com.htbh.model.ThanhVien;
import com.htbh.service.ContractService;

@WebServlet(name = "ContractWizardServlet", urlPatterns = {"/contracts/*"})
public class ContractWizardServlet extends HttpServlet {
	private static final String SESSION_KEY = "CONTRACT_DRAFT";
	private static final String FLASH_KEY = "CONTRACT_FLASH";

	private final KhachHangDAO khachHangDAO = new KhachHangDAO();
	private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
	private final DoiTacDAO doiTacDAO = new DoiTacDAO();
	private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
	private final ContractService contractService = new ContractService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = normalize(req);
		switch (path) {
			case "":
			case "/start":
				resetDraft(req);
				resp.sendRedirect(req.getContextPath() + "/contracts/customer");
				return;
			case "/customer":
				showCustomer(req, resp);
				return;
			case "/product":
				if (ensureCustomer(req, resp)) showProduct(req, resp);
				return;
			case "/partner":
				if (ensureProduct(req, resp)) showPartner(req, resp);
				return;
			case "/schedule":
				if (ensurePartner(req, resp)) showSchedule(req, resp);
				return;
			case "/down-payment":
				if (ensureSchedule(req, resp)) showDownPayment(req, resp);
				return;
			case "/review":
				if (ensureDownPayment(req, resp)) showReview(req, resp);
				return;
			default:
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = normalize(req);
		switch (path) {
			case "/customer/select":
				selectCustomer(req, resp);
				return;
			case "/product/select":
				selectProduct(req, resp);
				return;
			case "/partner/select":
				selectPartner(req, resp);
				return;
			case "/schedule/add":
				if (ensurePartner(req, resp)) addSchedule(req, resp);
				return;
			case "/schedule/delete":
				if (ensurePartner(req, resp)) deleteSchedule(req, resp);
				return;
			case "/down-payment/save":
				if (ensureSchedule(req, resp)) saveDownPayment(req, resp);
				return;
			case "/submit":
				if (ensureDownPayment(req, resp)) finalizeContract(req, resp);
				return;
			default:
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void showCustomer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ContractDraft draft = getDraft(req);
		req.setAttribute("draft", draft);
		String keyword = trim(req.getParameter("keyword"));
		if (keyword != null && !keyword.isEmpty()) {
			req.setAttribute("customers", khachHangDAO.searchByKeyword(keyword));
			req.setAttribute("keyword", keyword);
		}
		applyFlash(req);
		req.getRequestDispatcher("/WEB-INF/views/contract-customer.jsp").forward(req, resp);
	}

	private void showProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ContractDraft draft = getDraft(req);
		req.setAttribute("draft", draft);
		String keyword = trim(req.getParameter("keyword"));
		req.setAttribute("products", sanPhamDAO.search(keyword));
		req.setAttribute("keyword", keyword);
		applyFlash(req);
		req.getRequestDispatcher("/WEB-INF/views/contract-product.jsp").forward(req, resp);
	}

	private void showPartner(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ContractDraft draft = getDraft(req);
		req.setAttribute("draft", draft);
		String keyword = trim(req.getParameter("keyword"));
		req.setAttribute("partners", doiTacDAO.search(keyword));
		req.setAttribute("keyword", keyword);
		applyFlash(req);
		req.getRequestDispatcher("/WEB-INF/views/contract-partner.jsp").forward(req, resp);
	}

	private void showSchedule(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ContractDraft draft = getDraft(req);
		req.setAttribute("draft", draft);
		applyFlash(req);
		req.getRequestDispatcher("/WEB-INF/views/contract-schedule.jsp").forward(req, resp);
	}

	private void showDownPayment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ContractDraft draft = getDraft(req);
		req.setAttribute("draft", draft);
		req.setAttribute("suggested", suggestDownPayment(draft));
		applyFlash(req);
		req.getRequestDispatcher("/WEB-INF/views/contract-downpayment.jsp").forward(req, resp);
	}

	private void showReview(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ContractDraft draft = getDraft(req);
		req.setAttribute("draft", draft);
		BigDecimal tongVay = calculateTongVay(draft);
		req.setAttribute("calculatedTongVay", tongVay);
		req.setAttribute("calculatedDuNo", tongVay);
		applyFlash(req);
		req.getRequestDispatcher("/WEB-INF/views/contract-review.jsp").forward(req, resp);
	}

	private void selectCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Integer id = intParam(req, "id");
		if (id == null) {
			setFlash(req, "Vui lòng chọn khách hàng hợp lệ.");
			resp.sendRedirect(req.getContextPath() + "/contracts/customer");
			return;
		}
		KhachHang kh = khachHangDAO.findById(id);
		if (kh == null) {
			setFlash(req, "Không tìm thấy khách hàng.");
			resp.sendRedirect(req.getContextPath() + "/contracts/customer");
			return;
		}
		ContractDraft draft = getDraft(req);
		draft.resetAll();
		draft.setCustomer(kh);
		resp.sendRedirect(req.getContextPath() + "/contracts/product");
	}

	private void selectProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Integer id = intParam(req, "id");
		if (id == null) {
			setFlash(req, "Vui lòng chọn sản phẩm.");
			resp.sendRedirect(req.getContextPath() + "/contracts/product");
			return;
		}
		SanPham sp = sanPhamDAO.findById(id);
		if (sp == null) {
			setFlash(req, "Không tìm thấy sản phẩm.");
			resp.sendRedirect(req.getContextPath() + "/contracts/product");
			return;
		}
		ContractDraft draft = getDraft(req);
		draft.resetAfterCustomer();
		draft.setProduct(sp);
		resp.sendRedirect(req.getContextPath() + "/contracts/partner");
	}

	private void selectPartner(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Integer id = intParam(req, "id");
		if (id == null) {
			setFlash(req, "Vui lòng chọn đối tác.");
			resp.sendRedirect(req.getContextPath() + "/contracts/partner");
			return;
		}
		DoiTac dt = doiTacDAO.findById(id);
		if (dt == null) {
			setFlash(req, "Không tìm thấy đối tác.");
			resp.sendRedirect(req.getContextPath() + "/contracts/partner");
			return;
		}
		ContractDraft draft = getDraft(req);
		draft.resetAfterProduct();
		draft.setPartner(dt);
		resp.sendRedirect(req.getContextPath() + "/contracts/schedule");
	}

	private void addSchedule(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ContractDraft draft = getDraft(req);
		KyThanhToan ky = new KyThanhToan();
		ky.setTenKy(trim(req.getParameter("tenKy")));
		ky.setNgayDenHan(parseDate(req.getParameter("ngayDenHan")));
		ky.setTienGoc(parseBigDecimal(req.getParameter("tienGoc")));
		ky.setTienLai(parseBigDecimal(req.getParameter("tienLai")));
		String trangThai = trim(req.getParameter("trangThai"));
		if (trangThai == null || trangThai.isEmpty()) {
			trangThai = "Chưa thanh toán";
		}
		ky.setTrangThai(trangThai);
		if (ky.getTenKy() == null || ky.getTenKy().isEmpty() || ky.getNgayDenHan() == null || ky.getTienGoc() == null) {
			setFlash(req, "Vui lòng điền đủ thông tin kỳ thanh toán.");
		} else {
			draft.getSchedule().add(ky);
		}
		resp.sendRedirect(req.getContextPath() + "/contracts/schedule");
	}

	private void deleteSchedule(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ContractDraft draft = getDraft(req);
		Integer index = intParam(req, "index");
		if (index != null && index >= 0 && index < draft.getSchedule().size()) {
			draft.getSchedule().remove((int) index);
		}
		resp.sendRedirect(req.getContextPath() + "/contracts/schedule");
	}

	private void saveDownPayment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ContractDraft draft = getDraft(req);
		BigDecimal value = parseBigDecimal(req.getParameter("mucTraTruoc"));
		if (value == null) {
			setFlash(req, "Vui lòng nhập mức trả trước hợp lệ.");
			resp.sendRedirect(req.getContextPath() + "/contracts/down-payment");
			return;
		}
		BigDecimal min = suggestDownPayment(draft);
		if (min != null && value.compareTo(min) < 0) {
			setFlash(req, "Mức trả trước phải lớn hơn hoặc bằng " + min.toPlainString());
			resp.sendRedirect(req.getContextPath() + "/contracts/down-payment");
			return;
		}
		draft.setMucTraTruoc(value);
		resp.sendRedirect(req.getContextPath() + "/contracts/review");
	}

	private void finalizeContract(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String action = req.getParameter("action");
		if ("back".equals(action)) {
			resp.sendRedirect(req.getContextPath() + "/contracts/down-payment");
			return;
		}
		ContractDraft draft = getDraft(req);
		draft.setSoHopDong(trim(req.getParameter("soHopDong")));
		draft.setLaiSuat(parseBigDecimal(req.getParameter("laiSuat")));
		draft.setNgayKy(parseDate(req.getParameter("ngayKy")));
		draft.setTrangThai(trim(req.getParameter("trangThai")));

		if (draft.getSchedule().isEmpty()) {
			setFlash(req, "Cần ít nhất một kỳ thanh toán.");
			resp.sendRedirect(req.getContextPath() + "/contracts/schedule");
			return;
		}
		if (draft.getMucTraTruoc() == null) {
			setFlash(req, "Chưa có mức trả trước.");
			resp.sendRedirect(req.getContextPath() + "/contracts/down-payment");
			return;
		}
		BigDecimal tongVay = calculateTongVay(draft);
		if (tongVay == null) {
			setFlash(req, "Không xác định được tổng vay. Kiểm tra lại giá sản phẩm và mức trả trước.");
			resp.sendRedirect(req.getContextPath() + "/contracts/review");
			return;
		}
		draft.setTongVay(tongVay);
		draft.setDuNoConLai(tongVay);

		HttpSession session = req.getSession(false);
		ThanhVien tv = session == null ? null : (ThanhVien) session.getAttribute("CURRENT_USER");
		if (tv == null) {
			resp.sendRedirect(req.getContextPath() + "/auth/login");
			return;
		}
		NhanVien nv = nhanVienDAO.findByThanhVienId(tv.getId());
		if (nv == null) {
			setFlash(req, "Tài khoản hiện tại chưa được gán vai trò nhân viên.");
			resp.sendRedirect(req.getContextPath() + "/employee/home");
			return;
		}

		List<KyThanhToan> snapshot = new ArrayList<>(draft.getSchedule());
		try {
			int hopDongId = contractService.createContract(draft, nv.getId());
			req.setAttribute("contractId", hopDongId);
			req.setAttribute("draft", draft);
			req.setAttribute("schedule", snapshot);
			req.getSession().removeAttribute(SESSION_KEY);
			req.getRequestDispatcher("/WEB-INF/views/contract-print.jsp").forward(req, resp);
		} catch (IllegalStateException ex) {
			setFlash(req, ex.getMessage());
			resp.sendRedirect(req.getContextPath() + "/contracts/review");
		}
	}

	private boolean ensureCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (getDraft(req).getCustomer() == null) {
			resp.sendRedirect(req.getContextPath() + "/contracts/customer");
			return false;
		}
		return true;
	}

	private boolean ensureProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!ensureCustomer(req, resp)) return false;
		if (getDraft(req).getProduct() == null) {
			resp.sendRedirect(req.getContextPath() + "/contracts/product");
			return false;
		}
		return true;
	}

	private boolean ensurePartner(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!ensureProduct(req, resp)) return false;
		if (getDraft(req).getPartner() == null) {
			resp.sendRedirect(req.getContextPath() + "/contracts/partner");
			return false;
		}
		return true;
	}

	private boolean ensureSchedule(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!ensurePartner(req, resp)) return false;
		if (getDraft(req).getSchedule().isEmpty()) {
			resp.sendRedirect(req.getContextPath() + "/contracts/schedule");
			return false;
		}
		return true;
	}

	private boolean ensureDownPayment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (!ensureSchedule(req, resp)) return false;
		if (getDraft(req).getMucTraTruoc() == null) {
			resp.sendRedirect(req.getContextPath() + "/contracts/down-payment");
			return false;
		}
		return true;
	}

	private ContractDraft getDraft(HttpServletRequest req) {
		HttpSession session = req.getSession(true);
		ContractDraft draft = (ContractDraft) session.getAttribute(SESSION_KEY);
		if (draft == null) {
			draft = new ContractDraft();
			session.setAttribute(SESSION_KEY, draft);
		}
		return draft;
	}

	private void resetDraft(HttpServletRequest req) {
		req.getSession(true).removeAttribute(SESSION_KEY);
	}

	private String normalize(HttpServletRequest req) {
		String path = req.getPathInfo();
		if (path == null) return "";
		if (path.endsWith("/") && path.length() > 1) {
			return path.substring(0, path.length() - 1);
		}
		return path;
	}

	private Integer intParam(HttpServletRequest req, String name) {
		try {
			return Integer.parseInt(req.getParameter(name));
		} catch (Exception e) {
			return null;
		}
	}

	private void setFlash(HttpServletRequest req, String message) {
		req.getSession(true).setAttribute(FLASH_KEY, message);
	}

	private void applyFlash(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) return;
		Object msg = session.getAttribute(FLASH_KEY);
		if (msg != null) {
			req.setAttribute("message", msg.toString());
			session.removeAttribute(FLASH_KEY);
		}
	}

	private String trim(String value) {
		return value == null ? null : value.trim();
	}

	private BigDecimal parseBigDecimal(String value) {
		try {
			if (value == null || value.trim().isEmpty()) return null;
			return new BigDecimal(value.replace(".", "").replace(",", "."));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Date parseDate(String value) {
		try {
			if (value == null || value.trim().isEmpty()) return null;
			return Date.valueOf(value);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private BigDecimal suggestDownPayment(ContractDraft draft) {
		if (draft.getProduct() == null || draft.getPartner() == null) return null;
		BigDecimal price = draft.getProduct().getDonGia();
		BigDecimal minPercent = draft.getPartner().getMucTraTruocToiThieu();
		if (price == null || minPercent == null) return null;
		return price.multiply(minPercent).divide(BigDecimal.valueOf(100));
	}

	private BigDecimal calculateTongVay(ContractDraft draft) {
		if (draft.getProduct() == null) return null;
		BigDecimal price = draft.getProduct().getDonGia();
		if (price == null) return null;
		BigDecimal down = draft.getMucTraTruoc();
		if (down == null) return price;
		BigDecimal result = price.subtract(down);
		if (result.compareTo(BigDecimal.ZERO) < 0) {
			return BigDecimal.ZERO;
		}
		return result;
	}
}

