package com.jstarcraft.core.cache;

import java.util.Collection;

import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 区域管理器
 * 
 * <pre>
 * 区域以索引为主,标识为辅,索引有且只有一个,提供索引->对象的查询
 * 注意:
 * 作为标识/索引的字段必须为Comparable;
 * 索引与标识一样不可变;索引不必保证唯一;
 * </pre>
 * 
 * @author Birdy
 *
 * @param <K>
 * @param <T>
 */
public interface RegionManager<K extends Comparable<K>, T extends IdentityObject<K>> extends CacheManager<K, T> {

    /**
     * 获取指定区域的缓存集合
     * 
     * @param index
     * @return
     */
    Collection<T> getInstances(CacheIndex index);

    /**
     * 获取指定区域的缓存集合
     * 
     * @param index
     * @param id
     * @return
     */
    T getInstance(CacheIndex index, K id);

    /**
     * 加载指定主键的缓存(半异步)
     * 
     * @param index
     * @param id
     * @param factory
     * @return
     */
    T loadInstance(CacheIndex index, K id, CacheObjectFactory<K, T> factory);

    /**
     * 创建指定对象的缓存
     * 
     * @param object
     * @return
     */
    T createInstance(T object);

    /**
     * 删除指定对象的缓存
     * 
     * @param object
     */
    void deleteInstance(T object);

    /**
     * 缓存指定集合的实例
     * 
     * @param index
     * @param instances
     * @return
     */
    Collection<T> cacheInstances(Collection<T> instances);

}
