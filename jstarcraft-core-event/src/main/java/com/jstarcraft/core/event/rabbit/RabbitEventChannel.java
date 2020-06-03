package com.jstarcraft.core.event.rabbit;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.jms.MessageConsumer;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * RabbitMQ事件管道
 * 
 * @author Birdy
 *
 */
public class RabbitEventChannel extends AbstractEventChannel {

    private Channel session;

    private ContentCodec codec;

    private ConcurrentMap<Class, String> type2Tags;

    private class EventHandler extends DefaultConsumer {

        private Class clazz;

        private EventManager manager;

        private EventHandler(Channel session, Class clazz, EventManager manager) {
            super(session);
            this.clazz = clazz;
            this.manager = manager;
        }

        @Override
        public void handleDelivery(String tag, Envelope envelope, BasicProperties properties, byte[] bytes) {
            try {
                Object event = codec.decode(clazz, bytes);
                synchronized (manager) {
                    switch (mode) {
                    case QUEUE: {
                        int size = manager.getSize();
                        int index = RandomUtility.randomInteger(size);
                        EventMonitor monitor = manager.getMonitor(index);
                        try {
                            monitor.onEvent(event);
                            session.basicAck(envelope.getDeliveryTag(), false);
                        } catch (Exception exception) {
                            // 记录日志
                            String message = StringUtility.format("监控器[{}]处理RabbitMQ事件[{}]时异常", monitor.getClass(), event.getClass());
                            logger.error(message, exception);
                            session.basicNack(envelope.getDeliveryTag(), false, false);
                        }
                        break;
                    }
                    case TOPIC: {
                        boolean throwable = false;
                        for (EventMonitor monitor : manager) {
                            try {
                                monitor.onEvent(event);
                            } catch (Exception exception) {
                                // 记录日志
                                String message = StringUtility.format("监控器[{}]处理RabbitMQ事件[{}]时异常", monitor.getClass(), event.getClass());
                                logger.error(message, exception);
                                throwable = true;

                            }
                        }
                        if (throwable) {
                            session.basicNack(envelope.getDeliveryTag(), false, false);
                        } else {
                            session.basicAck(envelope.getDeliveryTag(), false);
                        }
                        break;
                    }
                    }
                }
            } catch (Exception exception) {
                // 记录日志
                String message = StringUtility.format("编解码器[{}]处理RabbitMQ事件[{}]时异常", codec.getClass(), envelope);
                logger.error(message, exception);
            }
        }

    };

    public RabbitEventChannel(EventMode mode, String name, Channel session, ContentCodec codec) {
        super(mode, name);
        this.session = session;
        this.codec = codec;
        this.type2Tags = new ConcurrentHashMap<>();
        try {
            session.exchangeDeclare(name, BuiltinExchangeType.DIRECT);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                EventManager manager = type2Managers.get(type);
                if (manager == null) {
                    manager = new EventManager();
                    type2Managers.put(type, manager);

                    String address = null;
                    switch (mode) {
                    case QUEUE: {
                        // 共享名称,不独占队列
                        address = name + StringUtility.DOT + type.getName();
                        session.queueDeclare(address, true, false, false, null);
                        break;
                    }
                    case TOPIC: {
                        // 不共享名称,独占队列
                        address = name + StringUtility.DOT + type.getName() + UUID.randomUUID();
                        session.queueDeclare(address + UUID.randomUUID().toString(), true, true, true, null);
                        break;
                    }
                    }
                    String key = type.getName();
                    session.queueBind(address, name, key);

                    EventHandler handler = new EventHandler(session, type, manager);
                    String tag = session.basicConsume(address, false, handler);
                    type2Tags.put(type, tag);
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

                        String tag = type2Tags.remove(type);
                        session.basicCancel(tag);
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
            String key = type.getName();
            byte[] bytes = codec.encode(type, event);
            session.basicPublish(name, key, null, bytes);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
