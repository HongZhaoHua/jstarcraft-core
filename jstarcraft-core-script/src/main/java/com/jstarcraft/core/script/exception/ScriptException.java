package com.jstarcraft.core.script.exception;

/**
 * 脚本异常
 * 
 * @author Birdy
 */
public class ScriptException extends RuntimeException {

    private static final long serialVersionUID = 4140004503951280660L;

    public ScriptException() {
        super();
    }

    public ScriptException(String message, Throwable exception) {
        super(message, exception);
    }

    public ScriptException(String message) {
        super(message);
    }

    public ScriptException(Throwable exception) {
        super(exception);
    }

}
