package com.jstarcraft.core.monitor.trace;

/**
 * 基于ThreadStack的调用追踪器
 * 
 * @author Birdy
 *
 */
public class ThreadStackTracer implements Tracer {

    public static final int DEFAULT_OFFSET = 2;

    private final int offset;

    private final StackTraceElement[] context;

    public ThreadStackTracer() {
        this(DEFAULT_OFFSET + 1);
    }

    public ThreadStackTracer(int offset) {
        this.context = Thread.currentThread().getStackTrace();
        this.offset = offset;
    }

    @Override
    public String getClass(int index) {
        if (null != context && (offset + index) < context.length) {
            return context[offset + index].getClassName();
        }
        return null;
    }

    @Override
    public String getMethod(int index) {
        if (null != context && (offset + index) < context.length) {
            return context[offset + index].getMethodName();
        }
        return null;
    }

}
