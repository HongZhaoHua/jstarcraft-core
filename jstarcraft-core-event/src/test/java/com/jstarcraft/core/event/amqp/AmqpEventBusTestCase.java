package com.jstarcraft.core.event.amqp;

import javax.jms.Connection;
import javax.jms.Session;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.junit.After;
import org.junit.Before;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventChannel;
import com.jstarcraft.core.event.EventChannelTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockEvent;

public class AmqpEventBusTestCase extends EventChannelTestCase {

    private JmsConnectionFactory factory;

    private Connection connection;

    @Before
    public void start() throws Exception {
        factory = new JmsConnectionFactory("amqp://localhost:5672");
        connection = factory.createConnection();
        connection.start();
    }

    @After
    public void stop() throws Exception {
//        Session session = connection.createSession();
//        Destination channel = session.createQueue(EventMode.QUEUE + MockEvent.class.getName());
//        MessageConsumer consumer = session.createConsumer(channel);
//        // 清理测试消息
//        logger.error("清理AMQP测试消息开始");
//        AtomicInteger count = new AtomicInteger();
//        consumer.setMessageListener((data) -> {
//            String message = StringUtility.format("清理AMQP测试消息[{}]", count.incrementAndGet());
//            logger.error(message);
//        });
//        Thread.sleep(1000L);
//        logger.error("清理AMQP测试消息结束");
        connection.stop();
        connection.close();
    }

    @Override
    protected EventChannel getEventChannel(EventMode mode) {
        try {
            Session session = connection.createSession();
            CodecDefinition definition = CodecDefinition.instanceOf(MockEvent.class);
            ContentCodec codec = new JsonContentCodec(definition);
            return new AmqpEventChannel(mode, "AMQP" + mode, session, codec);
        } catch (Exception exception) {
            return null;
        }
    }

}
