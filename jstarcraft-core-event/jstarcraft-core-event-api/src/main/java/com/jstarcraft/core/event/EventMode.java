package com.jstarcraft.core.event;

/**
 * 事件模式
 * 
 * @author Birdy
 *
 */
public enum EventMode {

    /** 队列模式(点对点) */
    QUEUE,

    /** 主题模式(订阅发布) */
    TOPIC;

}
