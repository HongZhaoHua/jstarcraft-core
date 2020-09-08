package com.jstarcraft.core.monitor.trace;

/**
 * 基于ThreadStack的调用追踪器
 * 
 * @author Birdy
 *
 */
public class ThreadStackTracer implements CallTracer {

    public static final int DEFAULT_OFFSET = 2;

    private final int offset;

    private final StackTraceElement[] context;

    public ThreadStackTracer() {
        this(DEFAULT_OFFSET + 1);
    }

    public ThreadStackTracer(int level) {
        this.context = Thread.currentThread().getStackTrace();
        this.offset = level;
    }

    @Override
    public int getCallLevels() {
        return context.length - offset;
    }

    @Override
    public String getCallClass(int level) {
        if (null != context && (offset + level) < context.length) {
            return context[offset + level].getClassName();
        }
        return null;
    }

    @Override
    public String getCallMethod(int index) {
        if (null != context && (offset + index) < context.length) {
            return context[offset + index].getMethodName();
        }
        return null;
    }

}
