package com.jstarcraft.core.event.rocket;

import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.RPCHook;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventProducer;
import com.jstarcraft.core.utility.StringUtility;

/**
 * RocketMQ事件生产者
 * 
 * @author Birdy
 *
 */
public class RocketEventProducer extends AbstractEventProducer {

    public static final String STARTDELIVERTIME = "__STARTDELIVERTIME";

    /** 主题 */
    private String topic;

    /** 服务名 */
    private String name;

    private ContentCodec codec;

    private MQProducer producer;

    public RocketEventProducer(String topic, String name, String connections, ContentCodec codec) {
        this(topic, name, connections, codec, null);
    }

    public RocketEventProducer(String topic, String name, String connections, ContentCodec codec, RPCHook hook) {
        super(name);
        try {
            this.topic = topic;
            this.codec = codec;
            DefaultMQProducer producer = new DefaultMQProducer(name, hook, hook != null ? true : false, null);
            // 设置使用接入方式为阿里云，在使用云上消息轨迹的时候，需要设置此项，如果不开启消息轨迹功能，则运行不设置此项.
            if (hook != null) {
                producer.setAccessChannel(AccessChannel.CLOUD);
            }
            producer.setNamesrvAddr(connections);
            this.producer = producer;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void start() {
        try {
            producer.start();
        } catch (MQClientException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void stop() {
        producer.shutdown();
    }

    @Override
    public void triggerEvent(Object event) {
        try {
            Class<?> type = event.getClass();
            String tag = type.getName();
            tag = tag.replace(StringUtility.DOT, StringUtility.DASH);
            byte[] bytes = codec.encode(type, event);
            Message message = new Message(topic, tag, bytes);
            producer.send(message);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void triggerEvent(Object event, long instant) {
        try {
            Class<?> type = event.getClass();
            String tag = type.getName();
            tag = tag.replace(StringUtility.DOT, StringUtility.DASH);
            byte[] bytes = codec.encode(type, event);
            Message message = new Message(topic, tag, bytes);
            message.putUserProperty(STARTDELIVERTIME, String.valueOf(instant));
            producer.send(message);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
