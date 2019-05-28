package com.jstarcraft.core.distribution.exception;

import com.jstarcraft.core.distribution.resource.ResourceManager;

/**
 * 分布式加锁异常(必须由{@link ResourceManager}具体实现的lock方法使用)
 * 
 * @author Birdy
 */
public class DistributionLockException extends DistributionException {

	private static final long serialVersionUID = -8052326981691061048L;

	public DistributionLockException() {
		super();
	}

	public DistributionLockException(String message, Throwable exception) {
		super(message, exception);
	}

	public DistributionLockException(String message) {
		super(message);
	}

	public DistributionLockException(Throwable exception) {
		super(exception);
	}

}
