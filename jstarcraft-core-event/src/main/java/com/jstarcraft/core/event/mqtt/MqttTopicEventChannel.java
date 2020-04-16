package com.jstarcraft.core.event.mqtt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.StringUtility;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.messages.MqttPublishMessage;

/**
 * MQTT主题事件管道
 * 
 * @author Birdy
 *
 */
public class MqttTopicEventChannel extends AbstractEventChannel {

    private Map<String, Class> address2Classes = new HashMap<>();

    private MqttClient session;

    private ContentCodec codec;

    private class EventHandler implements Handler<MqttPublishMessage> {

        @Override
        public void handle(MqttPublishMessage data) {
            String address = data.topicName();
            try {
                // TODO 需要防止路径冲突
                address = address.substring(name.length() + 1);
                address = address.replace(StringUtility.FORWARD_SLASH, StringUtility.DOT);
                Class clazz = address2Classes.get(address);
                EventManager manager = type2Managers.get(clazz);
                Object event = codec.decode(clazz, data.payload().getBytes());
                synchronized (manager) {
                    for (EventMonitor monitor : manager) {
                        try {
                            monitor.onEvent(event);
                        } catch (Exception exception) {
                            // 记录日志
                            String message = StringUtility.format("监控器[{}]处理MQTT事件[{}]时异常", monitor.getClass(), event.getClass());
                            logger.error(message, exception);
                        }
                    }
                }
            } catch (Exception exception) {
                // 记录日志
                String message = StringUtility.format("编解码器[{}]处理MQTT事件[{}]时异常", codec.getClass(), data);
                logger.error(message, exception);
            }
        }

    };

    public MqttTopicEventChannel(String name, MqttClient session, ContentCodec codec) {
        super(EventMode.TOPIC, name);
        this.session = session;
        this.codec = codec;
        EventHandler handler = new EventHandler();
        this.session.publishHandler(handler);
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                EventManager manager = type2Managers.get(type);
                if (manager == null) {
                    manager = new EventManager();
                    type2Managers.put(type, manager);
                    address2Classes.put(type.getName(), type);
                    // TODO 需要防止路径冲突
                    CountDownLatch latch = new CountDownLatch(1);
                    session.subscribe(name + StringUtility.DOT + type.getName(), MqttQoS.AT_MOST_ONCE.value(), (subscribe) -> {
                        latch.countDown();
                    });
                    latch.await();
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
                        address2Classes.remove(type.getName());
                        // TODO 需要防止路径冲突
                        CountDownLatch latch = new CountDownLatch(1);
                        session.unsubscribe(name + StringUtility.DOT + type.getName(), (subscribe) -> {
                            latch.countDown();
                        });
                        latch.await();
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
            byte[] bytes = codec.encode(type, event);
            // TODO 需要防止路径冲突
            session.publish(name + StringUtility.DOT + type.getName(), Buffer.buffer(bytes), MqttQoS.AT_MOST_ONCE, false, false);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
