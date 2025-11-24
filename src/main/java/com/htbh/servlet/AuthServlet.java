package com.htbh.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.htbh.dao.NhanVienDAO;
import com.htbh.dao.ThanhVienDAO;
import com.htbh.model.ThanhVien;

@WebServlet(name = "AuthServlet", urlPatterns = {"/auth/*"})
public class AuthServlet extends HttpServlet {
	private final ThanhVienDAO thanhVienDAO = new ThanhVienDAO();
	private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = subPath(req);
		switch (path) {
			case "/login":
				req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
				return;
			case "/register":
				req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
				return;
			case "/logout":
				HttpSession s = req.getSession(false);
				if (s != null) s.invalidate();
				resp.sendRedirect(req.getContextPath() + "/auth/login");
				return;
			default:
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String path = subPath(req);
		switch (path) {
			case "/login":
				handleLogin(req, resp);
				return;
			case "/register":
				handleRegister(req, resp);
				return;
			default:
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String username = param(req, "username");
		String password = param(req, "password");
		if (isBlank(username) || isBlank(password)) {
			req.setAttribute("error", "Vui long nhap day du thong tin");
			req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
			return;
		}
		ThanhVien tv = thanhVienDAO.checkLogin(username, password);
		if (tv == null) {
			req.setAttribute("error", "Sai username hoac password");
			req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
			return;
		}
		HttpSession session = req.getSession(true);
		session.setAttribute("CURRENT_USER", tv);
		resp.sendRedirect(req.getContextPath() + "/employee/home");
	}

	private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String username = param(req, "username");
		String password = param(req, "password");
		String fullName = param(req, "fullName");
		String email = param(req, "email");
		String chiNhanh = param(req, "chiNhanh");

		if (isBlank(username) || isBlank(password)) {
			req.setAttribute("error", "Username/password khong duoc de trong");
			req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
			return;
		}
		
		// Kiểm tra định dạng email (nếu email không rỗng)
		if (email != null && !email.trim().isEmpty()) {
			if (!isValidEmail(email)) {
				req.setAttribute("error", "Email khong dung dinh dang");
				req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
				return;
			}
		}
		
		try {
			ThanhVien tv = new ThanhVien();
			tv.setUsername(username);
			tv.setRawPassword(password);
			tv.setTen(fullName);
			tv.setEmail(email);
			
			tv = thanhVienDAO.create(tv);
			nhanVienDAO.createForThanhVien(tv.getId(), chiNhanh, null);
			req.setAttribute("success", "Dang ky thanh cong. Vui long dang nhap.");
			req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
		} catch (IllegalStateException | IllegalArgumentException e) {
			req.setAttribute("error", e.getMessage());
			req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
		}
	}

	private String subPath(HttpServletRequest req) {
		String path = req.getPathInfo();
		if (path == null) return "";
		// Normalize: remove trailing slash (except root "/")
		if (path.endsWith("/") && path.length() > 1) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	private String param(HttpServletRequest req, String name) {
		String v = req.getParameter(name);
		return v == null ? "" : v.trim();
	}

	private boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	/**
	 * Kiểm tra định dạng email chuẩn
	 * @param email Email cần kiểm tra
	 * @return true nếu email hợp lệ, false nếu không hợp lệ
	 */
	private boolean isValidEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		// Pattern chuẩn cho email: local@domain
		// - Local part: chứa chữ, số, dấu chấm, gạch dưới, dấu trừ, dấu cộng
		// - Domain part: chứa chữ, số, dấu chấm, dấu trừ
		// - Phải có ít nhất một dấu chấm trong domain và TLD có ít nhất 2 ký tự
		String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email.trim().matches(emailPattern);
	}
}


