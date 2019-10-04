package com.jstarcraft.core.monitor.trace;

public class ThreadStackTracer implements Tracer {

    private static final int OFFSET = 2;

    private final StackTraceElement[] context;

    public ThreadStackTracer() {
        this.context = Thread.currentThread().getStackTrace();
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
