package com.jstarcraft.core.codec.exception;

/**
 * 协议定义异常
 * 
 * @author Birdy
 *
 */
public class CodecDefinitionException extends CodecException {

    private static final long serialVersionUID = 8408961463958421623L;

    public CodecDefinitionException() {
        super();
    }

    public CodecDefinitionException(String message, Throwable exception) {
        super(message, exception);
    }

    public CodecDefinitionException(String message) {
        super(message);
    }

    public CodecDefinitionException(Throwable exception) {
        super(exception);
    }

}
