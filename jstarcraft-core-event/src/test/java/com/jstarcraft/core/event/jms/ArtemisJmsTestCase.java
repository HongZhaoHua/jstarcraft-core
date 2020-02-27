package com.jstarcraft.core.event.jms;

import java.util.concurrent.CountDownLatch;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.Assert;
import org.junit.Test;

public class ArtemisJmsTestCase {

    private static final String content = "message";

    @Test
    public void testQueue() throws Exception {
        try (ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616"); JMSContext context = factory.createContext()) {
            Queue queue = context.createQueue("queue.jms");

            JMSProducer producer = context.createProducer();
            producer.send(queue, content);

            JMSConsumer consumer = context.createConsumer(queue);
            Message message = consumer.receive(5000);

            Assert.assertEquals(queue, message.getJMSDestination());
            Assert.assertEquals(content, message.getBody(String.class));
        }
    }

    @Test
    public void testTopic() throws Exception {
        try (ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616"); JMSContext context = factory.createContext()) {
            Topic topic = context.createTopic("topic.jms.#");

            CountDownLatch latch = new CountDownLatch(100);
            for (int index = 0; index < 10; index++) {
                JMSConsumer consumer = context.createConsumer(topic);
                consumer.setMessageListener((message) -> {
                    try {
                        Topic channel = (Topic) message.getJMSDestination();
                        Assert.assertTrue(channel.getTopicName().startsWith("topic.jms"));
                        Assert.assertEquals(content, message.getBody(String.class));
                        latch.countDown();
                    } catch (Exception exception) {
                        Assert.fail();
                    }
                });
            }

            JMSProducer producer = context.createProducer();
            for (int index = 0; index < 10; index++) {
                Topic channel = context.createTopic("topic.jms.test." + index);
                producer.send(channel, content);
            }

            latch.await();
        }
    }

}
