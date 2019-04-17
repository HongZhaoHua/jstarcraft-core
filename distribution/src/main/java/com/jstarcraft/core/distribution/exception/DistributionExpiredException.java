package com.jstarcraft.core.distribution.exception;

/**
 * 分布式过期异常
 * 
 * @author Birdy
 */
public class DistributionExpiredException extends DistributionException {

	private static final long serialVersionUID = -4342551686681422269L;

	public DistributionExpiredException() {
		super();
	}

	public DistributionExpiredException(String message, Throwable exception) {
		super(message, exception);
	}

	public DistributionExpiredException(String message) {
		super(message);
	}

	public DistributionExpiredException(Throwable exception) {
		super(exception);
	}

}
