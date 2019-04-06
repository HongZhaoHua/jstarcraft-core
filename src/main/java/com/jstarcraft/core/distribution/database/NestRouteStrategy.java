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
		}
		return context;
	}

	/**
	 * 推入名称
	 * 
	 * @param name
	 */
	public void pushName(String name) {
		LinkedList<String> context = getContext();
		context.addLast(name);
	}

	/**
	 * 拉出名称
	 */
	public void pullName() {
		LinkedList<String> context = getContext();
		context.removeLast();
	}

	@Override
	public String chooseDataSource(List<String> names) {
		LinkedList<String> context = getContext();
		return context.peekLast();
	}

}