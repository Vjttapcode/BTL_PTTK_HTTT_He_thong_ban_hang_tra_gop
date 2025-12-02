package com.htbh.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.htbh.config.ConnectionFactory;
import com.htbh.model.KhachHang;
import com.htbh.model.ThanhVien;

/**
 * DAO cho thống kê dư nợ
 * Theo sơ đồ thiết kế, class này kế thừa từ DAO (có Connection con)
 */
public class TKDuNoDAO {
	
	/**
	 * Kiểm tra và trả về danh sách khách hàng có tổng dư nợ >= ngưỡng dư nợ
	 * @param nguongDuNo Ngưỡng dư nợ (BigDecimal)
	 * @return Mảng KhachHang[] hoặc null nếu không tìm thấy
	 */
	public KhachHang[] checkDuNo(BigDecimal nguongDuNo) {
		if (nguongDuNo == null) {
			return null;
		}
		
		List<KhachHang> list = new ArrayList<>();
		
		// Query để tìm khách hàng có tổng dư nợ >= ngưỡng
		// Tổng dư nợ = SUM(duNoConLai) từ tất cả hợp đồng của khách hàng
		String sql = "SELECT kh.id, kh.cccd, kh.tblThanhVienid, " +
			"tv.ten, tv.ngaySinh, tv.sdt, tv.email, tv.diaChi, " +
			"COALESCE(SUM(hd.duNoConLai), 0) AS tongDuNo " +
			"FROM tblKhachHang kh " +
			"JOIN tblThanhVien tv ON kh.tblThanhVienid = tv.id " +
			"LEFT JOIN tblHopDong hd ON kh.id = hd.tblKhachHang	id " +
			"GROUP BY kh.id, kh.cccd, kh.tblThanhVienid, tv.ten, tv.ngaySinh, tv.sdt, tv.email, tv.diaChi " +
			"HAVING tongDuNo <= ? " +
			"ORDER BY tongDuNo DESC";
		
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setBigDecimal(1, nguongDuNo);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					KhachHang kh = map(rs);
					list.add(kh);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Lỗi khi truy vấn thống kê dư nợ: " + e.getMessage(), e);
		}
		
		if (list.isEmpty()) {
			return null;
		}
		
		return list.toArray(new KhachHang[0]);
	}
	
	/**
	 * Map ResultSet sang KhachHang object
	 */
	private KhachHang map(ResultSet rs) throws SQLException {
		KhachHang kh = new KhachHang();
		kh.setId(rs.getInt("id"));
		kh.setThanhVienId(rs.getInt("tblThanhVienid"));
		kh.setCccd(rs.getString("cccd"));
		
		// Lưu tổng dư nợ
		BigDecimal tongDuNo = rs.getBigDecimal("tongDuNo");
		if (tongDuNo == null) {
			tongDuNo = BigDecimal.ZERO;
		}
		kh.setTongDuNo(tongDuNo);
		
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

