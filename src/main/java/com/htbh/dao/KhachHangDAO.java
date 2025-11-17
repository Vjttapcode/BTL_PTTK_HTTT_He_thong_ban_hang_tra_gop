package com.htbh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.htbh.config.ConnectionFactory;
import com.htbh.model.KhachHang;
import com.htbh.model.ThanhVien;

public class KhachHangDAO {

	private static final String BASE_SELECT = "SELECT kh.id, kh.cccd, kh.tblThanhVienid, tv.ten, tv.ngaySinh, tv.sdt, tv.email, tv.diaChi " +
		"FROM tblKhachHang kh JOIN tblThanhVien tv ON kh.tblThanhVienid = tv.id ";

	public List<KhachHang> searchByKeyword(String keyword) {
		List<KhachHang> list = new ArrayList<>();
		String sql = BASE_SELECT + "WHERE tv.ten LIKE ? OR kh.cccd LIKE ? ORDER BY tv.ten";
		String like = "%" + keyword + "%";
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, like);
			ps.setString(2, like);
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

	public KhachHang findById(int id) {
		String sql = BASE_SELECT + "WHERE kh.id = ?";
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

	private KhachHang map(ResultSet rs) throws SQLException {
		KhachHang kh = new KhachHang();
		kh.setId(rs.getInt("id"));
		kh.setThanhVienId(rs.getInt("tblThanhVienid"));
		kh.setCccd(rs.getString("cccd"));
		ThanhVien tv = new ThanhVien();
		tv.setId(rs.getInt("tblThanhVienid"));
		tv.setTen(rs.getString("ten"));
		tv.setNgaySinh(rs.getDate("ngaySinh"));
		tv.setSdt(rs.getString("sdt"));
		tv.setEmail(rs.getString("email"));
		tv.setDiaChi(rs.getString("diaChi"));
		kh.setProfile(tv);
		return kh;
	}
}


