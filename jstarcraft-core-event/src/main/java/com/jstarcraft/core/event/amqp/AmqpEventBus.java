package com.jstarcraft.core.event.amqp;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap.Builder;
import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventBus;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

public class AmqpEventBus extends AbstractEventBus {

    private Session session;

    private ContentCodec codec;

    private ConcurrentMap<Class<?>, MessageProducer> address2Producers;

    private ConcurrentMap<Class<?>, MessageConsumer> address2Consumers;

    private class EventHandler implements MessageListener {

        private Class<?> clazz;

        private EventManager manager;

        private EventHandler(Class<?> clazz, EventManager manager) {
            this.clazz = clazz;
            this.manager = manager;
        }

        @Override
        public void onMessage(Message data) {
            try {
                byte[] bytes = data.getBody(byte[].class);
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

    public AmqpEventBus(EventMode mode, Session session, ContentCodec codec) {
        super(mode);
        this.session = session;
        this.codec = codec;
        Builder<Class<?>, MessageProducer> builder = new Builder<>();
        builder.initialCapacity(1000);
        builder.maximumWeightedCapacity(1000);
        this.address2Producers = builder.build();
        this.address2Consumers = new ConcurrentHashMap<>();
    }

    @Override
    public void registerMonitor(Set<Class<?>> addresses, EventMonitor monitor) {
        try {
            for (Class<?> address : addresses) {
                EventManager manager = address2Managers.get(address);
                if (manager == null) {
                    manager = new EventManager();
                    address2Managers.put(address, manager);
                    Destination destination = null;
                    switch (mode) {
                    case QUEUE: {
                        destination = session.createQueue(mode + address.getName());
                        break;
                    }
                    case TOPIC: {
                        destination = session.createTopic(mode + address.getName());
                        break;
                    }
                    }
                    MessageConsumer consumer = session.createConsumer(destination);
                    EventHandler handler = new EventHandler(address, manager);
                    consumer.setMessageListener(handler);
                    address2Consumers.put(address, consumer);
                }
                manager.attachMonitor(monitor);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class<?>> addresses, EventMonitor monitor) {
        try {
            for (Class<?> address : addresses) {
                EventManager manager = address2Managers.get(address);
                if (manager != null) {
                    manager.detachMonitor(monitor);
                    if (manager.getSize() == 0) {
                        address2Managers.remove(address);
                        MessageConsumer consumer = address2Consumers.remove(address);
                        consumer.close();
                    }
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void triggerEvent(Object event) {
        try {
            Class<?> address = event.getClass();
            MessageProducer producer = null;
            synchronized (address2Producers) {
                producer = address2Producers.get(address);
                Destination destination = null;
                switch (mode) {
                case QUEUE: {
                    destination = session.createQueue(mode + address.getName());
                    break;
                }
                case TOPIC: {
                    destination = session.createTopic(mode + address.getName());
                    break;
                }
                }
                producer = session.createProducer(destination);
                address2Producers.put(address, producer);
            }
            byte[] bytes = codec.encode(address, event);
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(bytes);
            producer.send(message);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
