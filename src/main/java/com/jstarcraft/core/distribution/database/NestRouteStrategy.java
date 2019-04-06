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

	/** 堆栈(后入先出) */
	private LinkedList<String> stack = new LinkedList<>();

	/**
	 * 推入索引
	 * 
	 * @param index
	 */
	public void pushIndex(String index) {
		stack.addLast(index);
	}

	/**
	 * 拉出索引
	 */
	public void pullIndex() {
		stack.removeLast();
	}

	@Override
	public String chooseIndex(List<String> indexes) {
		return stack.peekLast();
	}

}