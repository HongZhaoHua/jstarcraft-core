package com.jstarcraft.core.storage.berkeley.exception;

import com.jstarcraft.core.storage.exception.StorageException;

public class BerkeleyOperationException extends StorageException {

    private static final long serialVersionUID = -3668430093940319385L;

    public BerkeleyOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BerkeleyOperationException(String message) {
        super(message);
    }

    public BerkeleyOperationException(Throwable cause) {
        super(cause);
    }

}