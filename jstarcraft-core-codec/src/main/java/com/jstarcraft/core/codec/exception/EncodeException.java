package com.jstarcraft.core.codec.exception;

/**
 * 编码异常
 * 
 * @author Birdy
 *
 */
public class EncodeException extends CodecException {

	private static final long serialVersionUID = -7184761954271609066L;

	public EncodeException() {
		super();
	}

	public EncodeException(String message, Throwable exception) {
		super(message, exception);
	}

	public EncodeException(String message) {
		super(message);
	}

	public EncodeException(Throwable exception) {
		super(exception);
	}

}
