package com.jstarcraft.core.event.memory;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.event.AbstractEventBus;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

public class MemoryQueueEventBus extends AbstractEventBus {

    private static final Logger logger = LoggerFactory.getLogger(MemoryQueueEventBus.class);

    private int size;

    private ConcurrentMap<Class<?>, BlockingQueue<Object>> address2Events;

    private ConcurrentMap<Class<?>, EventThread> address2Threads;

    private class EventThread extends Thread {

        private EventManager manager;

        private BlockingQueue<?> events;

        private EventThread(EventManager manager, BlockingQueue<?> events) {
            this.manager = manager;
            this.events = events;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Object event = events.take();
                    int size = manager.getSize();
                    if (size == 0) {
                        continue;
                    }
                    int index = RandomUtility.randomInteger(size);
                    EventMonitor monitor = manager.getMonitor(index);
                    try {
                        monitor.onEvent(event);
                    } catch (Exception exception) {
                        // 记录日志
                        String message = StringUtility.format("监控器[{}]处理内存事件[{}]时异常", monitor.getClass(), event);
                        logger.error(message, exception);
                    }
                }
            } catch (InterruptedException exception) {
            }
        }
    };

    public MemoryQueueEventBus(int size) {
        super(EventMode.QUEUE);
        this.size = size;
        this.address2Events = new ConcurrentHashMap<>();
        this.address2Threads = new ConcurrentHashMap<>();
    }

    private BlockingQueue<Object> getEvents(Class<?> address) {
        BlockingQueue<Object> events = address2Events.get(address);
        if (events == null) {
            events = new ArrayBlockingQueue<>(size);
            address2Events.put(address, events);
        }
        return events;
    }

    @Override
    public void registerMonitor(Set<Class<?>> addresses, EventMonitor monitor) {
        for (Class<?> address : addresses) {
            EventManager manager = address2Managers.get(address);
            if (manager == null) {
                manager = new EventManager();
                address2Managers.put(address, manager);
                BlockingQueue<Object> events = getEvents(address);
                EventThread thread = new EventThread(manager, events);
                thread.start();
                address2Threads.put(address, thread);
            }
            manager.attachMonitor(monitor);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class<?>> addresses, EventMonitor monitor) {
        for (Class<?> address : addresses) {
            EventManager manager = address2Managers.get(address);
            if (manager != null) {
                manager.detachMonitor(monitor);
                if (manager.getSize() == 0) {
                    address2Managers.remove(address);
                    EventThread thread = address2Threads.remove(address);
                    thread.interrupt();
                }
            }
        }
    }

    @Override
    public void triggerEvent(Object event) {
        Class<?> address = event.getClass();
        BlockingQueue<Object> events = getEvents(address);
        events.offer(event);
    }

}
