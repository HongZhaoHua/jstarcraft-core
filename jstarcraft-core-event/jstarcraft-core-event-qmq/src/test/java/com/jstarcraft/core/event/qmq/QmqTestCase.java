package com.jstarcraft.core.event.qmq;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import qunar.tc.qmq.Message;
import qunar.tc.qmq.MessageListener;
import qunar.tc.qmq.MessageSendStateListener;
import qunar.tc.qmq.consumer.MessageConsumerProvider;
import qunar.tc.qmq.producer.MessageProducerProvider;

public class QmqTestCase {

    @Test
    public void test() throws Exception {
        final String applicationCode = "QmqTestCase";
        final String metaAddress = "http://127.0.0.1:8080/meta/address";

        final String messageSubject = "QmqTestCase";
        final String messageKey = "key";
        final String messageValue = "value";

        AtomicInteger counter = new AtomicInteger();

        MessageProducerProvider producer = new MessageProducerProvider();
        producer.setAppCode(applicationCode);
        producer.setMetaServer(metaAddress);
        producer.init();
        // 每次发消息之前请使用generateMessage生成一个Message对象，然后填充数据
        Message message = producer.generateMessage(messageSubject);
        // QMQ提供的Message是key/value的形式
        message.setProperty(messageKey, messageValue);
        // 发送消息
        producer.sendMessage(message, new MessageSendStateListener() {

            @Override
            public void onSuccess(Message message) {
                Assert.assertEquals(messageValue, message.getStringProperty(messageKey));
                counter.incrementAndGet();
            }

            @Override
            public void onFailed(Message message) {
                counter.decrementAndGet();
            }

        });
        Thread.sleep(5000L);
        Assert.assertEquals(1, counter.get());

        counter.set(0);

        MessageConsumerProvider consumer = new MessageConsumerProvider();
        consumer.setAppCode(applicationCode);
        consumer.setMetaServer(metaAddress);
        consumer.init();
        // ThreadPoolExecutor根据实际业务场景进行配置
        consumer.addListener(messageSubject, "group", new MessageListener() {

            @Override
            public void onMessage(Message message) {
                Assert.assertEquals(messageValue, message.getStringProperty(messageKey));
                counter.incrementAndGet();
            }

        }, Executors.newFixedThreadPool(1));
        Thread.sleep(5000L);
        Assert.assertEquals(1, counter.get());
    }

}
