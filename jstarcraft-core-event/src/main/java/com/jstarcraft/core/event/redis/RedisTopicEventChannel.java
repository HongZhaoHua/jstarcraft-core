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

/**
 * Redis主题事件管道
 * 
 * @author Birdy
 *
 */
public class RedisTopicEventChannel extends RedisEventChannel {

    private ConcurrentMap<Class, EventHandler> type2Handlers;

    private ConcurrentMap<Class, RTopic> type2Topics;

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

    public RedisTopicEventChannel(String name, Redisson redisson, ContentCodec codec) {
        super(EventMode.TOPIC, name, redisson, codec);
        this.type2Handlers = new ConcurrentHashMap<>();
        this.type2Topics = new ConcurrentHashMap<>();
    }

    protected RTopic getTopic(Class type) {
        RTopic topic = type2Topics.get(type);
        if (topic == null) {
            topic = redisson.getTopic(name + StringUtility.DOT + type.getName(), byteCodec);
            type2Topics.put(type, topic);
        }
        return topic;
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        for (Class type : types) {
            EventManager manager = type2Managers.get(type);
            if (manager == null) {
                manager = new EventManager();
                type2Managers.put(type, manager);
                // TODO 需要防止路径冲突
                RTopic topic = getTopic(type);
                EventHandler handler = new EventHandler(type, manager);
                topic.addListener(byte[].class, handler);
                type2Handlers.put(type, handler);
            }
            manager.attachMonitor(monitor);
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
                    // TODO 需要防止路径冲突
                    RTopic topic = getTopic(type);
                    EventHandler handler = type2Handlers.remove(type);
                    topic.removeListener(handler);
                }
            }
        }
    }

    @Override
    public void triggerEvent(Object event) {
        Class type = event.getClass();
        // TODO 需要防止路径冲突
        RTopic topic = getTopic(type);
        byte[] bytes = codec.encode(type, event);
        topic.publish(bytes);
    }

}
