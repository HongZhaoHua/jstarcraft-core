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
import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * AMQP事件管道
 * 
 * @author Birdy
 *
 */
public class AmqpEventChannel extends AbstractEventChannel {

    private Session session;

    private ContentCodec codec;

    private ConcurrentMap<Class, MessageProducer> type2Producers;

    private ConcurrentMap<Class, MessageConsumer> type2Consumers;

    private class EventHandler implements MessageListener {

        private Class clazz;

        private EventManager manager;

        private EventHandler(Class clazz, EventManager manager) {
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

    public AmqpEventChannel(EventMode mode, String name, Session session, ContentCodec codec) {
        super(mode, name);
        this.session = session;
        this.codec = codec;
        Builder<Class, MessageProducer> builder = new Builder<>();
        builder.initialCapacity(1000);
        builder.maximumWeightedCapacity(1000);
        this.type2Producers = builder.build();
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
                    Destination address = null;
                    switch (mode) {
                    case QUEUE: {
                        // TODO 需要防止路径冲突
                        address = session.createQueue(name + StringUtility.DOT + type.getName());
                        break;
                    }
                    case TOPIC: {
                        // TODO 需要防止路径冲突
                        address = session.createTopic(name + StringUtility.DOT + type.getName());
                        break;
                    }
                    }
                    MessageConsumer consumer = session.createConsumer(address);
                    EventHandler handler = new EventHandler(type, manager);
                    consumer.setMessageListener(handler);
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
                        MessageConsumer consumer = type2Consumers.remove(type);
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
            Class type = event.getClass();
            MessageProducer producer = null;
            synchronized (type2Producers) {
                producer = type2Producers.get(type);
                Destination address = null;
                switch (mode) {
                case QUEUE: {
                    // TODO 需要防止路径冲突
                    address = session.createQueue(name + StringUtility.DOT + type.getName());
                    break;
                }
                case TOPIC: {
                    // TODO 需要防止路径冲突
                    address = session.createTopic(name + StringUtility.DOT + type.getName());
                    break;
                }
                }
                producer = session.createProducer(address);
                type2Producers.put(type, producer);
            }
            byte[] bytes = codec.encode(type, event);
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(bytes);
            producer.send(message);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
