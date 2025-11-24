package com.htbh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mindrot.jbcrypt.BCrypt;

import com.htbh.config.ConnectionFactory;
import com.htbh.model.ThanhVien;

public class ThanhVienDAO {

	public ThanhVien findByUsername(String username) {
		final String sql = "SELECT * FROM tblThanhVien WHERE username = ?";
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public ThanhVien findByEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return null;
		}
		final String sql = "SELECT * FROM tblThanhVien WHERE email = ?";
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, email.trim());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	public ThanhVien create(ThanhVien thanhVien) {
		if (thanhVien == null) {
			throw new IllegalArgumentException("ThanhVien khong duoc null");
		}
		
		String username = thanhVien.getUsername();
		String rawPassword = thanhVien.getRawPassword();
		String ten = thanhVien.getTen();
		String email = thanhVien.getEmail();
		
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Username khong duoc de trong");
		}
		if (rawPassword == null || rawPassword.isEmpty()) {
			throw new IllegalArgumentException("Password khong duoc de trong");
		}
		
		// Kiểm tra username trùng
		if (findByUsername(username) != null) {
			throw new IllegalStateException("Username da ton tai");
		}
		
		// Kiểm tra định dạng email (nếu email không rỗng)
		if (email != null && !email.trim().isEmpty()) {
			if (!isValidEmail(email)) {
				throw new IllegalStateException("Email khong dung dinh dang");
			}
			// Kiểm tra email trùng
			if (findByEmail(email) != null) {
				throw new IllegalStateException("Email da ton tai");
			}
		}
		
		// Tạo tài khoản mới
		final String insertSql = "INSERT INTO tblThanhVien(username, password, ten, email) VALUES(?,?,?,?)";
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
			final String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
			ps.setString(1, username);
			ps.setString(2, hash);
			ps.setString(3, ten);
			ps.setString(4, email);
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					int id = rs.getInt(1);
					ThanhVien tv = new ThanhVien();
					tv.setId(id);
					tv.setUsername(username);
					tv.setPasswordHash(hash);
					tv.setTen(ten);
					tv.setEmail(email);
					return tv;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public ThanhVien checkLogin(String username, String rawPassword) {
		ThanhVien tv = findByUsername(username);
		if (tv == null) return null;
		if (BCrypt.checkpw(rawPassword, tv.getPasswordHash())) {
			return tv;
		}
		return null;
	}

	private boolean isValidEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email.trim().matches(emailPattern);
	}

	private ThanhVien map(ResultSet rs) throws SQLException {
		ThanhVien tv = new ThanhVien();
		tv.setId(rs.getInt("id"));
		tv.setUsername(rs.getString("username"));
		tv.setPasswordHash(rs.getString("password"));
		tv.setTen(rs.getString("ten"));
		tv.setEmail(rs.getString("email"));
		return tv;
	}
}


