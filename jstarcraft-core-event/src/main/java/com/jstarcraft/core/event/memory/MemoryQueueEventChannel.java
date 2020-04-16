package com.jstarcraft.core.event.memory;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 内存队列事件管道
 * 
 * @author Birdy
 *
 */
public class MemoryQueueEventChannel extends AbstractEventChannel {

    private static final Logger logger = LoggerFactory.getLogger(MemoryQueueEventChannel.class);

    private int size;

    private ConcurrentMap<Class, BlockingQueue<Object>> type2Events;

    private ConcurrentMap<Class, EventThread> type2Threads;

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

    public MemoryQueueEventChannel(String name, int size) {
        super(EventMode.QUEUE, name);
        this.size = size;
        this.type2Events = new ConcurrentHashMap<>();
        this.type2Threads = new ConcurrentHashMap<>();
    }

    private BlockingQueue<Object> getEvents(Class type) {
        BlockingQueue<Object> events = type2Events.get(type);
        if (events == null) {
            events = new ArrayBlockingQueue<>(size);
            type2Events.put(type, events);
        }
        return events;
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        for (Class type : types) {
            EventManager manager = type2Managers.get(type);
            if (manager == null) {
                manager = new EventManager();
                type2Managers.put(type, manager);
                BlockingQueue<Object> events = getEvents(type);
                EventThread thread = new EventThread(manager, events);
                thread.start();
                type2Threads.put(type, thread);
            }
            manager.attachMonitor(monitor);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class> types, EventMonitor monitor) {
        for (Class type : types) {
            EventManager manager = type2Managers.get(type);
            if (manager != null) {
                manager.detachMonitor(monitor);
                if (manager.getSize() == 0) {
                    type2Managers.remove(type);
                    EventThread thread = type2Threads.remove(type);
                    thread.interrupt();
                }
            }
        }
    }

    @Override
    public void triggerEvent(Object event) {
        Class type = event.getClass();
        BlockingQueue<Object> events = getEvents(type);
        events.offer(event);
    }

}
