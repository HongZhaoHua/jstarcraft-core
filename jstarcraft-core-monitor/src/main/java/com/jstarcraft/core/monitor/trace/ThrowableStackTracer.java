package com.jstarcraft.core.monitor.trace;

public class ThrowableStackTracer implements Tracer {

    private static final int OFFSET = 1;

    private final StackTraceElement[] context;

    public ThrowableStackTracer() {
        this.context = new Throwable().getStackTrace();
    }

    @Override
    public String getClass(int index) {
        if (null != context && (OFFSET + index) < context.length) {
            return context[OFFSET + index].getClassName();
        }
        return null;
    }

    @Override
    public String getMethod(int index) {
        if (null != context && (OFFSET + index) < context.length) {
            return context[OFFSET + index].getMethodName();
        }
        return null;
    }

}
