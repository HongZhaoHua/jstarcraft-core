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

    /** JMS的Session(非线程安全) */
    private Session session;

    private ContentCodec codec;

    private ConcurrentMap<Class, MessageProducer> producers;

    private ConcurrentMap<Class, MessageConsumer> consumers;

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
                String context = data.getStringProperty(CONTEXT);
                if (context != null) {
                    if (setter != null) {
                        setter.accept(context);
                    }
                }
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
                            String message = StringUtility.format("监控器[{}]处理AMQP事件[{}]时异常", monitor.getClass(), event.getClass());
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
                                String message = StringUtility.format("监控器[{}]处理AMQP事件[{}]时异常", monitor.getClass(), event.getClass());
                                logger.error(message, exception);
                            }
                        }
                        break;
                    }
                    }
                }
            } catch (Exception exception) {
                // 记录日志
                String message = StringUtility.format("编解码器[{}]处理AMQP事件[{}]时异常", codec.getClass(), data);
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
        this.producers = builder.build();
        this.consumers = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void registerMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                EventManager manager = managers.get(type);
                if (manager == null) {
                    manager = new EventManager();
                    managers.put(type, manager);
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
                    consumers.put(type, consumer);
                }
                manager.attachMonitor(monitor);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public synchronized void unregisterMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                EventManager manager = managers.get(type);
                if (manager != null) {
                    manager.detachMonitor(monitor);
                    if (manager.getSize() == 0) {
                        managers.remove(type);
                        MessageConsumer consumer = consumers.remove(type);
                        consumer.close();
                    }
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public synchronized void triggerEvent(Object event) {
        try {
            Class type = event.getClass();
            MessageProducer producer = null;
            synchronized (producers) {
                producer = producers.get(type);
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
                producers.put(type, producer);
            }
            byte[] bytes = codec.encode(type, event);
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(bytes);
            if (getter != null) {
                String context = getter.get();
                if (context != null) {
                    message.setStringProperty(CONTEXT, context);
                }
            }
            producer.send(message);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
