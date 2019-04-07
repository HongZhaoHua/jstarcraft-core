package com.jstarcraft.core.distribution.database;

import java.util.List;

/**
 * 模仿路由策略
 * 
 * @author Birdy
 *
 */
public class MockRouteStrategy implements RouteStrategy {

	private String key;

	public MockRouteStrategy(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String chooseDataSource(List<String> keys) {
		return key;
	}

}
