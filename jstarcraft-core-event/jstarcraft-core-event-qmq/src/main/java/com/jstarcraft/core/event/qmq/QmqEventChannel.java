package com.jstarcraft.core.event.qmq;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
import qunar.tc.qmq.MessageListener;
import qunar.tc.qmq.MessageSendStateListener;
import qunar.tc.qmq.consumer.MessageConsumerProvider;
import qunar.tc.qmq.producer.MessageProducerProvider;

/**
 * QMQ事件管道
 * 
 * @author Birdy
 *
 */
public class QmqEventChannel extends AbstractEventChannel {

    private static final String metaUrl = "http://{}:{}/meta/address";

    private static final String managementUrl = "http://{}:{}/management";

    private static final RestTemplate template = new RestTemplate();

    private String host;

    private int port;

    private String token;

    private MessageProducerProvider producer;

    private MessageConsumerProvider consumer;

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
                String context = envelope.getStringProperty(CONTEXT);
                if (context != null) {
                    if (setter != null) {
                        setter.accept(context);
                    }
                }
                String data = envelope.getStringProperty(DATA);
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

    public QmqEventChannel(EventMode mode, String name, String host, int port, String token, ContentCodec codec) {
        super(mode, name);
        this.host = host;
        this.port = port;
        this.token = token;
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
                if (mode == EventMode.TOPIC) {
                    // 使用空消息触发通道建立
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
                    // 设置通道(LATEST=1, EARLIEST=2)
                    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
                    parameters.add("action", "ResetOffset");
                    parameters.add("subject", key);
                    parameters.add("group", address);
                    parameters.add("code", String.valueOf(1));
                    String url = StringUtility.format(managementUrl, host, port);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    headers.add("X-Api-Token", token);
                    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);
                    ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, request, String.class);
                    logger.info(response.getBody());
                }
                EventHandler handler = new EventHandler(type);
                holder = consumer.addListener(key, address, handler, Executors.newFixedThreadPool(1));
                holders.put(type, holder);
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
            // QMQ的Message.setProperty(key, value)的大小默认不能超过32K
            envelope.setProperty(DATA, data);
            if (getter != null) {
                String context = getter.get();
                if (context != null) {
                    envelope.setProperty(CONTEXT, context);
                }
            }
            producer.sendMessage(envelope);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public synchronized void start() {
        String url = StringUtility.format(metaUrl, host, port);
        producer = new MessageProducerProvider();
        producer.setAppCode(name);
        producer.setMetaServer(url);
        producer.init();
        consumer = new MessageConsumerProvider();
        consumer.setAppCode(name);
        consumer.setMetaServer(url);
        consumer.init();
    }

    @Override
    public synchronized void stop() {
        producer.destroy();
        consumer.destroy();
    }

}
