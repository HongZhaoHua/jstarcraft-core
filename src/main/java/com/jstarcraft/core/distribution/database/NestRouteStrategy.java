package com.jstarcraft.core.distribution.database;

import java.util.LinkedList;
import java.util.List;

/**
 * 嵌套路由策略
 * 
 * <pre>
 * 此策略配合切面一起使用
 * </pre>
 * 
 * @author Birdy
 *
 */
public class NestRouteStrategy implements RouteStrategy {

	/** 上下文 */
	private ThreadLocal<LinkedList<String>> contexts = new ThreadLocal<>();

	private LinkedList<String> getContext() {
		LinkedList<String> context = contexts.get();
		if (context == null) {
			context = new LinkedList<>();
			contexts.set(context);
		}
		return context;
	}

	/**
	 * 推入数据键
	 * 
	 * @param key
	 */
	public void pushKey(String key) {
		LinkedList<String> context = getContext();
		context.addLast(key);
	}

	/**
	 * 拉出数据键
	 */
	public void pullKey() {
		LinkedList<String> context = getContext();
		context.removeLast();
	}

	@Override
	public String chooseDataSource(List<String> keys) {
		LinkedList<String> context = getContext();
		return context.peekLast();
	}

}