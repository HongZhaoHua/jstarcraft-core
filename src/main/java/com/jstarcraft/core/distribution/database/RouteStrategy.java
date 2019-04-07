package com.jstarcraft.core.distribution.database;

import java.util.List;

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
	 * @param keys
	 * @return
	 */
	String chooseDataSource(List<String> keys);

}
