package com.jstarcraft.core.monitor.trace;

public class ThrowableStackTracerTestCase extends TracerTestCase {

    protected Tracer getTracer() {
        ThrowableStackTracer tracer = new ThrowableStackTracer();
        return tracer;
    }

}
