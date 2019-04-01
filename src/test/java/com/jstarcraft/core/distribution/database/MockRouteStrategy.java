package com.jstarcraft.core.distribution.database;

import java.util.Map;

import javax.sql.DataSource;

/**
 * 模仿路由策略
 * 
 * @author Birdy
 *
 */
public class MockRouteStrategy implements RouteStrategy {

	private String name;

	public MockRouteStrategy(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	@Override
	public DataSource chooseDataSource(Map<String, DataSource> dataSources) {
		return dataSources.get(name);
	}

}
