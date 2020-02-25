package com.jstarcraft.core.storage.berkeley.exception;

import com.jstarcraft.core.storage.exception.StorageException;

/**
 * Berkeley版本异常(本质相当于乐观锁)
 * 
 * @author Birdy
 */
public class BerkeleyVersionException extends StorageException {

    private static final long serialVersionUID = 4228883407972897712L;

    public BerkeleyVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public BerkeleyVersionException(String message) {
        super(message);
    }

    public BerkeleyVersionException(Throwable cause) {
        super(cause);
    }

}
