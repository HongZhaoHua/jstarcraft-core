package com.jstarcraft.core.storage.berkeley.exception;

import com.jstarcraft.core.storage.exception.StorageException;

/**
 * Berkeley备忘异常
 * 
 * @author Birdy
 *
 */
public class BerkeleyMemorandumException extends StorageException {

    private static final long serialVersionUID = 6634969038060172220L;

    public BerkeleyMemorandumException() {
        super();
    }

    public BerkeleyMemorandumException(String message, Throwable cause) {
        super(message, cause);
    }

    public BerkeleyMemorandumException(String message) {
        super(message);
    }

    public BerkeleyMemorandumException(Throwable cause) {
        super(cause);
    }

}