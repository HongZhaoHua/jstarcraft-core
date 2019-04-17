package com.jstarcraft.core.codec.protocolbufferx.exception;

/**
 * 协议异常
 * 
 * @author Birdy
 */
public class ProtocolException extends RuntimeException {

	private static final long serialVersionUID = 4202415224023821472L;

	public ProtocolException() {
		super();
	}

	public ProtocolException(String message, Throwable exception) {
		super(message, exception);
	}

	public ProtocolException(String message) {
		super(message);
	}

	public ProtocolException(Throwable exception) {
		super(exception);
	}

}
