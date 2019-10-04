package com.jstarcraft.core.monitor.trace;

public class ThreadStackTracerTestCase extends TracerTestCase {

    protected Tracer getTracer() {
        ThreadStackTracer tracer = new ThreadStackTracer();
        return tracer;
    }

}
