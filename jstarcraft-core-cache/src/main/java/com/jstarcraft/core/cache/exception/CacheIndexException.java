package com.jstarcraft.core.cache.exception;

/**
 * 缓存索引异常
 * 
 * @author Birdy
 */
public class CacheIndexException extends CacheException {

    private static final long serialVersionUID = 9191882390957957633L;

    public CacheIndexException() {
        super();
    }

    public CacheIndexException(String message, Throwable exception) {
        super(message, exception);
    }

    public CacheIndexException(String message) {
        super(message);
    }

    public CacheIndexException(Throwable exception) {
        super(exception);
    }

}
