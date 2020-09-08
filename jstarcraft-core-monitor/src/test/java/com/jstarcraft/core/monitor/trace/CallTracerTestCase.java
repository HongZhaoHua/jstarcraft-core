package com.jstarcraft.core.monitor.trace;

import org.junit.Assert;
import org.junit.Test;

abstract public class CallTracerTestCase {

    abstract protected CallTracer getTracer();

    @Test
    public void testClass() {
        CallTracer tracer = getTracer();
        Assert.assertEquals(CallTracerTestCase.class.getName(), tracer.getCallerClass());
        Assert.assertEquals(this.getClass().getName(), tracer.getCalleeClass());
    }

    @Test
    public void testMethod() {
        CallTracer tracer = getTracer();
        Assert.assertEquals("testMethod", tracer.getCallerMethod());
        Assert.assertEquals("getTracer", tracer.getCalleeMethod());
    }

}
