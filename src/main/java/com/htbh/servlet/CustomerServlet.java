package com.htbh.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.htbh.dao.HopDongDAO;
import com.htbh.dao.KhachHangDAO;
import com.htbh.model.HopDong;
import com.htbh.model.KhachHang;

@WebServlet(name = "CustomerServlet", urlPatterns = {"/customer/*"})
public class CustomerServlet extends HttpServlet {
	private final KhachHangDAO khachHangDAO = new KhachHangDAO();
	private final HopDongDAO hopDongDAO = new HopDongDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		switch (normalizePath(req)) {
			case "/detail":
				showDetail(req, resp);
				break;
			case "/history":
				showHistory(req, resp);
				break;
			case "/search":
			case "":
				showSearch(req, resp);
				break;
			default:
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = normalizePath(req);
		if ("/search".equals(path) || "".equals(path)) {
			showSearch(req, resp);
			return;
		}
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private void showSearch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Object flash = req.getSession().getAttribute("CUSTOMER_ERROR");
		if (flash != null) {
			req.setAttribute("error", flash.toString());
			req.getSession().removeAttribute("CUSTOMER_ERROR");
		}
		String keyword = trim(req.getParameter("keyword"));
		if (keyword != null && !keyword.isEmpty()) {
			List<KhachHang> list = khachHangDAO.searchByKeyword(keyword);
			req.setAttribute("customers", list);
			req.setAttribute("keyword", keyword);
			if (list.isEmpty()) {
				req.setAttribute("message", "Không tìm thấy khách hàng phù hợp.");
			}
		}
		req.getRequestDispatcher("/WEB-INF/views/customer-search.jsp").forward(req, resp);
	}

	private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer id = intParam(req, "id");
		if (id == null) {
			resp.sendRedirect(req.getContextPath() + "/customer/search");
			return;
		}
		KhachHang kh = khachHangDAO.findById(id);
		if (kh == null) {
			req.getSession().setAttribute("CUSTOMER_ERROR", "Không tìm thấy khách hàng.");
			resp.sendRedirect(req.getContextPath() + "/customer/search");
			return;
		}
		req.setAttribute("customer", kh);
		req.getRequestDispatcher("/WEB-INF/views/customer-detail.jsp").forward(req, resp);
	}

	private void showHistory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer id = intParam(req, "id");
		if (id == null) {
			resp.sendRedirect(req.getContextPath() + "/customer/search");
			return;
		}
		KhachHang kh = khachHangDAO.findById(id);
		if (kh == null) {
			req.getSession().setAttribute("CUSTOMER_ERROR", "Không tìm thấy khách hàng.");
			resp.sendRedirect(req.getContextPath() + "/customer/search");
			return;
		}
		List<HopDong> hopDongs = hopDongDAO.findByKhachHangId(id);
		req.setAttribute("customer", kh);
		req.setAttribute("contracts", hopDongs);
		req.getRequestDispatcher("/WEB-INF/views/customer-history.jsp").forward(req, resp);
	}

	private String normalizePath(HttpServletRequest req) {
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

	private String trim(String value) {
		return value == null ? null : value.trim();
	}
}


