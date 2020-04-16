package com.jstarcraft.core.event.stomp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.stomp.Frame;
import io.vertx.ext.stomp.StompClientConnection;

/**
 * STOMP事件管道
 * 
 * @author Birdy
 *
 */
// TODO 目前存在一个Bug,消费者注册Queue模式的时候,会变成Topic.
public class StompEventChannel extends AbstractEventChannel {

    private StompClientConnection session;

    private ContentCodec codec;

    private class EventHandler implements Handler<Frame> {

        private Class clazz;

        private EventManager manager;

        private EventHandler(Class clazz, EventManager manager) {
            this.clazz = clazz;
            this.manager = manager;
        }

        @Override
        public void handle(Frame data) {
            try {
                byte[] bytes = data.getBodyAsByteArray();
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
                            String message = StringUtility.format("监控器[{}]处理JMS事件[{}]时异常", monitor.getClass(), event.getClass());
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
                                String message = StringUtility.format("监控器[{}]处理JMS事件[{}]时异常", monitor.getClass(), event.getClass());
                                logger.error(message, exception);
                            }
                        }
                        break;
                    }
                    }
                }
            } catch (Exception exception) {
                // 记录日志
                String message = StringUtility.format("编解码器[{}]处理JMS事件[{}]时异常", codec.getClass(), data);
                logger.error(message, exception);
            }
        }

    };

    public StompEventChannel(EventMode mode, String name, StompClientConnection session, ContentCodec codec) {
        super(mode, name);
        this.session = session;
        this.codec = codec;
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                EventManager manager = type2Managers.get(type);
                if (manager == null) {
                    manager = new EventManager();
                    type2Managers.put(type, manager);
                    Map<String, String> metadatas = new HashMap<>();
                    // TODO 需要防止路径冲突
                    String address = name + StringUtility.DOT + type.getName();
                    metadatas.put("destination", address);
                    switch (mode) {
                    case QUEUE: {
                        // Artemis特定的协议
                        metadatas.put("destination-type", "ANYCAST");
                        break;
                    }
                    case TOPIC: {
                        // Artemis特定的协议
                        metadatas.put("destination-type", "MULTICAST");
                        break;
                    }
                    }
                    EventHandler handler = new EventHandler(type, manager);
                    session.subscribe(address, metadatas, handler);
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
                        Map<String, String> metadatas = new HashMap<>();
                        // TODO 需要防止路径冲突
                        String address = name + StringUtility.DOT + type.getName();
                        metadatas.put("destination", address);
                        switch (mode) {
                        case QUEUE: {
                            // Artemis特定的协议
                            metadatas.put("destination-type", "ANYCAST");
                            break;
                        }
                        case TOPIC: {
                            // Artemis特定的协议
                            metadatas.put("destination-type", "MULTICAST");
                            break;
                        }
                        }
                        session.unsubscribe(address, metadatas);
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
            Map<String, String> metadatas = new HashMap<>();
            // TODO 需要防止路径冲突
            String channel = name + StringUtility.DOT + type.getName();
            metadatas.put("destination", channel);
            switch (mode) {
            case QUEUE: {
                // Artemis特定的协议
                metadatas.put("destination-type", "ANYCAST");
                break;
            }
            case TOPIC: {
                // Artemis特定的协议
                metadatas.put("destination-type", "MULTICAST");
                break;
            }
            }
            session.send(metadatas, Buffer.buffer(bytes));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
