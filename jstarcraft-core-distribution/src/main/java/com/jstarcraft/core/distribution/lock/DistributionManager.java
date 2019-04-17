package com.jstarcraft.core.distribution.lock;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.distribution.exception.DistributionException;
import com.jstarcraft.core.distribution.exception.DistributionExpiredException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 分布式管理器
 * 
 * @author Birdy
 *
 */
public abstract class DistributionManager {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 根据指定的配置执行任务
	 * 
	 * @param definition
	 * @param task
	 */
	public void execute(DistributionDefinition definition, DistributionTask task) {
		lock(definition);
		try {
			task.onForward();
			if (Instant.now().isAfter(definition.getMost())) {
				String message = StringUtility.format("根据指定的配置[{}]执行任务[{}]超时", definition, task);
				throw new DistributionExpiredException(message);
			}
		} catch (Exception exception) {
			String message = null;
			if (exception instanceof DistributionExpiredException) {
				message = exception.getMessage();
			} else {
				message = StringUtility.format("根据指定的配置[{}]执行任务[{}]Forward异常", definition, task);
				exception = new DistributionException(message, exception);
			}
			logger.error(message, exception);
			try {
				// 异常回滚
				task.onBackward(exception);
			} catch (Exception throwable) {
				message = StringUtility.format("根据指定的配置[{}]执行任务[{}]Backward异常", definition, task);
				exception = new DistributionException(message, throwable);
				logger.error(message, exception);
			}
			throw DistributionExpiredException.class.cast(exception);
		} finally {
			// TODO 思考解锁异常是否应该回滚任务?
			unlock(definition);
		}
	}

	/**
	 * 根据指定的定义加锁
	 * 
	 * <pre>
	 * 如果无法加锁,必须抛DistributionLockException
	 * </pre>
	 * 
	 * @param definition
	 */
	protected abstract void lock(DistributionDefinition definition);

	/**
	 * 根据指定的定义解锁
	 * 
	 * <pre>
	 * 如果无法解锁,必须抛DistributionUnlockException
	 * </pre>
	 * 
	 * @param definition
	 */
	protected abstract void unlock(DistributionDefinition definition);

}
