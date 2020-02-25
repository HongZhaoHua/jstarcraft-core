package com.jstarcraft.core.storage.exception;

/**
 * ORM查询异常
 * 
 * @author Birdy
 */
public class StorageQueryException extends StorageException {

    private static final long serialVersionUID = -1710027252202582783L;

    public StorageQueryException() {
        super();
    }

    public StorageQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageQueryException(String message) {
        super(message);
    }

    public StorageQueryException(Throwable cause) {
        super(cause);
    }

}
