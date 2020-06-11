package com.jstarcraft.core.event.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventManager;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.EventMonitor;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Kafka事件管道
 * 
 * @author Henser
 *
 */
public class KafkaEventChannel extends AbstractEventChannel {

    private static final Class<? extends Serializer<?>> keySerializer = StringSerializer.class;

    private static final Class<? extends Serializer<?>> valueSerializer = ByteArraySerializer.class;

    private static final Class<? extends Deserializer<?>> keyDeserializer = StringDeserializer.class;

    private static final Class<? extends Deserializer<?>> valueDeserializer = ByteArrayDeserializer.class;

    private String connections;

    private ContentCodec codec;

    private KafkaProducer<String, byte[]> producer;

    private ConcurrentMap<Class, KafkaConsumer<String, byte[]>> consumers;

    private ConcurrentMap<Class, EventThread> threads;

    private class EventThread extends Thread {

        private Class clazz;

        private EventManager manager;

        private EventThread(Class clazz, EventManager manager) {
            this.clazz = clazz;
            this.manager = manager;
        }

        @Override
        public void run() {
            KafkaConsumer<String, byte[]> consumer = consumers.get(clazz);
            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000L));
                logger.info("records count = " + records.count());
                for (ConsumerRecord<String, byte[]> record : records) {
                    byte[] bytes = record.value();
                    logger.info("topic = " + record.topic() + ", partition = " + record.partition() + ", value = " + codec.decode(clazz, bytes));
                    try {
                        Object event = codec.decode(clazz, bytes);
                        switch (mode) {
                        case QUEUE: {
                            int size = manager.getSize();
                            int index = RandomUtility.randomInteger(size);
                            EventMonitor monitor = manager.getMonitor(index);
                            try {
                                monitor.onEvent(event);
                            } catch (Exception exception) {
                                // 记录日志
                                String message = StringUtility.format("监控器[{}]处理Redis事件[{}]时异常", monitor.getClass(), bytes);
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
                                    String message = StringUtility.format("监控器[{}]处理Rocket事件[{}]时异常", monitor.getClass(), event.getClass());
                                    logger.error(message, exception);
                                }
                            }
                            break;
                        }
                        }
                    } catch (Exception exception) {
                        // 记录日志
                        String message = StringUtility.format("编解码器[{}]处理Redis事件[{}]时异常", codec.getClass(), bytes);
                        logger.error(message, exception);
                    } finally {
                        consumer.commitSync();
                    }
                }
            }
        }
    };

    public KafkaEventChannel(EventMode mode, String name, String connections, ContentCodec codec) {
        super(mode, name);
        this.connections = connections;
        this.codec = codec;
        Properties properties = new Properties();
        properties.put("bootstrap.servers", connections);
        properties.put("key.serializer", keySerializer);
        properties.put("value.serializer", valueSerializer);
        KafkaProducer producer = new KafkaProducer(properties);
        this.producer = producer;
        this.consumers = new ConcurrentHashMap<>();
        this.threads = new ConcurrentHashMap<>();
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                EventManager manager = managers.get(type);
                String group = name + StringUtility.DOT + type.getName();
                if (manager == null) {
                    manager = new EventManager();
                    managers.put(type, manager);
                    Properties properties = new Properties();
                    properties.put("bootstrap.servers", connections);
                    properties.put("key.deserializer", keyDeserializer);
                    properties.put("value.deserializer", valueDeserializer);
                    switch (mode) {
                    case QUEUE: {
                        properties.put("group.id", group);
                        break;
                    }
                    case TOPIC: {
                        properties.put("group.id", group + UUID.randomUUID());
                        break;
                    }
                    }
                    properties.put("auto.offset.reset", "earliest");
                    // 把auto.commit.offset设为false，让应用程序决定何时提交偏移量
                    properties.put("auto.commit.offset", false);
                    KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(properties);
                    consumer.subscribe(Collections.singleton(group));
                    consumers.put(type, consumer);
                    EventThread thread = new EventThread(type, manager);
                    thread.start();
                    threads.put(type, thread);
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
                    KafkaConsumer<String, byte[]> consumer = consumers.remove(type);
                    consumer.unsubscribe();
                    consumer.close();
                    EventThread thread = threads.remove(type);
                    thread.interrupt();
                }
            }
        }
    }

    @Override
    public void triggerEvent(Object event) {
        try {
            Class type = event.getClass();
            String group = name + StringUtility.DOT + type.getName();
            byte[] bytes = codec.encode(type, event);
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(group, bytes);
            producer.send(record);
            // 同步阻塞发送
//            Future<RecordMetadata> future = producer.send(record);
//            future.get();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
