package com.jstarcraft.core.monitor.trace;

/**
 * 基于ThrowableStack的调用追踪器
 * 
 * @author Birdy
 *
 */
public class ThrowableStackTracer implements CallTracer {

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
    public String getCallMethod(int level) {
        if (null != context && (offset + level) < context.length) {
            return context[offset + level].getMethodName();
        }
        return null;
    }

}
