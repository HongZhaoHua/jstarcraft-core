package com.jstarcraft.core.cache.transience;

import java.util.concurrent.atomic.AtomicReference;

import com.jstarcraft.core.cache.CacheState;
import com.jstarcraft.core.cache.exception.CacheConfigurationException;

/**
 * 定时瞬时策略
 * 
 * @author Birdy
 *
 */
public class DelayedTransienceStrategy implements TransienceStrategy {

	/** 参数:到期时间(秒) */
	public static final String PARAMETER_EXPIRE = "expire";
	/** 参数:分段 */
	public static final String PARAMETER_SEGMENT = "segment";

	/** 名称 */
	private String name;

	/** 到期时间(秒) */
	private int expire;
	/** 分段 */
	private int segment;

	/** 状态 */
	private AtomicReference<CacheState> state = new AtomicReference<>(null);

	@Override
	public void start(TransienceConfiguration configuration) {
		if (!state.compareAndSet(null, CacheState.STARTED)) {
			throw new CacheConfigurationException();
		}
		this.name = configuration.getName();
		this.expire = Integer.parseInt(configuration.getValue(PARAMETER_EXPIRE));
		this.segment = Integer.parseInt(configuration.getValue(PARAMETER_SEGMENT));

		if (expire <= 0 || segment <= 1) {
			throw new CacheConfigurationException();
		}
	}

	@Override
	public synchronized void stop() {
		if (!state.compareAndSet(CacheState.STARTED, CacheState.STOPPED)) {
			throw new CacheConfigurationException();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TransienceManager getTransienceManager(TransienceMonitor monitor) {
		return new DelayedTransienceManager<>(expire, segment, monitor);
	}

}
