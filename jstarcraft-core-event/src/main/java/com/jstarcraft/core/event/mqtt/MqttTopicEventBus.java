package com.jstarcraft.core.event.mqtt;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.jms.Message;
import javax.jms.MessageListener;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventBus;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.messages.MqttPublishMessage;

public class MqttTopicEventBus extends AbstractEventBus {

    private MqttClient session;

    private ContentCodec codec;

    private class EventHandler implements Handler<MqttPublishMessage> {

        @Override
        public void handle(MqttPublishMessage data) {
            String address = data.topicName();
            try {
                // TODO 需要防止路径冲突
                address = address.substring(mode.name().length());
                address = address.replace(StringUtility.FORWARD_SLASH, StringUtility.DOT);
                Class clazz = Class.forName(address);
                EventManager manager = address2Managers.get(clazz);
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

    public MqttTopicEventBus(MqttClient session, ContentCodec codec) {
        super(EventMode.TOPIC);
        this.session = session;
        this.codec = codec;
        EventHandler handler = new EventHandler();
        this.session.publishHandler(handler);
    }

    @Override
    public void registerMonitor(Set<Class<?>> addresses, EventMonitor monitor) {
        try {
            for (Class<?> address : addresses) {
                EventManager manager = address2Managers.get(address);
                if (manager == null) {
                    manager = new EventManager();
                    address2Managers.put(address, manager);
                    // TODO 需要防止路径冲突
                    CountDownLatch latch = new CountDownLatch(1);
                    session.subscribe(mode + address.getName(), MqttQoS.AT_MOST_ONCE.value(), (subscribe) -> {
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
    public void unregisterMonitor(Set<Class<?>> addresses, EventMonitor monitor) {
        try {
            for (Class<?> address : addresses) {
                EventManager manager = address2Managers.get(address);
                if (manager != null) {
                    manager.detachMonitor(monitor);
                    if (manager.getSize() == 0) {
                        address2Managers.remove(address);
                        // TODO 需要防止路径冲突
                        CountDownLatch latch = new CountDownLatch(1);
                        session.unsubscribe(mode + address.getName(), (subscribe) -> {
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
            Class<?> address = event.getClass();
            byte[] bytes = codec.encode(address, event);
            // TODO 需要防止路径冲突
            session.publish(mode + address.getName(), Buffer.buffer(bytes), MqttQoS.AT_MOST_ONCE, false, false);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
