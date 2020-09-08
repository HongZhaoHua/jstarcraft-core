package com.jstarcraft.core.monitor.trace;

public class ThrowableStackTracerTestCase extends CallTracerTestCase {

    protected CallTracer getTracer() {
        ThrowableStackTracer tracer = new ThrowableStackTracer();
        return tracer;
    }

}
