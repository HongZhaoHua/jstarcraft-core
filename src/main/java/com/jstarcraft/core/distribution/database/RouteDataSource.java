package com.jstarcraft.core.distribution.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.AbstractDataSource;

/**
 * 路由数据源
 * 
 * @author Birdy
 *
 */
public class RouteDataSource extends AbstractDataSource {

	/** 路由策略 */
	private RouteStrategy strategy;

	/** 所有索引 */
	private List<String> indexes;

	/** 所有数据源 */
	private Map<String, DataSource> dataSources;

	public RouteDataSource(RouteStrategy strategy, HashMap<String, DataSource> dataSources) {
		this.strategy = strategy;
		this.indexes = new ArrayList<>(dataSources.keySet());
		this.dataSources = new HashMap<>(dataSources);
	}

	/**
	 * 根据策略切换数据源
	 * 
	 * @return
	 */
	protected DataSource switchDataSource() {
		String index = strategy.chooseIndex(indexes);
		return dataSources.get(index);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return switchDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return switchDataSource().getConnection(username, password);
	}

	@Override
	public <T> T unwrap(Class<T> clazz) throws SQLException {
		if (clazz.isInstance(this)) {
			return (T) this;
		}
		return switchDataSource().unwrap(clazz);
	}

	@Override
	public boolean isWrapperFor(Class<?> clazz) throws SQLException {
		return (clazz.isInstance(this) || switchDataSource().isWrapperFor(clazz));
	}
}