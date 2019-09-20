package com.jstarcraft.core.common.event;

/**
 * 事件监控器
 * 
 * @author Birdy
 *
 */
public interface EventMonitor {

    /**
     * 处理事件
     * 
     * @param topic
     * @param event
     */
    void onEvent(Object event);

}
