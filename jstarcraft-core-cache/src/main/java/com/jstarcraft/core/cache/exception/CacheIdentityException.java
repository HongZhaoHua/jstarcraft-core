package com.jstarcraft.core.cache.exception;

/**
 * 缓存标识异常
 * 
 * @author Birdy
 */
public class CacheIdentityException extends CacheException {

    private static final long serialVersionUID = 1622804717507686589L;

    public CacheIdentityException() {
        super();
    }

    public CacheIdentityException(String message, Throwable exception) {
        super(message, exception);
    }

    public CacheIdentityException(String message) {
        super(message);
    }

    public CacheIdentityException(Throwable exception) {
        super(exception);
    }
}
