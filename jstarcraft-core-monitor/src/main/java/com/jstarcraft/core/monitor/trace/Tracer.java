package com.jstarcraft.core.monitor.trace;

/**
 * 调用追踪器
 * 
 * @author Birdy
 *
 */
public interface Tracer {

    /**
     * 获取指定层类型
     * 
     * @param index
     * @return
     */
    String getClass(int index);

    /**
     * 获取指定层方法
     * 
     * @param index
     * @return
     */
    String getMethod(int index);

    /**
     * 获取被叫者类型
     * 
     * @return
     */
    default String getCalleeClass() {
        return getClass(0);
    }

    /**
     * 获取被叫者方法
     * 
     * @return
     */
    default String getCalleeMethod() {
        return getMethod(0);
    }

    /**
     * 获取呼叫者类型
     * 
     * @return
     */
    default String getCallerClass() {
        return getClass(1);
    }

    /**
     * 获取呼叫者方法
     * 
     * @return
     */
    default String getCallerMethod() {
        return getMethod(1);
    }
}
