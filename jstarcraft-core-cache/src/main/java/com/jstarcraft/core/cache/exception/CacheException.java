package com.jstarcraft.core.cache.exception;

/**
 * 缓存异常
 * 
 * @author Birdy
 */
public class CacheException extends RuntimeException {

    private static final long serialVersionUID = 7555284510913520332L;

    public CacheException() {
        super();
    }

    public CacheException(String message, Throwable exception) {
        super(message, exception);
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(Throwable exception) {
        super(exception);
    }

}
