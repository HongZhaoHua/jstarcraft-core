package com.jstarcraft.core.cache.proxy;

import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 代理管理器
 * 
 * <pre>
 * 专门用于提供方法给代理对象调用
 * </pre>
 * 
 * @author Birdy
 *
 * @param <K>
 * @param <T>
 */
public interface ProxyManager<K extends Comparable, T extends IdentityObject<K>> {

    /**
     * 修改指定缓存对象的数据
     * 
     * @param id
     * @param instance
     */
    void modifyInstance(T instance);

}
