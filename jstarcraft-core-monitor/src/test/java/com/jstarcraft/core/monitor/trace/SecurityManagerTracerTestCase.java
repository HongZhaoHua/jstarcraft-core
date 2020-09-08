package com.jstarcraft.core.monitor.trace;

import org.junit.Test;

public class SecurityManagerTracerTestCase extends CallTracerTestCase {

    protected CallTracer getTracer() {
        SecurityManagerTracer tracer = new SecurityManagerTracer();
        return tracer;
    }

    @Test
    public void testMethod() {

    }

}
