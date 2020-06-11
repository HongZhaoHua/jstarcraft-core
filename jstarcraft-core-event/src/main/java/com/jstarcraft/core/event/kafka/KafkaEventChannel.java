package com.jstarcraft.core.event.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    @Deprecated
    private String addresses;

    private ContentCodec codec;

    private KafkaProducer producer;

    private ConcurrentMap<Class, KafkaConsumer> consumers;

    private ConcurrentMap<Class, EventThread> threads;

    private class EventThread extends Thread {

        private Class clazz;

        private EventManager manager;

        private KafkaConsumer consumer;

        private EventThread(Class clazz, EventManager manager, KafkaConsumer consumer) {
            this.clazz = clazz;
            this.manager = manager;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            while (true) {
                ConsumerRecords records = consumer.poll(Duration.ofMillis(1000));
                logger.info("records count = " + records.count());
                for (Iterator<ConsumerRecord<byte[], byte[]>> it = records.iterator(); it.hasNext();) {
                    ConsumerRecord<byte[], byte[]> record = it.next();
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

    public KafkaEventChannel(EventMode mode, String name, String addresses, ContentCodec codec) {
        super(mode, name);
        this.addresses = addresses;
        this.codec = codec;
        Properties props = new Properties();
        props.put("bootstrap.servers", addresses);
        props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        KafkaProducer producer = new KafkaProducer(props);
        this.producer = producer;
        this.consumers = new ConcurrentHashMap<>();
        this.threads = new ConcurrentHashMap<>();
    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        try {
            for (Class type : types) {
                EventManager manager = type2Managers.get(type);
                String consumerGroup = name + StringUtility.DOT + type.getName();
                consumerGroup = consumerGroup.replace(StringUtility.DOT, StringUtility.DASH);
                String topic = consumerGroup;
                if (manager == null) {
                    manager = new EventManager();
                    type2Managers.put(type, manager);
                    Properties props = new Properties();
                    props.put("bootstrap.servers", addresses);

                    switch (mode) {
                    case QUEUE: {
                        props.put("group.id", consumerGroup);
                        break;
                    }
                    case TOPIC: {
                        props.put("group.id", consumerGroup + UUID.randomUUID());
                        break;
                    }
                    }
                    props.put("auto.offset.reset", "earliest");
                    // 把auto.commit.offset设为false，让应用程序决定何时提交偏移量
                    props.put("auto.commit.offset", false);
                    props.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
                    props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
                    KafkaConsumer consumer = new KafkaConsumer<>(props);
                    List<String> topics = new ArrayList<>();
                    topics.add(topic);
                    consumer.subscribe(topics);
                    consumers.put(type, consumer);
                    EventThread thread = new EventThread(type, manager, consumer);
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
            EventManager manager = type2Managers.get(type);
            if (manager != null) {
                manager.detachMonitor(monitor);
                if (manager.getSize() == 0) {
                    type2Managers.remove(type);
                    KafkaConsumer consumer = consumers.remove(type);
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
            byte[] bytes = codec.encode(type, event);
            String topic = name + StringUtility.DOT + type.getName();
            topic = topic.replace(StringUtility.DOT, StringUtility.DASH);
            ProducerRecord record = new ProducerRecord<>(topic, bytes);
            producer.send(record);
            // 同步阻塞发送
//            Future<RecordMetadata> future = producer.send(record);
//            future.get();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
