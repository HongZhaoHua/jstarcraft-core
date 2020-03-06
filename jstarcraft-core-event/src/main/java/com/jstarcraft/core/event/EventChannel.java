package com.jstarcraft.core.event;

import java.util.Collection;
import java.util.Set;

/**
 * 事件通道
 * 
 * <pre>
 * 使用类名作为地址
 * </pre>
 * 
 * @author Birdy
 *
 */
public interface EventChannel {

    /**
     * 获取通道模式
     * 
     * @return
     */
    EventMode getMode();

    /**
     * 获取通道名称
     * 
     * @return
     */
    String getName();

    /**
     * 启动通道
     */
    void start();

    /**
     * 停止通道
     */
    void stop();

    /**
     * 获取指定地址的事件监控者
     * 
     * @param type
     * @return
     */
    Collection<EventMonitor> getMonitors(Class type);

    /**
     * 注册事件监控者
     * 
     * @param types
     * @param monitor
     * @return
     */
    void registerMonitor(Set<Class> types, EventMonitor monitor);

    /**
     * 注销事件监控者
     * 
     * @param types
     * @param monitor
     * @return
     */
    void unregisterMonitor(Set<Class> types, EventMonitor monitor);

    /**
     * 触发事件
     * 
     * @param event
     */
    void triggerEvent(Object event);

}
