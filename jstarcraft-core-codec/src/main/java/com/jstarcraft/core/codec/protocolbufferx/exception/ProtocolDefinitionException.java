package com.jstarcraft.core.codec.protocolbufferx.exception;

/**
 * 协议定义异常
 * 
 * @author Birdy
 *
 */
public class ProtocolDefinitionException extends ProtocolException{
	
	private static final long serialVersionUID = 8408961463958421623L;

	public ProtocolDefinitionException() {
		super();
	}

	public ProtocolDefinitionException(String message, Throwable exception) {
		super(message, exception);
	}

	public ProtocolDefinitionException(String message) {
		super(message);
	}

	public ProtocolDefinitionException(Throwable exception) {
		super(exception);
	}
	
}
