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
}


