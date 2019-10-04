package com.jstarcraft.core.monitor.trace;

import org.junit.Assert;
import org.junit.Test;

abstract public class TracerTestCase {

    abstract protected Tracer getTracer();

    @Test
    public void testClass() {
        Tracer tracer = getTracer();
        Assert.assertEquals(TracerTestCase.class.getName(), tracer.getCallerClass());
        Assert.assertEquals(this.getClass().getName(), tracer.getCalleeClass());
    }

    @Test
    public void testMethod() {
        Tracer tracer = getTracer();
        Assert.assertEquals("testMethod", tracer.getCallerMethod());
        Assert.assertEquals("getTracer", tracer.getCalleeMethod());
    }

}
