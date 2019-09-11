package com.jstarcraft.core.codec.exception;

/**
 * 协议转换异常
 * 
 * @author Birdy
 */
public class CodecConvertionException extends CodecException {

    private static final long serialVersionUID = -1098751050401171934L;

    public CodecConvertionException() {
        super();
    }

    public CodecConvertionException(String message, Throwable exception) {
        super(message, exception);
    }

    public CodecConvertionException(String message) {
        super(message);
    }

    public CodecConvertionException(Throwable exception) {
        super(exception);
    }

}
