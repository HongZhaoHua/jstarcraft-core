package com.jstarcraft.core.event.redis;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Redis队列事件管道
 * 
 * @author Birdy
 *
 */
public class RedisQueueEventChannel extends RedisEventChannel {

    private ConcurrentMap<Class, EventThread> threads;

    private ConcurrentMap<Class, RBlockingQueue<byte[]>> queues;

    private class EventThread extends Thread {

        private Class clazz;

        private EventManager manager;

        private RBlockingQueue<byte[]> events;

        private EventThread(Class clazz, EventManager manager, RBlockingQueue<byte[]> events) {
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

    public RedisQueueEventChannel(String name, Redisson redisson, ContentCodec codec) {
        super(EventMode.QUEUE, name, redisson, codec);
        this.threads = new ConcurrentHashMap<>();
        this.queues = new ConcurrentHashMap<>();
    }

    protected RBlockingQueue<byte[]> getQueue(Class type) {
        RBlockingQueue<byte[]> queue = queues.get(type);
        if (queue == null) {
            queue = redisson.getBlockingQueue(name + StringUtility.DOT + type.getName(), byteCodec);
            queues.put(type, queue);
        }
        return queue;
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        for (Class type : types) {
            EventManager manager = managers.get(type);
            if (manager == null) {
                manager = new EventManager();
                managers.put(type, manager);
                // TODO 需要防止路径冲突
                RBlockingQueue<byte[]> events = getQueue(type);
                EventThread thread = new EventThread(type, manager, events);
                thread.start();
                threads.put(type, thread);
            }
            manager.attachMonitor(monitor);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class> types, EventMonitor monitor) {
        for (Class type : types) {
            EventManager manager = managers.get(type);
            if (manager != null) {
                manager.detachMonitor(monitor);
                if (manager.getSize() == 0) {
                    managers.remove(type);
                    EventThread thread = threads.remove(type);
                    thread.interrupt();
                }
            }
        }
    }

    @Override
    public void triggerEvent(Object event) {
        Class type = event.getClass();
        // TODO 需要防止路径冲突
        RBlockingQueue<byte[]> events = getQueue(type);
        byte[] bytes = codec.encode(type, event);
        events.add(bytes);
    }

}
