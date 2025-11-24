package com.htbh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.htbh.config.ConnectionFactory;
import com.htbh.model.DoiTac;

public class DoiTacDAO {

	private static final String BASE_SELECT = "SELECT id, tenDoiTac, maSoThue, loaiHinhToChuc, mucTraTruocToiThieu FROM tblDoiTac ";

	public List<DoiTac> search(String keyword) {
		List<DoiTac> list = new ArrayList<>();
		StringBuilder sql = new StringBuilder(BASE_SELECT);
		boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
		if (hasKeyword) {
			sql.append("WHERE tenDoiTac LIKE ? OR maSoThue LIKE ? ");
		}
		sql.append("ORDER BY tenDoiTac");
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql.toString())) {
			if (hasKeyword) {
				String like = "%" + keyword.trim() + "%";
				ps.setString(1, like);
				ps.setString(2, like);
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

	public DoiTac findById(int id) {
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

	private DoiTac map(ResultSet rs) throws SQLException {
		DoiTac dt = new DoiTac();
		dt.setId(rs.getInt("id"));
		dt.setTenDoiTac(rs.getString("tenDoiTac"));
		dt.setMaSoThue(rs.getString("maSoThue"));
		dt.setLoaiHinhToChuc(rs.getString("loaiHinhToChuc"));
		dt.setMucTraTruocToiThieu(rs.getBigDecimal("mucTraTruocToiThieu"));
		return dt;
	}
}

