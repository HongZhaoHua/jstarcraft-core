package com.jstarcraft.core.distribution.database;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询路由策略
 * 
 * @author Birdy
 *
 */
public class PollRouteStrategy implements RouteStrategy {

	/** 计数 */
	private AtomicInteger count = new AtomicInteger(0);

	@Override
	public String chooseIndex(List<String> indexes) {
		return indexes.get(Math.abs(count.getAndIncrement()) % indexes.size());
	}

}
