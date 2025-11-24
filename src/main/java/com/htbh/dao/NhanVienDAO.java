package com.htbh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.htbh.config.ConnectionFactory;
import com.htbh.model.NhanVien;

public class NhanVienDAO {
	public NhanVien createForThanhVien(int thanhVienId, String chiNhanh, Integer cuaHangId) {
		final String insert = "INSERT INTO tblNhanVien(chiNhanh, tblThanhVienid, tblCuaHangid) VALUES(?,?,?)";
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, chiNhanh);
			ps.setInt(2, thanhVienId);
			if (cuaHangId == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, cuaHangId);
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					NhanVien nv = new NhanVien();
					nv.setId(rs.getInt(1));
					nv.setChiNhanh(chiNhanh);
					nv.setThanhVienId(thanhVienId);
					nv.setCuaHangId(cuaHangId);
					return nv;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public NhanVien findByThanhVienId(int thanhVienId) {
		String sql = "SELECT id, chiNhanh, tblThanhVienid, tblCuaHangid FROM tblNhanVien WHERE tblThanhVienid = ?";
		try (Connection con = ConnectionFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, thanhVienId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					NhanVien nv = new NhanVien();
					nv.setId(rs.getInt("id"));
					nv.setChiNhanh(rs.getString("chiNhanh"));
					nv.setThanhVienId(rs.getInt("tblThanhVienid"));
					int cuaHangId = rs.getInt("tblCuaHangid");
					if (!rs.wasNull()) nv.setCuaHangId(cuaHangId);
					return nv;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
}


