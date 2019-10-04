package com.jstarcraft.core.monitor.trace;

/**
 * 基于ThrowableStack的调用追踪器
 * 
 * @author Birdy
 *
 */
public class ThrowableStackTracer implements Tracer {

    public static final int DEFAULT_OFFSET = 1;

    private final StackTraceElement[] context;

    private final int offset;

    public ThrowableStackTracer() {
        this(DEFAULT_OFFSET + 1);
    }

    public ThrowableStackTracer(int offset) {
        this.context = new Throwable().getStackTrace();
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
