package com.jstarcraft.core.cache.persistence;

import java.util.List;
import java.util.Map;

import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 持久管理器
 * 
 * @author Birdy
 */
public interface PersistenceManager<K extends Comparable, T extends IdentityObject<K>> {

    /**
     * 获取实例
     * 
     * @param cacheId
     * @return
     */
    T getInstance(K cacheId);

    /**
     * 获取指定索引的主键映射
     * 
     * @param indexName
     * @param indexValue
     * @return
     */
    Map<K, Object> getIdentities(String indexName, Comparable indexValue);

    /**
     * 获取指定索引的实例集合
     * 
     * @param indexName
     * @param indexValue
     * @return
     */
    List<T> getInstances(String indexName, Comparable indexValue);

    /**
     * 创建实例
     * 
     * @param cacheObject
     * @return
     */
    PersistenceElement createInstance(IdentityObject<?> cacheObject);

    /**
     * 删除实例
     * 
     * @param cacheId
     * @return
     */
    PersistenceElement deleteInstance(Comparable cacheId);

    /**
     * 修改实例
     * 
     * @param cacheObject
     * @return
     */
    PersistenceElement updateInstance(IdentityObject<?> cacheObject);

    /**
     * 设置监控器
     * 
     * @param monitor
     */
    void setMonitor(PersistenceMonitor monitor);

    /**
     * 获取监听器
     * 
     * @return
     */
    PersistenceMonitor getMonitor();

    /**
     * 获取等待持久的元素数量
     * 
     * @return
     */
    int getWaitSize();

    /**
     * 获取已经创建记录数量
     * 
     * @return
     */
    long getCreatedCount();

    /**
     * 获取已经更新记录数量
     * 
     * @return
     */
    long getUpdatedCount();

    /**
     * 获取已经删除记录数量
     * 
     * @return
     */
    long getDeletedCount();

    /**
     * 获取异常次数
     * 
     * @return
     */
    long getExceptionCount();

}
