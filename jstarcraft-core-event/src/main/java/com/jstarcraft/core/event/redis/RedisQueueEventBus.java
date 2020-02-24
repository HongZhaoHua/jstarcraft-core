package com.jstarcraft.core.event.redis;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventBus;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

public class RedisQueueEventBus extends AbstractEventBus {

    private static final Logger logger = LoggerFactory.getLogger(RedisQueueEventBus.class);

    private String name;

    private Redisson redisson;

    private ContentCodec codec;

    private ConcurrentMap<Class<?>, EventThread> address2Threads;

    private class EventThread extends Thread {

        private Class<?> clazz;

        private EventManager manager;

        private RBlockingQueue<byte[]> events;

        private EventThread(Class<?> clazz, EventManager manager, RBlockingQueue<byte[]> events) {
            this.clazz = clazz;
            this.manager = manager;
            this.events = events;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    byte[] bytes = events.take();
                    try {
                        Object event = codec.decode(clazz, bytes);
                        int size = manager.getSize();
                        int index = RandomUtility.randomInteger(size);
                        EventMonitor monitor = manager.getMonitor(index);
                        try {
                            monitor.onEvent(event);
                        } catch (Exception exception) {
                            // 记录日志
                            String message = StringUtility.format("监控器[{}]处理Redis事件[{}]时异常", monitor.getClass(), bytes);
                            logger.error(message, exception);
                        }
                    } catch (Exception exception) {
                        // 记录日志
                        String message = StringUtility.format("编解码器[{}]处理Redis事件[{}]时异常", codec.getClass(), bytes);
                        logger.error(message, exception);
                    }
                }
            } catch (InterruptedException exception) {
            }
        }
    };

    public RedisQueueEventBus(String name, Redisson redisson, ContentCodec codec) {
        super(EventMode.QUEUE);
        this.name = name;
        this.redisson = redisson;
        this.codec = codec;
        this.address2Threads = new ConcurrentHashMap<>();
    }

    @Override
    public void registerMonitor(Set<Class<?>> addresses, EventMonitor monitor) {
        for (Class<?> address : addresses) {
            EventManager manager = address2Managers.get(address);
            if (manager == null) {
                manager = new EventManager();
                address2Managers.put(address, manager);
                // TODO 需要防止路径冲突
                RBlockingQueue<byte[]> events = redisson.getBlockingQueue(name + StringUtility.DOT + address.getName());
                EventThread thread = new EventThread(address, manager, events);
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
        // TODO 需要防止路径冲突
        RBlockingQueue<byte[]> events = redisson.getBlockingQueue(name + StringUtility.DOT + address.getName());
        byte[] bytes = codec.encode(address, event);
        events.add(bytes);
    }

}
