package com.jstarcraft.core.monitor.trace;

import org.junit.Test;

public class SecurityManagerTracerTestCase extends TracerTestCase {

    protected Tracer getTracer() {
        SecurityManagerTracer tracer = new SecurityManagerTracer();
        return tracer;
    }

    @Test
    public void testMethod() {

    }

}
