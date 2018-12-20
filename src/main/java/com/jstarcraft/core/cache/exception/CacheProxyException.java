package com.jstarcraft.core.cache.exception;

import com.jstarcraft.core.cache.CacheObject;

/**
 * 缓存代理异常
 * 
 * @author Birdy
 */
public class CacheProxyException extends CacheException {

	private static final long serialVersionUID = -7670144308642956996L;

	/** 尝试代理的缓存对象 */
	private final CacheObject<?> instance;

	public CacheProxyException(CacheObject<?> instance, String message, Throwable exception) {
		super(message, exception);
		this.instance = instance;
	}

	public CacheProxyException(CacheObject<?> instance, String message) {
		super(message);
		this.instance = instance;
	}

	public CacheProxyException(CacheObject<?> instance, Throwable exception) {
		super(exception);
		this.instance = instance;
	}

	public CacheProxyException(CacheObject<?> instance) {
		super();
		this.instance = instance;
	}

	public CacheObject<?> getInstance() {
		return instance;
	}

}
