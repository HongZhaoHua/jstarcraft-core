package com.jstarcraft.core.monitor.trace;

import org.junit.Test;

public class SecurityTracerTestCase extends TracerTestCase {

    protected Tracer getTracer() {
        SecurityTracer tracer = new SecurityTracer();
        return tracer;
    }

    @Test
    public void testMethod() {

    }

}
