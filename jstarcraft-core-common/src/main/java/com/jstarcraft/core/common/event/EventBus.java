package com.jstarcraft.core.common.event;

import java.util.Collection;
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
     * @return
     */
    boolean registerMonitor(EventMonitor monitor, Set<Class<?>> topics);

    /**
     * 注销事件监控者
     * 
     * @param monitor
     * @return
     */
    boolean unregisterMonitor(EventMonitor monitor);

    /**
     * 获取所有事件监控者
     * 
     * @return
     */
    Collection<EventMonitor> getMonitors();

    /**
     * 触发事件
     * 
     * @param event
     */
    void triggerEvent(Object event);

}
