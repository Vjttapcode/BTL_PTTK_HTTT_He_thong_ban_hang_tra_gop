package com.htbh.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.htbh.dao.DoiTacDAO;
import com.htbh.dao.HopDongDAO;
import com.htbh.dao.KhachHangDAO;
import com.htbh.dao.KyThanhToanDAO;
import com.htbh.dao.SanPhamDAO;
import com.htbh.dao.TKDuNoDAO;
import com.htbh.model.DoiTac;
import com.htbh.model.HopDong;
import com.htbh.model.KhachHang;
import com.htbh.model.KyThanhToan;
import com.htbh.model.SanPham;

/**
 * Servlet xử lý flow thống kê khách hàng theo dư nợ
 * Theo sơ đồ thiết kế:
 * - gdBaoCaoTK.jsp: Trang báo cáo thống kê
 * - ChonNguongDuNo.jsp: Chọn ngưỡng dư nợ
 * - doChonNguongDuNo.jsp: Xử lý chọn ngưỡng dư nợ
 * - ThongKeDuNo.jsp: Hiển thị danh sách khách hàng
 * - TKChiTiet.jsp: Chi tiết khách hàng và hợp đồng
 * - HopDongChiTiet.jsp: Chi tiết hợp đồng
 */
@WebServlet(name = "TKDuNoServlet", urlPatterns = {
	"/debt-statistics/report",           // gdBaoCaoTK.jsp
	"/debt-statistics/choose-threshold", // ChonNguongDuNo.jsp
	"/debt-statistics/process",          // doChonNguongDuNo.jsp
	"/debt-statistics/list",             // ThongKeDuNo.jsp
	"/debt-statistics/customer-detail",  // TKChiTiet.jsp
	"/debt-statistics/contract-detail"   // HopDongChiTiet.jsp
})
public class TKDuNoServlet extends HttpServlet {
	
	private TKDuNoDAO tkDuNoDAO = new TKDuNoDAO();
	private HopDongDAO hopDongDAO = new HopDongDAO();
	private SanPhamDAO sanPhamDAO = new SanPhamDAO();
	private DoiTacDAO doiTacDAO = new DoiTacDAO();
	private KyThanhToanDAO kyThanhToanDAO = new KyThanhToanDAO();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String path = req.getServletPath();
		HttpSession session = req.getSession(false);
		
		// Kiểm tra đăng nhập
		if (session == null || session.getAttribute("CURRENT_USER") == null) {
			resp.sendRedirect(req.getContextPath() + "/auth/login");
			return;
		}
		
