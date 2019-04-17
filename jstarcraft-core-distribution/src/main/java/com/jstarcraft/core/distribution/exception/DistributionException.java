package com.jstarcraft.core.distribution.exception;

/**
 * 分布式异常
 * 
 * @author Birdy
 */
public class DistributionException extends RuntimeException {

	private static final long serialVersionUID = 5755685315477361860L;

	public DistributionException() {
		super();
	}

	public DistributionException(String message, Throwable exception) {
		super(message, exception);
	}

	public DistributionException(String message) {
		super(message);
	}

	public DistributionException(Throwable exception) {
		super(exception);
	}

}
