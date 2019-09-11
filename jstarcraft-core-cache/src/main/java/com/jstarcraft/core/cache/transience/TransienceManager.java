package com.jstarcraft.core.cache.transience;

/**
 * 内存管理器
 * 
 * @author Birdy
 *
 * @param <K>
 * @param <T>
 */
public interface TransienceManager<K, T> {

    /**
     * 创建实例
     * 
     * @param id
     * @param instance
     */
    void createInstance(K id, T instance);

    /**
     * 删除实例
     * 
     * @param id
     * @return
     */
    T deleteInstance(K id);

    /**
     * 检索实例
     * 
     * @param id
     * @return
     */
    T retrieveInstance(K id);

    /**
     * 获取大小
     * 
     * @return
     */
    int getSize();

}
