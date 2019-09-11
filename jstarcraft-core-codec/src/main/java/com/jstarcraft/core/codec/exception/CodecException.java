package com.jstarcraft.core.codec.exception;

/**
 * 编解码异常
 * 
 * @author Birdy
 */
public class CodecException extends RuntimeException {

    private static final long serialVersionUID = 7555284510913520332L;

    public CodecException() {
        super();
    }

    public CodecException(String message, Throwable exception) {
        super(message, exception);
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(Throwable exception) {
        super(exception);
    }

}
