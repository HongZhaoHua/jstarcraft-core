package com.jstarcraft.core.event.amqp;

import java.util.concurrent.CountDownLatch;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.junit.Assert;
import org.junit.Test;

public class QpidAmqpTestCase {

    private static final String content = "message";

    @Test
    public void testQueue() throws Exception {
        ConnectionFactory factory = new JmsConnectionFactory("amqp://localhost:5672");
        try (JMSContext context = factory.createContext()) {
            Queue queue = context.createQueue("queue.amqp");

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
        ConnectionFactory factory = new JmsConnectionFactory("amqp://localhost:5672");
        try (JMSContext context = factory.createContext()) {
            Topic topic = context.createTopic("topic.amqp.#");

            CountDownLatch latch = new CountDownLatch(100);
            for (int index = 0; index < 10; index++) {
                JMSConsumer consumer = context.createConsumer(topic);
                consumer.setMessageListener((message) -> {
                    try {
                        Topic destination = (Topic) message.getJMSDestination();
                        Assert.assertTrue(destination.getTopicName().startsWith("topic.amqp"));
                        Assert.assertEquals(content, message.getBody(String.class));
                        latch.countDown();
                    } catch (Exception exception) {
                        Assert.fail();
                    }
                });
            }

            JMSProducer producer = context.createProducer();
            for (int index = 0; index < 10; index++) {
                Topic destination = context.createTopic("topic.amqp.test." + index);
                producer.send(destination, content);
            }

            latch.await();
        }
    }

}
