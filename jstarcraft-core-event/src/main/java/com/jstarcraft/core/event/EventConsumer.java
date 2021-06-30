package com.jstarcraft.core.event;

import java.util.Collection;
import java.util.Set;

public interface EventConsumer {

    /**
     * 获取名称
     * 
     * @return
     */
    String getName();

    /**
     * 启动消费者
     */
    void start();

    /**
     * 停止消费者
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

}
