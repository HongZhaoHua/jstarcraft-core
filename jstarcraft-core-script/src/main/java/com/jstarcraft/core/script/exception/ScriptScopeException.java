package com.jstarcraft.core.script.exception;

/**
 * 脚本范围异常
 * 
 * @author Birdy
 */
public class ScriptScopeException extends ScriptException {

    private static final long serialVersionUID = 4573879251762714484L;

    public ScriptScopeException() {
        super();
    }

    public ScriptScopeException(String message, Throwable exception) {
        super(message, exception);
    }

    public ScriptScopeException(String message) {
        super(message);
    }

    public ScriptScopeException(Throwable exception) {
        super(exception);
    }

}
