package com.jstarcraft.core.monitor.trace;

public class SecurityTracer extends SecurityManager implements Tracer {

    private static final int OFFSET = 1;

    private final Class<?>[] context = getClassContext();

    @Override
    public String getClass(int index) {
        if (null != context && (OFFSET + index) < context.length) {
            return context[OFFSET + index].getName();
        }
        return null;
    }

    @Override
    public String getMethod(int index) {
        throw new UnsupportedOperationException();
    }

}
