package com.htbh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.htbh.config.ConnectionFactory;
import com.htbh.model.HopDong;

public class HopDongDAO {
	public List<HopDong> findByKhachHangId(int khachHangId) {
		List<HopDong> list = new ArrayList<>();
		String sql = "SELECT * FROM tblHopDong WHERE tblKhachHangid = ? ORDER BY ngayKy DESC";
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, khachHangId);
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

	private HopDong map(ResultSet rs) throws SQLException {
		HopDong hd = new HopDong();
		hd.setId(rs.getInt("id"));
		hd.setSoHopDong(rs.getString("soHopDong"));
		hd.setTongVay(rs.getBigDecimal("tongVay"));
		hd.setMucTraTruoc(rs.getBigDecimal("mucTraTruoc"));
		hd.setLaiSuat(rs.getBigDecimal("laiSuat"));
		hd.setDuNoConLai(rs.getBigDecimal("duNoConLai"));
		hd.setNgayKy(rs.getDate("ngayKy"));
		hd.setTrangThai(rs.getString("trangThai"));
		return hd;
	}
}


