package com.jstarcraft.core.storage.berkeley.exception;

import com.jstarcraft.core.storage.exception.StorageException;

/**
 * Berkeley状态异常
 * 
 * @author Birdy
 *
 */
public class BerkeleyStateException extends StorageException {

    private static final long serialVersionUID = -466786536849658568L;

    public BerkeleyStateException() {
        super();
    }

    public BerkeleyStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BerkeleyStateException(String message) {
        super(message);
    }

    public BerkeleyStateException(Throwable cause) {
        super(cause);
    }

}
