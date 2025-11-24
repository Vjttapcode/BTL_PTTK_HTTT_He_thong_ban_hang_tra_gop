package com.htbh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.htbh.config.ConnectionFactory;
import com.htbh.model.SanPham;

public class SanPhamDAO {

	private static final String BASE_SELECT = "SELECT id, tenSp, soLuong, donGia, moTa FROM tblSanPham ";

	public List<SanPham> search(String keyword) {
		List<SanPham> list = new ArrayList<>();
		StringBuilder sql = new StringBuilder(BASE_SELECT);
		boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
		if (hasKeyword) {
			sql.append("WHERE tenSp LIKE ? ");
		}
		sql.append("ORDER BY tenSp");
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql.toString())) {
			if (hasKeyword) {
				ps.setString(1, "%" + keyword.trim() + "%");
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(map(rs));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	public SanPham findById(int id) {
		String sql = BASE_SELECT + "WHERE id = ?";
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
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

	private SanPham map(ResultSet rs) throws SQLException {
		SanPham sp = new SanPham();
		sp.setId(rs.getInt("id"));
		sp.setTenSp(rs.getString("tenSp"));
		sp.setSoLuong(rs.getInt("soLuong"));
		sp.setDonGia(rs.getBigDecimal("donGia"));
		sp.setMoTa(rs.getString("moTa"));
		return sp;
	}
}

