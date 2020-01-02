package com.jstarcraft.core.common.event;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public abstract class EventBusTestCase {

    protected abstract EventBus getEventBus();

    @Test(timeout = 5000)
    public void testTriggerEvent() throws Exception {
        int size = 10;
        MockMonitor monitor = new MockMonitor(size);
        Set<Class<?>> topics = new HashSet<>();
        topics.add(MockEvent.class);
        EventBus bus = getEventBus();
        bus.registerMonitor(monitor, topics);
        for (int index = 0; index < size; index++) {
            bus.triggerEvent(new MockEvent(index));
        }
        monitor.awaitLatch();
        Assert.assertEquals(10, monitor.getCount());
        bus.unregisterMonitor(monitor);
        for (int index = 0; index < size; index++) {
            bus.triggerEvent(new MockEvent(index));
        }
        Assert.assertEquals(10, monitor.getCount());
    }

}
