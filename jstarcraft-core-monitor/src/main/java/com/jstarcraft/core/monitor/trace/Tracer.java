package com.jstarcraft.core.monitor.trace;

/**
 * 调用追踪器
 * 
 * @author Birdy
 *
 */
public interface Tracer {

    /**
     * 获取呼叫者类型
     * 
     * @return
     */
    String getCallerClass();

    /**
     * 获取呼叫者方法
     * 
     * @return
     */
    String getCallerMethod();

    /**
     * 获取呼叫者类型
     * 
     * @param index
     * @return
     */
    String getCallerClass(int index);

    /**
     * 获取呼叫者方法
     * 
     * @param index
     * @return
     */
    String getCallerMethod(int index);

    /**
     * 获取被叫者类型
     * 
     * @return
     */
    String getCalleeClass();

    /**
     * 获取被叫者方法
     * 
     * @return
     */
    String getCalleeMethod();

}
