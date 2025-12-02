package com.htbh.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/htbhtg?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
	private static final String JDBC_USER = "root";
	private static final String JDBC_PASS = "Bawkbak12@";

	private ConnectionFactory() {}

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException ignored) {
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
	}
}


