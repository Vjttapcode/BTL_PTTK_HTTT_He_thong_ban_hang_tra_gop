package com.htbh.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.htbh.model.ThanhVien;

@WebFilter(urlPatterns = {"/employee/*"})
public class AuthFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// Prevent caching for protected pages
		resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		resp.setHeader("Pragma", "no-cache");
		resp.setDateHeader("Expires", 0);

		HttpSession session = req.getSession(false);
		ThanhVien current = session == null ? null : (ThanhVien) session.getAttribute("CURRENT_USER");
		if (current == null) {
			resp.sendRedirect(req.getContextPath() + "/auth/login");
			return;
		}
		chain.doFilter(request, response);
	}
}


