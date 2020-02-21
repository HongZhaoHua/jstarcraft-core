package com.jstarcraft.core.orm.exception;

/**
 * ORM异常
 * 
 * @author Birdy
 */
public class OrmException extends RuntimeException {

    private static final long serialVersionUID = -5480573723994246089L;

    public OrmException() {
        super();
    }

    public OrmException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmException(String message) {
        super(message);
    }

    public OrmException(Throwable cause) {
        super(cause);
    }

}
