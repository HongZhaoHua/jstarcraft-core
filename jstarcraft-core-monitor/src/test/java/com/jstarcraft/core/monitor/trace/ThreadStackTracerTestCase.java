package com.jstarcraft.core.monitor.trace;

public class ThreadStackTracerTestCase extends CallTracerTestCase {

    protected CallTracer getTracer() {
        ThreadStackTracer tracer = new ThreadStackTracer();
        return tracer;
    }

}
