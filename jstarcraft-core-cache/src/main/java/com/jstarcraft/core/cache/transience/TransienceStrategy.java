package com.jstarcraft.core.cache.transience;

/**
 * 内存策略
 * 
 * <pre>
 * 配合{@link TransienceType}实现内存控制.
 * </pre>
 * 
 * @author Birdy
 *
 */
public interface TransienceStrategy {

	/**
	 * 策略类型
	 * 
	 * @author Birdy
	 */
	public enum TransienceType {

		/** 使用到期策略管理内存 */
		DELAYED,

		/** 使用最近最少使用策略管理内存 */
		LEAST_RECENTLY_USED,

		/** 使用自定义策略管理内存 */
		USER_DEFINED,

	}

	/**
	 * 启动(策略需要保证有且仅调用一次)
	 * 
	 * @param configuration
	 */
	void start(TransienceConfiguration configuration);

	/**
	 * 关闭
	 */
	void stop();

	/**
	 * 获取名称
	 */
	String getName();

	/**
	 * 获取内存管理器
	 * 
	 * @param monitor
	 * @return
	 */
	TransienceManager getTransienceManager(TransienceMonitor monitor);

}
