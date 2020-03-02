package com.jstarcraft.core.event.vertx;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

import javax.jms.JMSProducer;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventBus;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

public class VertxEventBus extends AbstractEventBus {

    private EventBus bus;

    private ContentCodec codec;

    private JMSProducer producer;

    private ConcurrentMap<Class, MessageConsumer<byte[]>> address2Consumers;

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

    public VertxEventBus(EventMode mode, String name, EventBus bus, ContentCodec codec) {
        super(mode, name);
        this.bus = bus;
        this.codec = codec;
        this.address2Consumers = new ConcurrentHashMap<>();
    }

    @Override
    public void registerMonitor(Set<Class> addresses, EventMonitor monitor) {
        try {
            for (Class address : addresses) {
                EventManager manager = address2Managers.get(address);
                if (manager == null) {
                    manager = new EventManager();
                    address2Managers.put(address, manager);
                    // TODO 需要防止路径冲突
                    EventHandler handler = new EventHandler(address, manager);
                    MessageConsumer<byte[]> consumer = bus.consumer(name + StringUtility.DOT + address.getName(), handler);
                    CountDownLatch latch = new CountDownLatch(1);
                    consumer.completionHandler((register) -> {
                        latch.countDown();
                    });
                    latch.await();
                    address2Consumers.put(address, consumer);
                }
                manager.attachMonitor(monitor);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class> addresses, EventMonitor monitor) {
        try {
            for (Class address : addresses) {
                EventManager manager = address2Managers.get(address);
                if (manager != null) {
                    manager.detachMonitor(monitor);
                    if (manager.getSize() == 0) {
                        address2Managers.remove(address);
                        MessageConsumer<byte[]> consumer = address2Consumers.remove(address);
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
        Class address = event.getClass();
        byte[] bytes = codec.encode(address, event);
        switch (mode) {
        case QUEUE: {
            // TODO 需要防止路径冲突
            bus.send(name + StringUtility.DOT + address.getName(), bytes);
            break;
        }
        case TOPIC: {
            // TODO 需要防止路径冲突
            bus.publish(name + StringUtility.DOT + address.getName(), bytes);
            break;
        }
        }
    }

}
