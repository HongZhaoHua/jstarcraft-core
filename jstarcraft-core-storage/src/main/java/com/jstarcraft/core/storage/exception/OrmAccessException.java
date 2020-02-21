package com.jstarcraft.core.storage.exception;

/**
 * ORM存取异常
 * 
 * @author Birdy
 */
public class OrmAccessException extends OrmException {

    private static final long serialVersionUID = -8396525701135532677L;

    public OrmAccessException() {
        super();
    }

    public OrmAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmAccessException(String message) {
        super(message);
    }

    public OrmAccessException(Throwable cause) {
        super(cause);
    }

}
