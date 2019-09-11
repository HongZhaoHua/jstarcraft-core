package com.jstarcraft.core.cache.exception;

/**
 * 缓存配置异常
 * 
 * @author Birdy
 */
public class CacheConfigurationException extends CacheException {

    private static final long serialVersionUID = -1669864425630854744L;

    public CacheConfigurationException() {
        super();
    }

    public CacheConfigurationException(String message, Throwable exception) {
        super(message, exception);
    }

    public CacheConfigurationException(String message) {
        super(message);
    }

    public CacheConfigurationException(Throwable exception) {
        super(exception);
    }

}
