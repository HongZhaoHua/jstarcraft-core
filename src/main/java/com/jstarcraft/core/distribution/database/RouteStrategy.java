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
	 * 选择索引
	 * 
	 * @return
	 */
	String chooseIndex(List<String> indexes);

}
