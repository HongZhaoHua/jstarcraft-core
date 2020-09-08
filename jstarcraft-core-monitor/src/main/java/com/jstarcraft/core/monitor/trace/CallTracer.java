package com.jstarcraft.core.monitor.trace;

/**
 * 调用追踪器
 * 
 * @author Birdy
 *
 */
public interface CallTracer {

    /**
     * 获取调用层数量
     * 
     * @return
     */
    int getCallLevels();

    /**
     * 获取指定层调用类型
     * 
     * @param level
     * @return
     */
    String getCallClass(int level);

    /**
     * 获取指定层调用方法
     * 
     * @param level
     * @return
     */
    String getCallMethod(int level);

    /**
     * 获取被叫者类型
     * 
     * @return
     */
    default String getCalleeClass() {
        return getCallClass(0);
    }

    /**
     * 获取被叫者方法
     * 
     * @return
     */
    default String getCalleeMethod() {
        return getCallMethod(0);
    }

    /**
     * 获取呼叫者类型
     * 
     * @return
     */
    default String getCallerClass() {
        return getCallClass(1);
    }

    /**
     * 获取呼叫者方法
     * 
     * @return
     */
    default String getCallerMethod() {
        return getCallMethod(1);
    }
}
