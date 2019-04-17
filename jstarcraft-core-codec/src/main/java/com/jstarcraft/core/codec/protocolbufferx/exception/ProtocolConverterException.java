package com.jstarcraft.core.codec.protocolbufferx.exception;

/**
 * 协议转换异常
 * 
 * @author Birdy
 */
public class ProtocolConverterException extends ProtocolException {

	private static final long serialVersionUID = -1098751050401171934L;

	public ProtocolConverterException() {
		super();
	}

	public ProtocolConverterException(String message, Throwable exception) {
		super(message, exception);
	}

	public ProtocolConverterException(String message) {
		super(message);
	}

	public ProtocolConverterException(Throwable exception) {
		super(exception);
	}
	
}
