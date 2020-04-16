package com.jstarcraft.core.event.vertx;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

/**
 * Vert.x事件管道
 * 
 * @author Birdy
 *
 */
public class VertxEventChannel extends AbstractEventChannel {

    private EventBus bus;

    private ContentCodec codec;

    private ConcurrentMap<Class, MessageConsumer<byte[]>> type2Consumers;

    private class EventHandler implements Handler<Message<byte[]>> {

        private Class clazz;

        private EventManager manager;

        private EventHandler(Class clazz, EventManager manager) {
            this.clazz = clazz;
            this.manager = manager;
        }

        @Override
        public void handle(Message<byte[]> data) {
            try {
                byte[] bytes = data.body();
                Object event = codec.decode(clazz, bytes);
                synchronized (manager) {
                    switch (mode) {
                    case QUEUE: {
                        int size = manager.getSize();
                        int index = RandomUtility.randomInteger(size);
                        EventMonitor monitor = manager.getMonitor(index);
                        try {
                            monitor.onEvent(event);
                        } catch (Exception exception) {
                            // 记录日志
                            String message = StringUtility.format("监控器[{}]处理JMS事件[{}]时异常", monitor.getClass(), event.getClass());
                            logger.error(message, exception);
                        }
                        break;
                    }
                    case TOPIC: {
                        for (EventMonitor monitor : manager) {
                            try {
                                monitor.onEvent(event);
                            } catch (Exception exception) {
                                // 记录日志
                                String message = StringUtility.format("监控器[{}]处理JMS事件[{}]时异常", monitor.getClass(), event.getClass());
                                logger.error(message, exception);
                            }
                        }
                        break;
                    }
                    }
                }
            } catch (Exception exception) {
                // 记录日志
                String message = StringUtility.format("编解码器[{}]处理JMS事件[{}]时异常", codec.getClass(), data);
                logger.error(message, exception);
            }
        }

    };

    public VertxEventChannel(EventMode mode, String name, EventBus bus, ContentCodec codec) {
        super(mode, name);
        this.bus = bus;
        this.codec = codec;
        this.type2Consumers = new ConcurrentHashMap<>();
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                EventManager manager = type2Managers.get(type);
                if (manager == null) {
                    manager = new EventManager();
                    type2Managers.put(type, manager);
                    // TODO 需要防止路径冲突
                    EventHandler handler = new EventHandler(type, manager);
                    MessageConsumer<byte[]> consumer = bus.consumer(name + StringUtility.DOT + type.getName(), handler);
                    CountDownLatch latch = new CountDownLatch(1);
                    consumer.completionHandler((register) -> {
                        latch.countDown();
                    });
                    latch.await();
                    type2Consumers.put(type, consumer);
                }
                manager.attachMonitor(monitor);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                EventManager manager = type2Managers.get(type);
                if (manager != null) {
                    manager.detachMonitor(monitor);
                    if (manager.getSize() == 0) {
                        type2Managers.remove(type);
                        MessageConsumer<byte[]> consumer = type2Consumers.remove(type);
                        CountDownLatch latch = new CountDownLatch(1);
                        consumer.unregister((unregister) -> {
                            latch.countDown();
                        });
                        latch.await();
                    }
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void triggerEvent(Object event) {
        Class type = event.getClass();
        byte[] bytes = codec.encode(type, event);
        switch (mode) {
        case QUEUE: {
            // TODO 需要防止路径冲突
            bus.send(name + StringUtility.DOT + type.getName(), bytes);
            break;
        }
        case TOPIC: {
            // TODO 需要防止路径冲突
            bus.publish(name + StringUtility.DOT + type.getName(), bytes);
            break;
        }
        }
    }

}
