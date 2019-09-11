package com.jstarcraft.core.cache;

import java.util.Map;

/**
 * 缓存监控器(JMX)
 * 
 * @author Birdy
 */
public interface CacheMonitor {

    /**
     * 获取缓存实例数量
     * 
     * @return
     */
    Map<String, Integer> getInstanceCounts();

    /**
     * 获取缓存索引数量
     * 
     * @return
     */
    Map<String, Map<String, Integer>> getIndexesCounts();

}
