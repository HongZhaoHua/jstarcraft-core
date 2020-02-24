package com.jstarcraft.core.event;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EventBusTestCase {

    protected static final Logger logger = LoggerFactory.getLogger(EventBusTestCase.class);

    protected abstract EventBus getEventBus(EventMode mode);

    @Test(timeout = 5000)
    public void testTriggerEventByQueue() throws Exception {
        int size = 10;
        Set<Class<?>> addresses = new HashSet<>();
        addresses.add(MockEvent.class);
        EventBus bus = getEventBus(EventMode.QUEUE);
        CountDownLatch latch = new CountDownLatch(size);
        MockMonitor[] monitors = new MockMonitor[size];
        for (int index = 0; index < size; index++) {
            monitors[index] = new MockMonitor(index, latch);
        }

        // 注册监控器
        for (int index = 0; index < size; index++) {
            MockMonitor monitor = monitors[index];
            bus.registerMonitor(addresses, monitor);
            Assert.assertTrue(bus.getMonitors(MockEvent.class).contains(monitor));
        }
        for (int index = 0; index < size; index++) {
            bus.triggerEvent(new MockEvent(index));
        }
        {
            latch.await();
            int count = 0;
            for (int index = 0; index < size; index++) {
                MockMonitor monitor = monitors[index];
                count += monitor.getCount();
            }
            Assert.assertEquals(10, count);
        }

        // 注销监控器
        for (int index = 0; index < size; index++) {
            MockMonitor monitor = monitors[index];
            bus.unregisterMonitor(addresses, monitor);
            Assert.assertFalse(bus.getMonitors(MockEvent.class).contains(monitor));
        }
        for (int index = 0; index < size; index++) {
            bus.triggerEvent(new MockEvent(index));
        }
        {
            latch.await();
            int count = 0;
            for (int index = 0; index < size; index++) {
                MockMonitor monitor = monitors[index];
                count += monitor.getCount();
            }
            Assert.assertEquals(10, count);
        }
    }

    @Test(timeout = 5000)
    public void testTriggerEventByTopic() throws Exception {
        int size = 10;
        Set<Class<?>> addresses = new HashSet<>();
        addresses.add(MockEvent.class);
        EventBus bus = getEventBus(EventMode.TOPIC);
        CountDownLatch latch = new CountDownLatch(size * size);
        MockMonitor[] monitors = new MockMonitor[size];
        for (int index = 0; index < size; index++) {
            monitors[index] = new MockMonitor(index, latch);
        }

        // 注册监控器
        for (int index = 0; index < size; index++) {
            MockMonitor monitor = monitors[index];
            bus.registerMonitor(addresses, monitor);
            Assert.assertTrue(bus.getMonitors(MockEvent.class).contains(monitor));
        }
        for (int index = 0; index < size; index++) {
            bus.triggerEvent(new MockEvent(index));
        }
        {
            latch.await();
            int count = 0;
            for (int index = 0; index < size; index++) {
                MockMonitor monitor = monitors[index];
                count += monitor.getCount();
            }
            Assert.assertEquals(100, count);
        }

        // 注销监控器
        for (int index = 0; index < size; index++) {
            MockMonitor monitor = monitors[index];
            bus.unregisterMonitor(addresses, monitor);
            Assert.assertFalse(bus.getMonitors(MockEvent.class).contains(monitor));
        }
        for (int index = 0; index < size; index++) {
            bus.triggerEvent(new MockEvent(index));
        }
        {
            latch.await();
            int count = 0;
            for (int index = 0; index < size; index++) {
                MockMonitor monitor = monitors[index];
                count += monitor.getCount();
            }
            Assert.assertEquals(100, count);
        }
    }

}
