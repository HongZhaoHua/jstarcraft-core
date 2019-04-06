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
	 * 推入索引
	 * 
	 * @param index
	 */
	public void pushIndex(String index) {
		LinkedList<String> context = getContext();
		context.addLast(index);
	}

	/**
	 * 拉出索引
	 */
	public void pullIndex() {
		LinkedList<String> context = getContext();
		context.removeLast();
	}

	@Override
	public String chooseIndex(List<String> indexes) {
		LinkedList<String> context = getContext();
		return context.peekLast();
	}

}