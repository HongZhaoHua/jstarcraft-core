package com.jstarcraft.cloud.platform;

public class StorageException extends RuntimeException {

    private static final long serialVersionUID = 6526127733069513446L;

    public StorageException() {
        super();
    }

    public StorageException(String message, Throwable exception) {
        super(message, exception);
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(Throwable exception) {
        super(exception);
    }
}