		switch (path) {
			case "/debt-statistics/report":
				showReportPage(req, resp);
				break;
			case "/debt-statistics/choose-threshold":
				showChooseThresholdPage(req, resp);
				break;
			case "/debt-statistics/list":
				showDebtListPage(req, resp);
				break;
			case "/debt-statistics/customer-detail":
				showCustomerDetailPage(req, resp);
				break;
			case "/debt-statistics/contract-detail":
				showContractDetailPage(req, resp);
				break;
			default:
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String path = req.getServletPath();
		HttpSession session = req.getSession(false);
		
		// Kiểm tra đăng nhập
		if (session == null || session.getAttribute("CURRENT_USER") == null) {
			resp.sendRedirect(req.getContextPath() + "/auth/login");
			return;
		}
		
		if ("/debt-statistics/process".equals(path)) {
			processChooseThreshold(req, resp);
		} else {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
	}
	
	/**
	 * Hiển thị trang báo cáo thống kê (gdBaoCaoTK.jsp)
	 */
	private void showReportPage(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		req.getRequestDispatcher("/WEB-INF/views/gdBaoCaoTK.jsp").forward(req, resp);
	}
	
	/**
	 * Hiển thị trang chọn ngưỡng dư nợ (ChonNguongDuNo.jsp)
	 */
	private void showChooseThresholdPage(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		String error = req.getParameter("error");
		
		if ("true".equals(error)) {
			// Hiển thị thông báo không có hợp đồng
			BigDecimal nguongDuNo = (BigDecimal) session.getAttribute("nguongDuNo");
			req.setAttribute("error", true);
			req.setAttribute("nguongDuNo", nguongDuNo);
		} else {
			// Hiển thị form nhập ngưỡng dư nợ
			req.setAttribute("error", false);
		}
		
		req.getRequestDispatcher("/WEB-INF/views/ChonNguongDuNo.jsp").forward(req, resp);
	}
	
	/**
	 * Xử lý chọn ngưỡng dư nợ (doChonNguongDuNo.jsp)
	 */
	private void processChooseThreshold(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		try {
			String duNoStr = req.getParameter("nguongDuNo");
			if (duNoStr == null || duNoStr.trim().isEmpty()) {
				resp.sendRedirect(req.getContextPath() + "/debt-statistics/choose-threshold?error=true");
				return;
			}
			
			// Chuyển đổi từ string (có thể có dấu chấm phân cách) sang BigDecimal
			duNoStr = duNoStr.replaceAll("\\.", "").replaceAll(",", ".");
			BigDecimal nguongDuNo = new BigDecimal(duNoStr);
			
			// Lưu vào session
			session.setAttribute("nguongDuNo", nguongDuNo);
			
			// Gọi checkDuNo() từ TKDuNoDAO
			KhachHang[] listKhachHang = tkDuNoDAO.checkDuNo(nguongDuNo);
			
			if (listKhachHang == null || listKhachHang.length == 0) {
				// Không có hợp đồng trong ngưỡng dư nợ này
				resp.sendRedirect(req.getContextPath() + "/debt-statistics/choose-threshold?error=true");
			} else {
				// Lưu danh sách khách hàng và ngưỡng dư nợ vào session
				session.setAttribute("listKhachHang", Arrays.asList(listKhachHang));
				session.setAttribute("duNo", nguongDuNo);
				resp.sendRedirect(req.getContextPath() + "/debt-statistics/list");
			}
		} catch (NumberFormatException e) {
			resp.sendRedirect(req.getContextPath() + "/debt-statistics/choose-threshold?error=true");
		}
	}
	
	/**
	 * Hiển thị danh sách khách hàng theo dư nợ (ThongKeDuNo.jsp)
	 */
	private void showDebtListPage(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		@SuppressWarnings("unchecked")
		List<KhachHang> listKhachHang = (List<KhachHang>) session.getAttribute("listKhachHang");
		BigDecimal duNo = (BigDecimal) session.getAttribute("duNo");
		
		if (listKhachHang == null || duNo == null) {
			resp.sendRedirect(req.getContextPath() + "/debt-statistics/choose-threshold");
			return;
		}
		
		req.setAttribute("listKhachHang", listKhachHang);
		req.setAttribute("duNo", duNo);
		req.getRequestDispatcher("/WEB-INF/views/ThongKeDuNo.jsp").forward(req, resp);
	}
	
	/**
	 * Hiển thị chi tiết khách hàng và hợp đồng (TKChiTiet.jsp)
	 */
	private void showCustomerDetailPage(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		String idStr = req.getParameter("id");
		if (idStr == null || idStr.trim().isEmpty()) {
			resp.sendRedirect(req.getContextPath() + "/debt-statistics/list");
			return;
		}
		
		try {
			int khachHangId = Integer.parseInt(idStr);
			
			@SuppressWarnings("unchecked")
			List<KhachHang> listKhachHang = (List<KhachHang>) session.getAttribute("listKhachHang");
			
			if (listKhachHang == null) {
				resp.sendRedirect(req.getContextPath() + "/debt-statistics/list");
				return;
			}
			
			// Tìm khách hàng theo ID
			KhachHang selectedKhachHang = null;
			for (KhachHang kh : listKhachHang) {
				if (kh.getId() != null && kh.getId().equals(khachHangId)) {
					selectedKhachHang = kh;
					break;
				}
			}
			
			if (selectedKhachHang == null) {
				resp.sendRedirect(req.getContextPath() + "/debt-statistics/list");
				return;
			}
			
			// Lưu khách hàng đã chọn vào session
			session.setAttribute("selectedKhachHang", selectedKhachHang);
			
			// Gọi getHopDongByKhachHang() từ HopDongDAO
			HopDong[] listHopDong = hopDongDAO.getHopDongByKhachHang(selectedKhachHang);
			
			// Lưu danh sách hợp đồng vào session
			session.setAttribute("listHopDong", Arrays.asList(listHopDong));
			
			req.setAttribute("khachHang", selectedKhachHang);
			req.setAttribute("listHopDong", Arrays.asList(listHopDong));
			req.getRequestDispatcher("/WEB-INF/views/TKChiTiet.jsp").forward(req, resp);
		} catch (NumberFormatException e) {
			resp.sendRedirect(req.getContextPath() + "/debt-statistics/list");
		}
	}
	
	/**
	 * Hiển thị chi tiết hợp đồng (HopDongChiTiet.jsp)
	 */
	private void showContractDetailPage(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		String idStr = req.getParameter("id");
		if (idStr == null || idStr.trim().isEmpty()) {
			resp.sendRedirect(req.getContextPath() + "/debt-statistics/customer-detail");
			return;
		}
		
		try {
			int hopDongId = Integer.parseInt(idStr);
			
			@SuppressWarnings("unchecked")
			List<HopDong> listHopDong = (List<HopDong>) session.getAttribute("listHopDong");
			
			if (listHopDong == null) {
				resp.sendRedirect(req.getContextPath() + "/debt-statistics/customer-detail");
				return;
			}
			
			// Tìm hợp đồng theo ID
			HopDong selectedHopDong = null;
			for (HopDong hd : listHopDong) {
				if (hd.getId() != null && hd.getId().equals(hopDongId)) {
					selectedHopDong = hd;
					break;
				}
			}
			
			if (selectedHopDong == null) {
				resp.sendRedirect(req.getContextPath() + "/debt-statistics/customer-detail");
				return;
			}
			
			// Lưu hợp đồng đã chọn vào session
			session.setAttribute("selectedHopDong", selectedHopDong);
			
			// Load related entities
			KhachHang khachHang = (KhachHang) session.getAttribute("selectedKhachHang");
			// If not in session, try to load from database using contract's customer ID
			if (khachHang == null && selectedHopDong.getKhachHangId() != null) {
				KhachHangDAO khachHangDAO = new KhachHangDAO();
				khachHang = khachHangDAO.findById(selectedHopDong.getKhachHangId());
			}
			SanPham sanPham = null;
			DoiTac doiTac = null;
			List<KyThanhToan> kyThanhToanList = new ArrayList<>();
			
			if (selectedHopDong.getSanPhamId() != null) {
				sanPham = sanPhamDAO.findById(selectedHopDong.getSanPhamId());
			}
			if (selectedHopDong.getDoiTacId() != null) {
				doiTac = doiTacDAO.findById(selectedHopDong.getDoiTacId());
			}
			kyThanhToanList = kyThanhToanDAO.findByHopDongId(hopDongId);
			
			req.setAttribute("hopDong", selectedHopDong);
			req.setAttribute("khachHang", khachHang);
			req.setAttribute("sanPham", sanPham);
			req.setAttribute("doiTac", doiTac);
			req.setAttribute("kyThanhToanList", kyThanhToanList);
			req.getRequestDispatcher("/WEB-INF/views/HopDongChiTiet.jsp").forward(req, resp);
		} catch (NumberFormatException e) {
			resp.sendRedirect(req.getContextPath() + "/debt-statistics/customer-detail");
		}
	}
}

