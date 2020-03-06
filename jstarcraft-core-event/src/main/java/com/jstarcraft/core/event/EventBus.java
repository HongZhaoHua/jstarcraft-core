package com.jstarcraft.core.event;

import java.util.Collection;
import java.util.Set;

/**
 * 事件总线
 * 
 * <pre>
 * 使用类名作为地址
 * </pre>
 * 
 * @author Birdy
 *
 */
// TODO 考虑改名为EventChannel
public interface EventBus {

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
     * 获取指定地址的事件监控者
     * 
     * @param address
     * @return
     */
    Collection<EventMonitor> getMonitors(Class address);

    /**
     * 注册事件监控者
     * 
     * @param addresses
     * @param monitor
     * @return
     */
    void registerMonitor(Set<Class> addresses, EventMonitor monitor);

    /**
     * 注销事件监控者
     * 
     * @param addresses
     * @param monitor
     * @return
     */
    void unregisterMonitor(Set<Class> addresses, EventMonitor monitor);

    /**
     * 触发事件
     * 
     * @param event
     */
    void triggerEvent(Object event);

    /**
     * 启动通道
     */
    default void start() {
    }

    /**
     * 停止通道
     */
    default void stop() {
    }

}
