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

    private String addresses;

    private ContentCodec codec;

    private MQProducer producer;

    private ConcurrentMap<Class, MQPushConsumer> type2Consumers;

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

    public RocketEventChannel(EventMode mode, String name, String addresses, ContentCodec codec) {
        super(mode, name);
        try {
            this.addresses = addresses;
            this.codec = codec;
            DefaultMQProducer producer = new DefaultMQProducer(name);
            producer.setInstanceName(name);
            producer.setNamesrvAddr(addresses);
            producer.start();
            this.producer = producer;
            this.type2Consumers = new ConcurrentHashMap<>();
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
                    // TODO 需要防止路径冲突
                    String address = name + StringUtility.DOT + type.getName();
                    address = address.replace(StringUtility.DOT, StringUtility.DASH);
                    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(address);
                    consumer.setInstanceName(name);
                    consumer.setNamesrvAddr(addresses);
                    consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
                    consumer.setConsumeMessageBatchMaxSize(1000);
                    consumer.subscribe(address, "*");
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
                    EventHandler handler = new EventHandler(type, manager);
                    consumer.registerMessageListener(handler);
                    consumer.start();
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
        for (Class type : types) {
            EventManager manager = type2Managers.get(type);
            if (manager != null) {
                manager.detachMonitor(monitor);
                if (manager.getSize() == 0) {
                    type2Managers.remove(type);
                    MQPushConsumer consumer = type2Consumers.remove(type);
                    consumer.shutdown();
                }
            }
        }
    }

    @Override
    public void triggerEvent(Object event) {
        try {
            Class type = event.getClass();
            String address = name + StringUtility.DOT + type.getName();
            address = address.replace(StringUtility.DOT, StringUtility.DASH);
            byte[] bytes = codec.encode(type, event);
            Message message = new Message(address, address, bytes);
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
        for (MQPushConsumer cousumer : type2Consumers.values()) {
            cousumer.shutdown();
        }
        producer.shutdown();
    }

}
