package com.htbh.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Types;

import com.htbh.config.ConnectionFactory;
import com.htbh.model.KyThanhToan;

public class KyThanhToanDAO {

	private static final String BASE_SELECT = "SELECT id, tblHopDongid, tenKy, ngayDenHan, tienGoc, tienLai, trangThai FROM tblKyThanhToan ";

	public List<KyThanhToan> findByHopDongId(int hopDongId) {
		List<KyThanhToan> list = new ArrayList<>();
		String sql = BASE_SELECT + "WHERE tblHopDongid = ? ORDER BY ngayDenHan";
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, hopDongId);
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

	public void insertBatch(int hopDongId, List<KyThanhToan> schedule) {
		if (schedule == null || schedule.isEmpty()) return;
		String sql = "INSERT INTO tblKyThanhToan(tblHopDongid, tenKy, ngayDenHan, tienGoc, tienLai, trangThai) VALUES (?,?,?,?,?,?)";
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql)) {
			for (KyThanhToan ky : schedule) {
				ps.setInt(1, hopDongId);
				ps.setString(2, ky.getTenKy());
				Date ngayDenHan = ky.getNgayDenHan();
				if (ngayDenHan == null) {
					ps.setDate(3, null);
				} else {
					ps.setDate(3, ngayDenHan);
				}
				if (ky.getTienGoc() == null) {
					ps.setNull(4, Types.DECIMAL);
				} else {
					ps.setBigDecimal(4, ky.getTienGoc());
				}
				if (ky.getTienLai() == null) {
					ps.setNull(5, Types.DECIMAL);
				} else {
					ps.setBigDecimal(5, ky.getTienLai());
				}
				ps.setString(6, ky.getTrangThai());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private KyThanhToan map(ResultSet rs) throws SQLException {
		KyThanhToan ky = new KyThanhToan();
		ky.setId(rs.getInt("id"));
		ky.setHopDongId(rs.getInt("tblHopDongid"));
		ky.setTenKy(rs.getString("tenKy"));
		ky.setNgayDenHan(rs.getDate("ngayDenHan"));
		ky.setTienGoc(rs.getBigDecimal("tienGoc"));
		ky.setTienLai(rs.getBigDecimal("tienLai"));
		ky.setTrangThai(rs.getString("trangThai"));
		return ky;
	}
}

