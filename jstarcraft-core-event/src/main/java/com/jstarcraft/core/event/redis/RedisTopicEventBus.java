package com.jstarcraft.core.event.redis;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.StringUtility;

public class RedisTopicEventBus extends RedisEventBus {

    private ConcurrentMap<Class, EventHandler> address2Handlers;

    private ConcurrentMap<Class, RTopic> address2Topics;

    private class EventHandler implements MessageListener<byte[]> {

        private Class clazz;

        private EventManager manager;

        private EventHandler(Class clazz, EventManager manager) {
            this.clazz = clazz;
            this.manager = manager;
        }

        @Override
        public void onMessage(CharSequence channel, byte[] data) {
            try {
                Object event = codec.decode(clazz, data);
                for (EventMonitor monitor : manager) {
                    try {
                        monitor.onEvent(event);
                    } catch (Exception exception) {
                        // 记录日志
                        String message = StringUtility.format("监控器[{}]处理Redis事件[{}]时异常", monitor.getClass(), data);
                        logger.error(message, exception);
                    }
                }
            } catch (Exception exception) {
                // 记录日志
                String message = StringUtility.format("编解码器[{}]处理Redis事件[{}]时异常", codec.getClass(), data);
                logger.error(message, exception);
            }
        }

    };

    public RedisTopicEventBus(String name, Redisson redisson, ContentCodec codec) {
        super(EventMode.TOPIC, name, redisson, codec);
        this.address2Handlers = new ConcurrentHashMap<>();
        this.address2Topics = new ConcurrentHashMap<>();
    }

    protected RTopic getTopic(Class address) {
        RTopic topic = address2Topics.get(address);
        if (topic == null) {
            topic = redisson.getTopic(name + StringUtility.DOT + address.getName(), byteCodec);
            address2Topics.put(address, topic);
        }
        return topic;
    }

    @Override
    public void registerMonitor(Set<Class> addresses, EventMonitor monitor) {
        for (Class address : addresses) {
            EventManager manager = address2Managers.get(address);
            if (manager == null) {
                manager = new EventManager();
                address2Managers.put(address, manager);
                // TODO 需要防止路径冲突
                RTopic topic = getTopic(address);
                EventHandler handler = new EventHandler(address, manager);
                topic.addListener(byte[].class, handler);
                address2Handlers.put(address, handler);
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
                    // TODO 需要防止路径冲突
                    RTopic topic = getTopic(address);
                    EventHandler handler = address2Handlers.remove(address);
                    topic.removeListener(handler);
                }
            }
        }
    }

    @Override
    public void triggerEvent(Object event) {
        Class address = event.getClass();
        // TODO 需要防止路径冲突
        RTopic topic = getTopic(address);
        byte[] bytes = codec.encode(address, event);
        topic.publish(bytes);
    }

}
