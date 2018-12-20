package com.jstarcraft.core.log.exception;

/**
 * 日志异常
 * 
 * @author Birdy
 */
public class LogException extends RuntimeException {

	private static final long serialVersionUID = -6807194955167819578L;

	public LogException() {
		super();
	}

	public LogException(String message, Throwable exception) {
		super(message, exception);
	}

	public LogException(String message) {
		super(message);
	}

	public LogException(Throwable exception) {
		super(exception);
	}

}
