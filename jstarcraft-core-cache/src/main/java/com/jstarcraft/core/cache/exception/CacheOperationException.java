package com.jstarcraft.core.cache.exception;

/**
 * 缓存操作异常
 * 
 * @author Birdy
 */
public class CacheOperationException extends CacheException {

    private static final long serialVersionUID = 1622804717507686589L;

    public CacheOperationException() {
        super();
    }

    public CacheOperationException(String message, Throwable exception) {
        super(message, exception);
    }

    public CacheOperationException(String message) {
        super(message);
    }

    public CacheOperationException(Throwable exception) {
        super(exception);
    }
}
