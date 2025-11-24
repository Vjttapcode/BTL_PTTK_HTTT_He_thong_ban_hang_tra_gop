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

	public ThanhVien create(ThanhVien thanhVien) {
		if (thanhVien == null) {
			throw new IllegalArgumentException("ThanhVien khong duoc null");
		}
		final String username = thanhVien.getUsername();
		final String rawPassword = thanhVien.getRawPassword();
		final String ten = thanhVien.getTen();
		final String email = thanhVien.getEmail();

		final String checkUsernameSql = "SELECT id FROM tblThanhVien WHERE username = ?";
		final String checkEmailSql = "SELECT id FROM tblThanhVien WHERE email = ?";
		final String insertSql = "INSERT INTO tblThanhVien(username, password, ten, email) VALUES(?,?,?,?)";
		try (Connection con = ConnectionFactory.getConnection()) {
			try (PreparedStatement c = con.prepareStatement(checkUsernameSql)) {
				c.setString(1, username);
				try (ResultSet r = c.executeQuery()) {
					if (r.next()) {
						throw new IllegalStateException("Username da ton tai");
					}
				}
			}
			if (email != null && !email.isEmpty()) {
				try (PreparedStatement c2 = con.prepareStatement(checkEmailSql)) {
					c2.setString(1, email);
					try (ResultSet r2 = c2.executeQuery()) {
						if (r2.next()) {
							throw new IllegalStateException("Email da ton tai");
						}
					}
				}
			}
			try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
				final String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
				ps.setString(1, username);
				ps.setString(2, hash);
				ps.setString(3, ten);
				ps.setString(4, email);
				ps.executeUpdate();
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						int id = rs.getInt(1);
						ThanhVien saved = new ThanhVien();
						saved.setId(id);
						saved.setUsername(username);
						saved.setPasswordHash(hash);
						saved.setTen(ten);
						saved.setEmail(email);
						return saved;
					}
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


