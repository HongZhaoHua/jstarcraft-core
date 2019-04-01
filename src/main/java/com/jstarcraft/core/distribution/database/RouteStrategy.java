package com.jstarcraft.core.distribution.database;

import java.util.Map;

import javax.sql.DataSource;

/**
 * 路由策略
 * 
 * @author Birdy
 *
 */
public interface RouteStrategy {

	/**
	 * 选择数据源
	 * 
	 * @return
	 */
	DataSource chooseDataSource(Map<String, DataSource> dataSources);

}
