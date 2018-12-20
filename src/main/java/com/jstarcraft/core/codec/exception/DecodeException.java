package com.jstarcraft.core.codec.exception;

/**
 * 解码异常
 * 
 * @author Birdy
 *
 */
public class DecodeException extends CodecException {

	private static final long serialVersionUID = -7937731216011169585L;

	public DecodeException() {
		super();
	}

	public DecodeException(String message, Throwable exception) {
		super(message, exception);
	}

	public DecodeException(String message) {
		super(message);
	}

	public DecodeException(Throwable exception) {
		super(exception);
	}

}
