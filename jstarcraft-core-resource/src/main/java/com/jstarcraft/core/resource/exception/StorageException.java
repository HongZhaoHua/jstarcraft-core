package com.jstarcraft.core.resource.exception;

/**
 * 仓储异常
 * 
 * @author Birdy
 *
 */
public class StorageException extends RuntimeException {

    private static final long serialVersionUID = 4013433856373480929L;

    public StorageException() {
        super();
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(Throwable cause) {
        super(cause);
    }

}
