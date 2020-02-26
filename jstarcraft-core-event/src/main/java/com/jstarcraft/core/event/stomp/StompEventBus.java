package com.jstarcraft.core.event.stomp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventBus;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.stomp.Frame;
import io.vertx.ext.stomp.StompClientConnection;

public class StompEventBus extends AbstractEventBus {

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

    public StompEventBus(EventMode mode, StompClientConnection session, ContentCodec codec) {
        super(mode);
        this.session = session;
        this.codec = codec;
    }

    @Override
    public void registerMonitor(Set<Class> addresses, EventMonitor monitor) {
        try {
            for (Class address : addresses) {
                EventManager manager = address2Managers.get(address);
                if (manager == null) {
                    manager = new EventManager();
                    address2Managers.put(address, manager);
                    Map<String, String> metadatas = new HashMap<>();
                    // TODO 需要防止路径冲突
                    String destination = mode.name().toLowerCase() + StringUtility.FORWARD_SLASH + address.getName();
                    metadatas.put("destination", destination);
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
                    EventHandler handler = new EventHandler(address, manager);
                    session.subscribe(destination, metadatas, handler);
                }
                manager.attachMonitor(monitor);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class> addresses, EventMonitor monitor) {
        try {
            for (Class address : addresses) {
                EventManager manager = address2Managers.get(address);
                if (manager != null) {
                    manager.detachMonitor(monitor);
                    if (manager.getSize() == 0) {
                        address2Managers.remove(address);
                        Map<String, String> metadatas = new HashMap<>();
                        // TODO 需要防止路径冲突
                        String destination = mode.name().toLowerCase() + StringUtility.FORWARD_SLASH + address.getName();
                        metadatas.put("destination", destination);
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
                        session.unsubscribe(destination, metadatas);
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
            Class address = event.getClass();
            byte[] bytes = codec.encode(address, event);
            Map<String, String> metadatas = new HashMap<>();
            // TODO 需要防止路径冲突
            String destination = mode.name().toLowerCase() + StringUtility.FORWARD_SLASH + address.getName();
            metadatas.put("destination", destination);
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
