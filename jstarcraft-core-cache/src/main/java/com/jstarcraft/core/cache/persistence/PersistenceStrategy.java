package com.jstarcraft.core.cache.persistence;

import java.util.Map;

import com.jstarcraft.core.cache.CacheInformation;
import com.jstarcraft.core.storage.StorageAccessor;

/**
 * 持久策略
 * 
 * @author Birdy
 */
@SuppressWarnings("rawtypes")
public interface PersistenceStrategy {

    /**
     * 持久操作
     * 
     * @author Birdy
     */
    public enum PersistenceOperation {

        /** 插入 */
        CREATE,
        /** 更新 */
        UPDATE,
        /** 删除 */
        DELETE;

    }

    /**
     * 启动策略(策略需要保证有且仅调用一次)
     * 
     * @param accessor
     * @param configuration
     */
    void start(StorageAccessor accessor, Map<Class<?>, CacheInformation> informations);

    /**
     * 停止策略
     */
    void stop();

    /**
     * 获取名称
     * 
     * @return
     */
    String getName();

    /**
     * 获取管理器
     * 
     * @param monitor
     * @return
     */
    PersistenceManager getPersistenceManager(Class clazz);

}
