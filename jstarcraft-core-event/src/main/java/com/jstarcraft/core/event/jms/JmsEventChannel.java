package com.jstarcraft.core.event.jms;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * JMS事件管道
 * 
 * @author Birdy
 *
 */
public class JmsEventChannel extends AbstractEventChannel {

    private ConnectionFactory factory;

    private JMSContext context;

    private ContentCodec codec;

    private JMSProducer producer;

    private ConcurrentMap<Class, JMSConsumer> consumers;

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

    public JmsEventChannel(EventMode mode, String name, ConnectionFactory factory, ContentCodec codec) {
        super(mode, name);
        this.factory = factory;
        this.context = factory.createContext();
        this.codec = codec;
        this.producer = context.createProducer();
        this.consumers = new ConcurrentHashMap<>();
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        for (Class type : types) {
            EventManager manager = managers.get(type);
            if (manager == null) {
                manager = new EventManager();
                managers.put(type, manager);
                Destination address = null;
                switch (mode) {
                case QUEUE: {
                    // TODO 需要防止路径冲突
                    address = context.createQueue(name + StringUtility.DOT + type.getName());
                    break;
                }
                case TOPIC: {
                    // TODO 需要防止路径冲突
                    address = context.createTopic(name + StringUtility.DOT + type.getName());
                    break;
                }
                }
                // 注意:JMSContext不能共享.
                JMSContext context = factory.createContext();
                JMSConsumer consumer = context.createConsumer(address);
                EventHandler handler = new EventHandler(type, manager);
                consumer.setMessageListener(handler);
                consumers.put(type, consumer);
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
                    JMSConsumer consumer = consumers.remove(type);
                    consumer.close();
                }
            }
        }
    }

    @Override
    public void triggerEvent(Object event) {
        try {
            Class type = event.getClass();
            Destination address = null;
            switch (mode) {
            case QUEUE: {
                // TODO 需要防止路径冲突
                address = context.createQueue(name + StringUtility.DOT + type.getName());
                break;
            }
            case TOPIC: {
                // TODO 需要防止路径冲突
                address = context.createTopic(name + StringUtility.DOT + type.getName());
                break;
            }
            }
            byte[] bytes = codec.encode(type, event);
            BytesMessage message = context.createBytesMessage();
            message.writeBytes(bytes);
            if (getter != null) {
                String context = getter.get();
                if (context != null) {
                    message.setStringProperty(CONTEXT, context);
                }
            }
            producer.send(address, message);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
