package com.jstarcraft.core.cache.transience;

/**
 * 内存监控器
 * 
 * @author Birdy
 */
public interface TransienceMonitor {

    /**
     * 交换通知
     * 
     * @param key
     * @param value
     */
    void notifyExchanged(Object key, Object value);

}
