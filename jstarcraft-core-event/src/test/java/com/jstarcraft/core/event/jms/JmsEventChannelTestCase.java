package com.jstarcraft.core.event.jms;

import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventChannel;
import com.jstarcraft.core.event.EventChannelTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockEvent;
import com.jstarcraft.core.utility.StringUtility;

public class JmsEventChannelTestCase extends EventChannelTestCase {

    private ActiveMQConnectionFactory factory;

    @Before
    public void start() {
        factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    @After
    public void stop() throws Exception {
        JMSContext context = factory.createContext();
        Destination channel = context.createQueue(MockEvent.class.getName());
        JMSConsumer consumer = context.createConsumer(channel);
        // 清理测试消息
        logger.info("清理JMS测试消息开始");
        AtomicInteger count = new AtomicInteger();
        consumer.setMessageListener((data) -> {
            String message = StringUtility.format("清理JMS测试消息[{}]", count.incrementAndGet());
            logger.info(message);
        });
        Thread.sleep(1000L);
        logger.info("清理JMS测试消息结束");
        factory.close();
    }

    @Override
    protected EventChannel getEventChannel(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        return new JmsEventChannel(mode, "JMS" + mode, factory, codec);
    }

}
