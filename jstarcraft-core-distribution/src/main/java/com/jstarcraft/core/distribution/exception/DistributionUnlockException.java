package com.jstarcraft.core.distribution.exception;

import com.jstarcraft.core.distribution.resource.ResourceManager;

/**
 * 分布式解锁异常(必须由{@link ResourceManager}具体实现的unlock方法使用)
 * 
 * @author Birdy
 */
public class DistributionUnlockException extends DistributionException {

	private static final long serialVersionUID = -4971616622290538397L;

	public DistributionUnlockException() {
		super();
	}

	public DistributionUnlockException(String message, Throwable exception) {
		super(message, exception);
	}

	public DistributionUnlockException(String message) {
		super(message);
	}

	public DistributionUnlockException(Throwable exception) {
		super(exception);
	}

}
