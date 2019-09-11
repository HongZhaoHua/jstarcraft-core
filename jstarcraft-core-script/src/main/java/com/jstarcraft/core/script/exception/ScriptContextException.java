package com.jstarcraft.core.script.exception;

/**
 * 脚本上下文异常
 * 
 * @author Birdy
 */
public class ScriptContextException extends ScriptException {

    private static final long serialVersionUID = -6701901974455257392L;

    public ScriptContextException() {
        super();
    }

    public ScriptContextException(String message, Throwable exception) {
        super(message, exception);
    }

    public ScriptContextException(String message) {
        super(message);
    }

    public ScriptContextException(Throwable exception) {
        super(exception);
    }

}
