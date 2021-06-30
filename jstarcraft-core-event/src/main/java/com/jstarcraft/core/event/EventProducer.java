package com.jstarcraft.core.event;

public interface EventProducer {

    /**
     * 获取名称
     * 
     * @return
     */
    String getName();

    /**
     * 启动生产者
     */
    void start();

    /**
     * 停止生产者
     */
    void stop();

    /**
     * 触发事件
     * 
     * @param event
     */
    void triggerEvent(Object event);

}
