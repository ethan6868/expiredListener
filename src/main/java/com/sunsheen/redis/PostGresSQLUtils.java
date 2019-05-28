package com.sunsheen.redis;

import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class PostGresSQLUtils {
	static HikariDataSource ds;
	
	private PostGresSQLUtils() {
	}
	
	public static HikariDataSource getDataSource(String path) {
		if(ds == null) {
			ds = new HikariDataSource(config(path));
		}
		return ds;
	}

	public static HikariConfig config(String path) {
		HikariConfig config = new HikariConfig(path);
		return config;
	}

	public static void main(String[] args) {
		Properties props = new Properties();
		// Examines both filesystem and classpath for .properties file
//		HikariConfig config = new HikariConfig("/database.properties");
		/*
		 * HikariDataSource dataSource = getDataSource(); try { Connection connection =
		 * dataSource.getConnection();
		 * 
		 * } catch (SQLException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}
}
