package com.jstarcraft.core.distribution.database;

import java.util.List;

import com.jstarcraft.core.utility.RandomUtility;

/**
 * 随机路由策略
 * 
 * @author Birdy
 *
 */
public class RandomRouteStrategy implements RouteStrategy {

	@Override
	public String chooseIndex(List<String> indexes) {
		return indexes.get(RandomUtility.randomInteger(indexes.size()));
	}

}