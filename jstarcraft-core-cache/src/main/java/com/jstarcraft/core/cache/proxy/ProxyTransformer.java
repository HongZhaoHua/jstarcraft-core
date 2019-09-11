package com.jstarcraft.core.cache.proxy;

import com.jstarcraft.core.cache.exception.CacheProxyException;
import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 代理转换器
 * 
 * @author Birdy
 */
public interface ProxyTransformer {

    /**
     * 将指定缓存对象转换为代理对象
     * 
     * @param instance
     * @return
     * @throws CacheProxyException
     */
    <T extends IdentityObject<?>> T transform(T instance) throws CacheProxyException;

}
