package com.jstarcraft.core.monitor.trace;

/**
 * 基于SecurityManager的调用追踪器
 * 
 * @author Birdy
 *
 */
public class SecurityManagerTracer extends SecurityManager implements CallTracer {

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
    public int getCallLevels() {
        return context.length - offset;
    }

    @Override
    public String getCallClass(int level) {
        if (null != context && (offset + level) < context.length) {
            return context[offset + level].getName();
        }
        return null;
    }

    @Override
    public String getCallMethod(int level) {
        throw new UnsupportedOperationException();
    }

}
