package com.jstarcraft.core.storage.exception;

/**
 * ORM查询异常
 * 
 * @author Birdy
 */
public class OrmQueryException extends OrmException {

    private static final long serialVersionUID = -1710027252202582783L;

    public OrmQueryException() {
        super();
    }

    public OrmQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmQueryException(String message) {
        super(message);
    }

    public OrmQueryException(Throwable cause) {
        super(cause);
    }

}
