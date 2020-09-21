package com.jstarcraft.core.event.qmq;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import com.google.common.io.CharStreams;
import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.common.security.SecurityUtility;
import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

import qunar.tc.qmq.ListenerHolder;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.MessageConsumer;
import qunar.tc.qmq.MessageListener;
import qunar.tc.qmq.MessageProducer;
import qunar.tc.qmq.MessageSendStateListener;

/**
 * QMQ事件管道
 * 
 * @author Birdy
 *
 */
public class QmqEventChannel extends AbstractEventChannel {

    private MessageProducer producer;

    private MessageConsumer consumer;

    private ContentCodec codec;

    private ConcurrentMap<Class, ListenerHolder> holders;

    private class EventHandler implements MessageListener {

        private Class clazz;

        private EventHandler(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        public void onMessage(Message envelope) {
            try {
                String data = envelope.getLargeString("data");
                if (data == null) {
                    return;
                }
                byte[] bytes = SecurityUtility.decodeBase64(data);
                Object event = codec.decode(clazz, bytes);
                synchronized (clazz) {
                    EventManager manager = managers.get(clazz);
                    switch (mode) {
                    case QUEUE: {
                        int size = manager.getSize();
                        int index = RandomUtility.randomInteger(size);
                        EventMonitor monitor = manager.getMonitor(index);
                        try {
                            monitor.onEvent(event);
                        } catch (Exception exception) {
                            // 记录日志
                            String message = StringUtility.format("监控器[{}]处理QMQ事件[{}]时异常", monitor.getClass(), event.getClass());
                            logger.error(message, exception);
                        }
                        break;
                    }
                    case TOPIC: {
                        boolean throwable = false;
                        for (EventMonitor monitor : manager) {
                            try {
                                monitor.onEvent(event);
                            } catch (Exception exception) {
                                // 记录日志
                                String message = StringUtility.format("监控器[{}]处理QMQ事件[{}]时异常", monitor.getClass(), event.getClass());
                                logger.error(message, exception);

                            }
                        }
                        break;
                    }
                    }
                }
            } catch (Exception exception) {
                // 记录日志
                String message = StringUtility.format("编解码器[{}]处理QMQ事件[{}]时异常", codec.getClass(), envelope);
                logger.error(message, exception);
            }
        }

    };

    public QmqEventChannel(EventMode mode, String name, MessageProducer producer, MessageConsumer consumer, ContentCodec codec) {
        super(mode, name);
        this.producer = producer;
        this.consumer = consumer;
        this.codec = codec;
        this.holders = new ConcurrentHashMap<>();
    }

    private ListenerHolder getHolder(Class type) throws Exception {
        synchronized (type) {
            ListenerHolder holder = holders.get(type);
            if (holder == null) {
                String address = null;
                switch (mode) {
                case QUEUE: {
                    // 共享地址,不独占队列
                    address = name + StringUtility.DOT + type.getName();
                    break;
                }
                case TOPIC: {
                    // 不共享地址,独占队列
                    address = name + StringUtility.DOT + type.getName() + UUID.randomUUID();
                    break;
                }
                }
                String key = type.getName();
                EventHandler handler = new EventHandler(type);
                holder = consumer.addListener(key, address, handler, Executors.newFixedThreadPool(1));
                holders.put(type, holder);

                // 保证通道建立
                CountDownLatch latch = new CountDownLatch(1);
                Message envelope = producer.generateMessage(key);
                producer.sendMessage(envelope, new MessageSendStateListener() {

                    @Override
                    public void onSuccess(Message message) {
                        latch.countDown();
                    }

                    @Override
                    public void onFailed(Message message) {
                    }

                });
                latch.await();
                if (mode == EventMode.TOPIC) {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("action", "ResetOffset");
                    parameters.put("subject", key);
                    parameters.put("group", address);
                    parameters.put("code", String.valueOf(1));
                    management("127.0.0.1:8080", "token", parameters);
                }
            }
            return holder;
        }
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                synchronized (type) {
                    EventManager manager = managers.get(type);
                    if (manager == null) {
                        manager = new EventManager();
                        managers.put(type, manager);
                        ListenerHolder holder = getHolder(type);
                        holder.resumeListen();
                    }
                    manager.attachMonitor(monitor);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                synchronized (type) {
                    EventManager manager = managers.get(type);
                    if (manager != null) {
                        manager.detachMonitor(monitor);
                        if (manager.getSize() == 0) {
                            managers.remove(type);
                            ListenerHolder holder = getHolder(type);
                            holder.stopListen();
                        }
                    }
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public synchronized void triggerEvent(Object event) {
        try {
            Class type = event.getClass();
            String key = type.getName();
            byte[] bytes = codec.encode(type, event);
            String data = SecurityUtility.encodeBase64(bytes);
            Message envelope = producer.generateMessage(key);
            // QMQ提供的Message是key/value的形式
            envelope.setProperty("data", data);
            producer.sendMessage(envelope);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public String management(String metaServer, String token, Map<String, String> parameters) throws Exception {
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            final String url = String.format("http://%s/management", metaServer);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("X-Api-Token", token);
            connection.setRequestMethod("POST");
            writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StringUtility.CHARSET));
            writer.write(getParameters(parameters));
            writer.flush();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StringUtility.CHARSET));
            String content = CharStreams.toString(reader);
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("send request failed");
            }
            return content.trim();
        } catch (Exception exception) {
            throw new RuntimeException("send request meta server failed.", exception);
        } finally {
            closeQuietly(writer);
            closeQuietly(reader);
        }
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (Exception ignore) {
        }
    }

    private String getParameters(Map<String, String> parameters) throws UnsupportedEncodingException {
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, String> entry : parameters.entrySet()) {
            buffer.append(URLEncoder.encode(entry.getKey(), StringUtility.CHARSET.name()));
            buffer.append("=");
            buffer.append(URLEncoder.encode(entry.getValue(), StringUtility.CHARSET.name()));
            buffer.append("&");
        }
        return buffer.substring(0, buffer.length() - 1);
    }

}
