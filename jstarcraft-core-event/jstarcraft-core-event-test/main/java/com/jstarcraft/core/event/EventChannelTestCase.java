package com.jstarcraft.core.event;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EventChannelTestCase {

    protected static final Logger logger = LoggerFactory.getLogger(EventChannelTestCase.class);

    protected abstract EventChannel getEventChannel(EventMode mode);

    @Test
    public void testTriggerQueueEvent() throws Exception {
        int size = 10;
        Set<Class> addresses = new HashSet<>();
        addresses.add(MockUnicastEvent.class);
        EventChannel channel = getEventChannel(EventMode.QUEUE);
        Assert.assertEquals(EventMode.QUEUE, channel.getMode());
        Semaphore semaphore = new Semaphore(0);
        MockUnicastMonitor[] monitors = new MockUnicastMonitor[size];
        for (int index = 0; index < size; index++) {
            monitors[index] = new MockUnicastMonitor(index, semaphore);
        }

        try {
            channel.start();
            // 注册监控器
            for (int index = 0; index < size; index++) {
                MockUnicastMonitor monitor = monitors[index];
                channel.registerMonitor(addresses, monitor);
                Assert.assertTrue(channel.getMonitors(MockUnicastEvent.class).contains(monitor));
            }
            // 触发事件
            for (int index = 0; index < size; index++) {
                channel.triggerEvent(new MockUnicastEvent(index));
            }
            {
                semaphore.acquire(10);
                int count = 0;
                for (int index = 0; index < size; index++) {
                    MockUnicastMonitor monitor = monitors[index];
                    count += monitor.getCount();
                }
                Assert.assertEquals(10, count);
            }

            // 注销监控器
            for (int index = 0; index < 5; index++) {
                MockUnicastMonitor monitor = monitors[index];
                channel.unregisterMonitor(addresses, monitor);
                Assert.assertFalse(channel.getMonitors(MockUnicastEvent.class).contains(monitor));
            }
            // 触发事件
            for (int index = 0; index < size; index++) {
                channel.triggerEvent(new MockUnicastEvent(index));
            }
            {
                semaphore.acquire(10);
                int count = 0;
                for (int index = 0; index < size; index++) {
                    MockUnicastMonitor monitor = monitors[index];
                    count += monitor.getCount();
                }
                Assert.assertEquals(20, count);
            }
        } finally {
            channel.stop();
        }
    }

    @Test
    public void testTriggerTopicEvent() throws Exception {
        int size = 10;
        Set<Class> addresses = new HashSet<>();
        addresses.add(MockBroadcastEvent.class);
        EventChannel channel = getEventChannel(EventMode.TOPIC);
        Assert.assertEquals(EventMode.TOPIC, channel.getMode());
        Semaphore semaphore = new Semaphore(0);
        MockBroadcastMonitor[] monitors = new MockBroadcastMonitor[size];
        for (int index = 0; index < size; index++) {
            monitors[index] = new MockBroadcastMonitor(index, semaphore);
        }

        try {
            channel.start();
            // 注册监控器
            for (int index = 0; index < size; index++) {
                MockBroadcastMonitor monitor = monitors[index];
                channel.registerMonitor(addresses, monitor);
                Assert.assertTrue(channel.getMonitors(MockBroadcastEvent.class).contains(monitor));
            }
            // 触发事件
            for (int index = 0; index < size; index++) {
                channel.triggerEvent(new MockBroadcastEvent(index));
            }
            {
                semaphore.acquire(100);
                int count = 0;
                for (int index = 0; index < size; index++) {
                    MockBroadcastMonitor monitor = monitors[index];
                    count += monitor.getCount();
                }
                Assert.assertEquals(100, count);
            }

            // 注销监控器
            for (int index = 0; index < 5; index++) {
                MockBroadcastMonitor monitor = monitors[index];
                channel.unregisterMonitor(addresses, monitor);
                Assert.assertFalse(channel.getMonitors(MockBroadcastEvent.class).contains(monitor));
            }
            // 触发事件
            for (int index = 0; index < size; index++) {
                channel.triggerEvent(new MockBroadcastEvent(index));
            }
            {
                semaphore.acquire(50);
                int count = 0;
                for (int index = 0; index < size; index++) {
                    MockBroadcastMonitor monitor = monitors[index];
                    count += monitor.getCount();
                }
                Assert.assertEquals(150, count);
            }
        } finally {
            channel.stop();
        }
    }

}
