package com.jstarcraft.core.script.exception;

/**
 * 脚本表达式异常
 * 
 * @author Birdy
 */
public class ScriptExpressionException extends ScriptException {

    private static final long serialVersionUID = 5627673680043115031L;

    public ScriptExpressionException() {
        super();
    }

    public ScriptExpressionException(String message, Throwable exception) {
        super(message, exception);
    }

    public ScriptExpressionException(String message) {
        super(message);
    }

    public ScriptExpressionException(Throwable exception) {
        super(exception);
    }

}
