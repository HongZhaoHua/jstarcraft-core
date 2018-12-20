package com.jstarcraft.core.distribution.exception;

/**
 * 分布式任务异常
 * 
 * @author Birdy
 */
public class DistributionTaskException extends DistributionException {

	private static final long serialVersionUID = 2725404709235997067L;

	public DistributionTaskException() {
		super();
	}

	public DistributionTaskException(String message, Throwable exception) {
		super(message, exception);
	}

	public DistributionTaskException(String message) {
		super(message);
	}

	public DistributionTaskException(Throwable exception) {
		super(exception);
	}

}
