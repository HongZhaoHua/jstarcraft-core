package com.jstarcraft.core.event.rocket;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * RocketMQ事件管道
 * 
 * @author Birdy
 *
 */
public class RocketEventChannel extends AbstractEventChannel {

    private String connections;

    private ContentCodec codec;

    private MQProducer producer;

    private ConcurrentMap<Class, MQPushConsumer> consumers;

    private class EventHandler implements MessageListenerConcurrently {

        private Class clazz;

        private EventManager manager;

        private EventHandler(Class clazz, EventManager manager) {
            this.clazz = clazz;
            this.manager = manager;
        }

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> datas, ConsumeConcurrentlyContext current) {
            for (MessageExt data : datas) {
                try {
                    String context = data.getUserProperty(CONTEXT);
                    if (context != null) {
                        if (setter != null) {
                            setter.accept(context);
                        }
                    }
                    byte[] bytes = data.getBody();
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
                                String message = StringUtility.format("监控器[{}]处理RocketMQ事件[{}]时异常", monitor.getClass(), event.getClass());
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
                                    String message = StringUtility.format("监控器[{}]处理RocketMQ事件[{}]时异常", monitor.getClass(), event.getClass());
                                    logger.error(message, exception);
                                }
                            }
                            break;
                        }
                        }
                    }
                } catch (Exception exception) {
                    // 记录日志
                    String message = StringUtility.format("编解码器[{}]处理RocketMQ事件[{}]时异常", codec.getClass(), data);
                    logger.error(message, exception);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

    };

    public RocketEventChannel(EventMode mode, String name, String connections, ContentCodec codec) {
        super(mode, name);
        try {
            this.connections = connections;
            this.codec = codec;
            this.consumers = new ConcurrentHashMap<>();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                EventManager manager = managers.get(type);
                if (manager == null) {
                    manager = new EventManager();
                    managers.put(type, manager);
                    // TODO 需要防止路径冲突
                    String address = type.getName();
                    address = address.replace(StringUtility.DOT, StringUtility.DASH);
                    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(String.join(StringUtility.DASH, name, address));
                    consumer.setInstanceName(name);
                    consumer.setNamesrvAddr(connections);
                    consumer.setConsumeMessageBatchMaxSize(1000);
                    consumer.subscribe(name, address);
                    switch (mode) {
                    case QUEUE: {
                        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
                        consumer.setMessageModel(MessageModel.CLUSTERING);
                        break;
                    }
                    case TOPIC: {
                        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
                        consumer.setMessageModel(MessageModel.BROADCASTING);
                        break;
                    }
                    }
                    EventHandler handler = new EventHandler(type, manager);
                    consumer.registerMessageListener(handler);
                    consumer.start();
                    consumers.put(type, consumer);
                }
                manager.attachMonitor(monitor);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
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
                    MQPushConsumer consumer = consumers.remove(type);
                    consumer.shutdown();
                }
            }
        }
    }

    @Override
    public void triggerEvent(Object event) {
        try {
            Class type = event.getClass();
            String address = type.getName();
            address = address.replace(StringUtility.DOT, StringUtility.DASH);
            byte[] bytes = codec.encode(type, event);
            Message message = new Message(name, address, bytes);
            if (getter != null) {
                String context = getter.get();
                if (context != null) {
                    message.putUserProperty(CONTEXT, context);
                }
            }
            producer.send(message);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void start() {
        try {
            DefaultMQProducer producer = new DefaultMQProducer(name);
            producer.setInstanceName(name);
            producer.setNamesrvAddr(connections);
            producer.start();
            this.producer = producer;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void stop() {
        for (MQPushConsumer cousumer : consumers.values()) {
            cousumer.shutdown();
        }
        producer.shutdown();
    }

}
