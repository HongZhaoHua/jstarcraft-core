package com.jstarcraft.core.event.jms;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventBus;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

public class JmsEventBus extends AbstractEventBus {

    private ConnectionFactory factory;

    private JMSContext context;

    private ContentCodec codec;

    private JMSProducer producer;

    private ConcurrentMap<Class, JMSConsumer> address2Consumers;

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

    public JmsEventBus(EventMode mode, ConnectionFactory factory, ContentCodec codec) {
        super(mode);
        this.factory = factory;
        this.context = factory.createContext();
        this.codec = codec;
        this.producer = context.createProducer();
        this.address2Consumers = new ConcurrentHashMap<>();
    }

    @Override
    public void registerMonitor(Set<Class> addresses, EventMonitor monitor) {
        for (Class address : addresses) {
            EventManager manager = address2Managers.get(address);
            if (manager == null) {
                manager = new EventManager();
                address2Managers.put(address, manager);
                Destination destination = null;
                switch (mode) {
                case QUEUE: {
                    // TODO 需要防止路径冲突
                    destination = context.createQueue(address.getName());
                    break;
                }
                case TOPIC: {
                    // TODO 需要防止路径冲突
                    destination = context.createTopic(address.getName());
                    break;
                }
                }
                // 注意:JMSContext不能共享.
                JMSContext context = factory.createContext();
                JMSConsumer consumer = context.createConsumer(destination);
                EventHandler handler = new EventHandler(address, manager);
                consumer.setMessageListener(handler);
                address2Consumers.put(address, consumer);
            }
            manager.attachMonitor(monitor);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class> addresses, EventMonitor monitor) {
        for (Class address : addresses) {
            EventManager manager = address2Managers.get(address);
            if (manager != null) {
                manager.detachMonitor(monitor);
                if (manager.getSize() == 0) {
                    address2Managers.remove(address);
                    JMSConsumer consumer = address2Consumers.remove(address);
                    consumer.close();
                }
            }
        }
    }

    @Override
    public void triggerEvent(Object event) {
        Class address = event.getClass();
        Destination destination = null;
        switch (mode) {
        case QUEUE: {
            // TODO 需要防止路径冲突
            destination = context.createQueue(address.getName());
            break;
        }
        case TOPIC: {
            // TODO 需要防止路径冲突
            destination = context.createTopic(address.getName());
            break;
        }
        }
        byte[] bytes = codec.encode(address, event);
        producer.send(destination, bytes);
    }

}
