package com.htbh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

	public int insert(HopDong hopDong) {
		String sql = "INSERT INTO tblHopDong(soHopDong, tongVay, mucTraTruoc, laiSuat, duNoConLai, ngayKy, trangThai, tblKhachHangid, tblNhanVienid, tblSanPhamid, tblDoiTacid) "
			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, hopDong.getSoHopDong());
			ps.setBigDecimal(2, hopDong.getTongVay());
			ps.setBigDecimal(3, hopDong.getMucTraTruoc());
			ps.setBigDecimal(4, hopDong.getLaiSuat());
			ps.setBigDecimal(5, hopDong.getDuNoConLai());
			ps.setDate(6, hopDong.getNgayKy());
			ps.setString(7, hopDong.getTrangThai());
			ps.setInt(8, hopDong.getKhachHangId());
			if (hopDong.getNhanVienId() == null) {
				ps.setNull(9, java.sql.Types.INTEGER);
			} else {
				ps.setInt(9, hopDong.getNhanVienId());
			}
			if (hopDong.getSanPhamId() == null) {
				ps.setNull(10, java.sql.Types.INTEGER);
			} else {
				ps.setInt(10, hopDong.getSanPhamId());
			}
			if (hopDong.getDoiTacId() == null) {
				ps.setNull(11, java.sql.Types.INTEGER);
			} else {
				ps.setInt(11, hopDong.getDoiTacId());
			}
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		throw new IllegalStateException("Không tạo được hợp đồng");
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
		hd.setKhachHangId(rs.getInt("tblKhachHangid"));
		int nhanVienId = rs.getInt("tblNhanVienid");
		if (!rs.wasNull()) hd.setNhanVienId(nhanVienId);
		int sanPhamId = rs.getInt("tblSanPhamid");
		if (!rs.wasNull()) hd.setSanPhamId(sanPhamId);
		int doiTacId = rs.getInt("tblDoiTacid");
		if (!rs.wasNull()) hd.setDoiTacId(doiTacId);
		return hd;
	}
}


