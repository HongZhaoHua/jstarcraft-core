package com.jstarcraft.core.cache.exception;

import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 缓存代理异常
 * 
 * @author Birdy
 */
public class CacheProxyException extends CacheException {

    private static final long serialVersionUID = -7670144308642956996L;

    /** 尝试代理的缓存对象 */
    private final IdentityObject<?> instance;

    public CacheProxyException(IdentityObject<?> instance, String message, Throwable exception) {
        super(message, exception);
        this.instance = instance;
    }

    public CacheProxyException(IdentityObject<?> instance, String message) {
        super(message);
        this.instance = instance;
    }

    public CacheProxyException(IdentityObject<?> instance, Throwable exception) {
        super(exception);
        this.instance = instance;
    }

    public CacheProxyException(IdentityObject<?> instance) {
        super();
        this.instance = instance;
    }

    public IdentityObject<?> getInstance() {
        return instance;
    }

}
