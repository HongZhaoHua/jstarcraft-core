package com.jstarcraft.core.event.rocket;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.RPCHook;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventConsumer;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.StringUtility;

/**
 * RocketMQ事件消费者
 * 
 * @author Birdy
 *
 */
public class RocketEventConsumer extends AbstractEventConsumer {

    /** 主题 */
    private String topic;

    private String connections;

    private ContentCodec codec;

    private ConcurrentMap<Class, MQPushConsumer> consumers;

    protected ConcurrentMap<Class, EventManager> managers;

    private RPCHook hook;

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
                        for (EventMonitor monitor : manager) {
                            try {
                                monitor.onEvent(event);
                            } catch (Exception exception) {
                                // 记录日志
                                String message = StringUtility.format("监控器[{}]处理RocketMQ事件[{}]时异常", monitor.getClass(), event.getClass());
                                logger.error(message, exception);
                            }
                        }
                    }
                } catch (Exception exception) {
                    // 记录日志
                    String message = StringUtility.format("编解码器[{}]处理RocketMQ事件[{}]时异常", codec.getClass(), data);
                    logger.error(message, exception);
                }
            }
            // 消费状态
            // CONSUME_SUCCESS 消费成功
            // RECONSUME_LATER 消费失败
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

    };

    public RocketEventConsumer(String topic, String name, String connections, ContentCodec codec) {
        this(topic, name, connections, codec, null);
    }

    public RocketEventConsumer(String topic, String name, String connections, ContentCodec codec, RPCHook hook) {
        super(name);
        try {
            this.topic = topic;
            this.connections = connections;
            this.codec = codec;
            this.consumers = new ConcurrentHashMap<>();
            this.managers = new ConcurrentHashMap<>();
            this.hook = hook;
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
                    String tag = type.getName();
                    tag = tag.replace(StringUtility.DOT, StringUtility.DASH);
                    // 以name/topic/tag作为Group ID.
                    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(getGroup(type), hook, new AllocateMessageQueueAveragely(), hook != null ? true : false, null);
                    // 设置使用接入方式为阿里云，在使用云上消息轨迹的时候，需要设置此项，如果不开启消息轨迹功能，则运行不设置此项.
                    if (hook != null) {
                        consumer.setAccessChannel(AccessChannel.CLOUD);
                    }
                    consumer.setNamesrvAddr(connections);
                    consumer.setConsumeMessageBatchMaxSize(1000);
                    consumer.subscribe(topic, tag);
                    consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
                    consumer.setMessageModel(MessageModel.CLUSTERING);
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
    public void start() {

    }

    @Override
    public void stop() {
        for (MQPushConsumer cousumer : consumers.values()) {
            cousumer.shutdown();
        }
    }

    @Override
    public Collection<EventMonitor> getMonitors(Class type) {
        EventManager manager = managers.get(type);
        return manager == null ? Collections.EMPTY_SET : manager.getMonitors();
    }

    private String getGroup(Class type) {
        // 阿里云命名必须以GID_或GID-开始，且长度限制在7-64字符
        String tag = type.getSimpleName();
        tag = tag.replace(StringUtility.DOT, StringUtility.DASH);
        return String.join(StringUtility.DASH, "GID", name, topic, tag);
    }

}
