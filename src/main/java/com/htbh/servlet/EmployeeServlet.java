package com.htbh.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.htbh.model.ThanhVien;

@WebServlet(name = "EmployeeServlet", urlPatterns = {"/employee/home"})
public class EmployeeServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		ThanhVien tv = session == null ? null : (ThanhVien) session.getAttribute("CURRENT_USER");
		if (tv == null) {
			resp.sendRedirect(req.getContextPath() + "/auth/login");
			return;
		}
		String displayName = tv.getTen() == null || tv.getTen().isEmpty() ? tv.getUsername() : tv.getTen();
		req.setAttribute("ten", displayName);
		req.setAttribute("initials", generateInitials(displayName));
		req.getRequestDispatcher("/WEB-INF/views/employee-home.jsp").forward(req, resp);
	}

	private String generateInitials(String name) {
		if (name == null || name.trim().isEmpty()) return "U";
		String[] parts = name.trim().split("\\s+");
		if (parts.length == 1) {
			return parts[0].substring(0, Math.min(1, parts[0].length())).toUpperCase();
		}
		String first = parts[0];
		String last = parts[parts.length - 1];
		String i1 = first.isEmpty() ? "" : first.substring(0, 1);
		String i2 = last.isEmpty() ? "" : last.substring(0, 1);
		return (i1 + i2).toUpperCase();
	}
}


