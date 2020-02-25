package com.jstarcraft.core.storage.exception;

/**
 * ORM存取异常
 * 
 * @author Birdy
 */
public class StorageAccessException extends StorageException {

    private static final long serialVersionUID = -8396525701135532677L;

    public StorageAccessException() {
        super();
    }

    public StorageAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageAccessException(String message) {
        super(message);
    }

    public StorageAccessException(Throwable cause) {
        super(cause);
    }

}
