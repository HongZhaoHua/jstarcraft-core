package com.jstarcraft.core.distribution.lock;

/**
 * 分布式任务
 * 
 * @author Birdy
 *
 */
public interface DistributionTask {

	/**
	 * 向前操作(相当于正常逻辑)
	 */
	void onForward();

	/**
	 * 向后操作(相当于异常逻辑,例如超时)
	 */
	void onBackward(Exception exception);

}
