package com.jstarcraft.core.monitor.trace;

/**
 * 基于SecurityManager的调用追踪器
 * 
 * @author Birdy
 *
 */
public class SecurityManagerTracer extends SecurityManager implements Tracer {

    public static final int DEFAULT_OFFSET = 1;

    private final Class<?>[] context;

    private final int offset;

    public SecurityManagerTracer() {
        this(DEFAULT_OFFSET + 1);
    }

    public SecurityManagerTracer(int offset) {
        this.context = getClassContext();
        this.offset = offset;
    }

    @Override
    public String getClass(int index) {
        if (null != context && (offset + index) < context.length) {
            return context[offset + index].getName();
        }
        return null;
    }

    @Override
    public String getMethod(int index) {
        throw new UnsupportedOperationException();
    }

}
