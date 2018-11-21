package com.digipro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

public abstract class BaseDao {

	private static GenericObjectPool gPool = null;
	private Properties properties;
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	private static final Log log = LogFactory.getLog(BaseDao.class);

	public BaseDao(Properties properties) {
		this.properties = properties;
		try {
			if (gPool == null)
				setUpPool();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void cleanup(ResultSet rs, Connection connObj, PreparedStatement stmt) {
		try {
			// Closing ResultSet Object
			if (rs != null) {
				rs.close();
			}
			// Closing PreparedStatement Object
			if (stmt != null) {
				stmt.close();
			}
			// Closing Connection Object
			if (connObj != null) {
				connObj.close();
			}
		} catch (Exception sqlException) {
			throw new RuntimeException(sqlException);
		}
	}

	@SuppressWarnings("unused")
	protected DataSource setUpPool() throws Exception {
		Class.forName(JDBC_DRIVER);

		// Creates an Instance of GenericObjectPool That Holds Our Pool of Connections
		// Object!
		gPool = new GenericObjectPool();
		gPool.setMaxActive(10);

		// Creates a ConnectionFactory Object Which Will Be Use by the Pool to Create
		// the Connection Object!
		ConnectionFactory cf = new DriverManagerConnectionFactory(properties.getProperty(Constants.PROP_DB_URL),
				properties.getProperty(Constants.PROP_DB_USER), properties.getProperty(Constants.PROP_DB_PASS));

		// Creates a PoolableConnectionFactory That Will Wraps the Connection Object
		// Created by the ConnectionFactory to Add Object Pooling Functionality!
		PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, gPool, null, null, false, true);
		return new PoolingDataSource(gPool);
	}

	protected DataSource getDS() {
		return new PoolingDataSource(gPool);
	}

	protected GenericObjectPool getConnectionPool() {
		return gPool;
	}

	// This Method Is Used To Print The Connection Pool Status
	protected void printDbStatus() {
		System.out.println("Max.: " + getConnectionPool().getMaxActive() + "; Active: "
				+ getConnectionPool().getNumActive() + "; Idle: " + getConnectionPool().getNumIdle());
	}
}
