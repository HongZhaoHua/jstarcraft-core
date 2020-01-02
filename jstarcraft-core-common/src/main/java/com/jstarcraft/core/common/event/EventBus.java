package com.jstarcraft.core.common.event;

import java.util.Set;

/**
 * 事件总线
 * 
 * @author Birdy
 *
 */
public interface EventBus {

    /**
     * 注册事件监控者
     * 
     * @param monitor
     * @param topics
     */
    void registerMonitor(EventMonitor monitor, Set<Class<?>> topics);

    /**
     * 注销事件监控者
     * 
     * @param monitor
     */
    void unregisterMonitor(EventMonitor monitor);

    /**
     * 触发事件
     * 
     * @param event
     */
    void triggerEvent(Object event);

}
