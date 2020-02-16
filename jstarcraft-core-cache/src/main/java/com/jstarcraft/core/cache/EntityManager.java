package com.jstarcraft.core.cache;

import java.util.Collection;

import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 实体管理器
 * 
 * <pre>
 * 实体以标识为主,索引为辅,索引数量不受限,提供索引->标识的查询
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
public interface EntityManager<K extends Comparable<K>, T extends IdentityObject<K>> extends CacheManager<K, T> {

    /**
     * 获取指定主键的缓存
     * 
     * @param id
     * @return
     */
    T getInstance(K id);

    /**
     * 加载指定主键的缓存
     * 
     * @param id
     * @param factory
     * @return
     */
    T loadInstance(K id, CacheObjectFactory<K, T> factory);

    /**
     * 删除指定主键的缓存
     * 
     * @param id
     * @return
     */
    T deleteInstance(K id);

    /**
     * 获取指定索引的缓存
     * 
     * @param index
     * @return
     */
    Collection<K> getIdentities(CacheIndex index);

    /**
     * 缓存指定集合的实例
     * 
     * @param instances
     * @return
     */
    Collection<T> cacheInstances(Collection<T> instances);

}
