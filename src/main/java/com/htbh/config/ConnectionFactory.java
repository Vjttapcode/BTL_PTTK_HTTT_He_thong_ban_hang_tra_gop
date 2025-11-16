package com.htbh.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/htbhtg?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
	private static final String JDBC_USER = "root"; // TODO: change to your MySQL user
	private static final String JDBC_PASS = "Bawkbak12@"; // TODO: change to your MySQL password

	private ConnectionFactory() {}

	static {
		try {
			// Ensure MySQL driver is registered even if the container doesn't auto-load JDBC drivers
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException ignored) {
			// Fallback to DriverManager auto-loading (JDBC 4+). If connector jar is missing, a SQLException will be thrown on getConnection.
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
	}
}


