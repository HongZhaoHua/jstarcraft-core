package com.jstarcraft.core.common.event;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public abstract class EventBusTestCase {

    protected abstract EventBus getEventBus();

    @Test
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
        monitor.await();
        bus.unregisterMonitor(monitor);
    }

}
