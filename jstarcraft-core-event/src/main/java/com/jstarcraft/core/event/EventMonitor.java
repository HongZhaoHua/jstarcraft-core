package com.jstarcraft.core.event;

/**
 * 事件监控器
 * 
 * @author Birdy
 *
 */
public interface EventMonitor<T> {

    /**
     * 处理事件
     * 
     * @param event
     */
    void onEvent(T event);

}
