package com.jstarcraft.core.event.rabbit;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
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

    /** RabbitMQ的Channel(非线程安全) */
    private Channel channel;

    private ContentCodec codec;

    private ConcurrentMap<Class, String> tags;

    private class EventHandler extends DefaultConsumer {

        private Class clazz;

        private EventManager manager;

        private EventHandler(Channel channel, Class clazz, EventManager manager) {
            super(channel);
            this.clazz = clazz;
            this.manager = manager;
        }

        @Override
        public void handleDelivery(String tag, Envelope envelope, BasicProperties properties, byte[] bytes) {
            try {
                Object event = codec.decode(clazz, bytes);
                Map<String, Object> contexts = properties.getHeaders();
                if (contexts != null) {
                    Object context = contexts.get(CONTEXT);
                    if (context != null) {
                        if (setter != null) {
                            setter.accept(context.toString());
                        }
                    }
                }
                synchronized (manager) {
                    switch (mode) {
                    case QUEUE: {
                        int size = manager.getSize();
                        int index = RandomUtility.randomInteger(size);
                        EventMonitor monitor = manager.getMonitor(index);
                        try {
                            monitor.onEvent(event);
                            channel.basicAck(envelope.getDeliveryTag(), false);
                        } catch (Exception exception) {
                            // 记录日志
                            String message = StringUtility.format("监控器[{}]处理RabbitMQ事件[{}]时异常", monitor.getClass(), event.getClass());
                            logger.error(message, exception);
                            channel.basicNack(envelope.getDeliveryTag(), false, false);
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
                            channel.basicNack(envelope.getDeliveryTag(), false, false);
                        } else {
                            channel.basicAck(envelope.getDeliveryTag(), false);
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

    public RabbitEventChannel(EventMode mode, String name, Channel channel, ContentCodec codec) {
        super(mode, name);
        this.channel = channel;
        this.codec = codec;
        this.tags = new ConcurrentHashMap<>();
        try {
            channel.exchangeDeclare(name, BuiltinExchangeType.DIRECT);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public synchronized void registerMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                EventManager manager = managers.get(type);
                if (manager == null) {
                    manager = new EventManager();
                    managers.put(type, manager);

                    String address = null;
                    switch (mode) {
                    case QUEUE: {
                        // 共享地址,不独占队列
                        address = name + StringUtility.DOT + type.getName();
                        channel.queueDeclare(address, true, false, false, null);
                        break;
                    }
                    case TOPIC: {
                        // 不共享地址,独占队列
                        address = name + StringUtility.DOT + type.getName() + UUID.randomUUID();
                        channel.queueDeclare(address, true, true, true, null);
                        break;
                    }
                    }
                    String key = type.getName();
                    channel.queueBind(address, name, key);

                    EventHandler handler = new EventHandler(channel, type, manager);
                    String tag = channel.basicConsume(address, false, handler);
                    tags.put(type, tag);
                }
                manager.attachMonitor(monitor);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
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

                        String tag = tags.remove(type);
                        channel.basicCancel(tag);
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
            String key = type.getName();
            byte[] bytes = codec.encode(type, event);
            Builder properties = new Builder();
            if (getter != null) {
                String context = getter.get();
                if (context != null) {
                    properties.headers(Collections.singletonMap(CONTEXT, context));
                }
            }
            channel.basicPublish(name, key, properties.build(), bytes);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
