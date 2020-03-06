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

public class RocketEventChannel extends AbstractEventChannel {

    private String address;

    private ContentCodec codec;

    private MQProducer producer;

    private ConcurrentMap<Class, MQPushConsumer> address2Consumers;

    private class EventHandler implements MessageListenerConcurrently {

        private Class clazz;

        private EventManager manager;

        private EventHandler(Class clazz, EventManager manager) {
            this.clazz = clazz;
            this.manager = manager;
        }

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> datas, ConsumeConcurrentlyContext context) {
            for (MessageExt data : datas) {
                try {
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
                                String message = StringUtility.format("监控器[{}]处理Rocket事件[{}]时异常", monitor.getClass(), event.getClass());
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
                                    String message = StringUtility.format("监控器[{}]处理Rocket事件[{}]时异常", monitor.getClass(), event.getClass());
                                    logger.error(message, exception);
                                }
                            }
                            break;
                        }
                        }
                    }
                } catch (Exception exception) {
                    // 记录日志
                    String message = StringUtility.format("编解码器[{}]处理Rocket事件[{}]时异常", codec.getClass(), data);
                    logger.error(message, exception);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

    };

    public RocketEventChannel(EventMode mode, String name, String address, ContentCodec codec) {
        super(mode, name);
        try {
            this.address = address;
            this.codec = codec;
            DefaultMQProducer producer = new DefaultMQProducer(name);
            producer.setInstanceName(name);
            producer.setNamesrvAddr(address);
            producer.start();
            this.producer = producer;
            this.address2Consumers = new ConcurrentHashMap<>();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void registerMonitor(Set<Class> addresses, EventMonitor monitor) {
        try {
            for (Class clazz : addresses) {
                EventManager manager = address2Managers.get(clazz);
                if (manager == null) {
                    manager = new EventManager();
                    address2Managers.put(clazz, manager);
                    // TODO 需要防止路径冲突
                    String channel = name + StringUtility.DOT + clazz.getName();
                    channel = channel.replace(StringUtility.DOT, StringUtility.DASH);
                    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(channel);
                    consumer.setInstanceName(name);
                    consumer.setNamesrvAddr(address);
                    consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
                    consumer.setConsumeMessageBatchMaxSize(1000);
                    consumer.subscribe(channel, "*");
                    switch (mode) {
                    case QUEUE: {
                        consumer.setMessageModel(MessageModel.CLUSTERING);
                        break;
                    }
                    case TOPIC: {
                        consumer.setMessageModel(MessageModel.BROADCASTING);
                        break;
                    }
                    }
                    EventHandler handler = new EventHandler(clazz, manager);
                    consumer.registerMessageListener(handler);
                    consumer.start();
                    address2Consumers.put(clazz, consumer);
                }
                manager.attachMonitor(monitor);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class> addresses, EventMonitor monitor) {
        for (Class clazz : addresses) {
            EventManager manager = address2Managers.get(clazz);
            if (manager != null) {
                manager.detachMonitor(monitor);
                if (manager.getSize() == 0) {
                    address2Managers.remove(clazz);
                    MQPushConsumer consumer = address2Consumers.remove(clazz);
                    consumer.shutdown();
                }
            }
        }
    }

    @Override
    public void triggerEvent(Object event) {
        try {
            Class clazz = event.getClass();
            String channel = name + StringUtility.DOT + clazz.getName();
            channel = channel.replace(StringUtility.DOT, StringUtility.DASH);
            byte[] bytes = codec.encode(clazz, event);
            Message message = new Message(channel, channel, bytes);
            producer.send(message);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        for (MQPushConsumer cousumer : address2Consumers.values()) {
            cousumer.shutdown();
        }
        producer.shutdown();
    }

}
