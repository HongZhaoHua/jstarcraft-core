package com.jstarcraft.core.event.amqp;

import java.util.concurrent.CountDownLatch;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.junit.Assert;
import org.junit.Test;

public class QpidAmqpTestCase {

    private static final String content = "message";

    @Test
    public void testQueue() throws Exception {
        ConnectionFactory factory = new JmsConnectionFactory("amqp://localhost:5672");
        // Qpid的JMS 2.0 API(似乎有些问题.无法使用队列)
//        try (JMSContext context = factory.createContext()) {
//            Queue queue = context.createQueue("queue.amqp");
//
//            JMSProducer producer = context.createProducer();
//            producer.send(queue, content);
//
//            JMSConsumer consumer = context.createConsumer(queue);
//            Message message = consumer.receive(5000);
//
//            Assert.assertEquals(queue, message.getJMSDestination());
//            Assert.assertEquals(content, message.getBody(String.class));
//        }
        // Qpid的JMS 1.0 API
        Connection connection = factory.createConnection();
        connection.start();
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("queue.amqp");

            MessageProducer producer = session.createProducer(queue);
            producer.send(session.createTextMessage(content));

            MessageConsumer consumer = session.createConsumer(queue);
            Message message = consumer.receive(5000);

            Assert.assertEquals(queue, message.getJMSDestination());
            Assert.assertEquals(content, message.getBody(String.class));
        } finally {
            connection.close();
        }
    }

    @Test
    public void testTopic() throws Exception {
        ConnectionFactory factory = new JmsConnectionFactory("amqp://localhost:5672");
        // Qpid的JMS 2.0 API
//        try (JMSContext context = factory.createContext()) {
//            Topic topic = context.createTopic("topic.amqp.#");
//
//            CountDownLatch latch = new CountDownLatch(100);
//            for (int index = 0; index < 10; index++) {
//                JMSConsumer consumer = context.createConsumer(topic);
//                consumer.setMessageListener((message) -> {
//                    try {
//                        Topic channel = (Topic) message.getJMSDestination();
//                        Assert.assertTrue(channel.getTopicName().startsWith("topic.amqp"));
//                        Assert.assertEquals(content, message.getBody(String.class));
//                        latch.countDown();
//                    } catch (Exception exception) {
//                        Assert.fail();
//                    }
//                });
//            }
//
//            JMSProducer producer = context.createProducer();
//            for (int index = 0; index < 10; index++) {
//                Topic channel = context.createTopic("topic.amqp.test." + index);
//                producer.send(channel, content);
//            }
//
//            latch.await();
//        }
        // Qpid的JMS 1.0 API
        Connection connection = factory.createConnection();
        connection.start();
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("topic.amqp.#");

            CountDownLatch latch = new CountDownLatch(100);
            for (int index = 0; index < 10; index++) {
                MessageConsumer consumer = session.createConsumer(topic);
                consumer.setMessageListener((message) -> {
                    try {
                        Topic channel = (Topic) message.getJMSDestination();
                        Assert.assertTrue(channel.getTopicName().startsWith("topic.amqp"));
                        Assert.assertEquals(content, message.getBody(String.class));
                        latch.countDown();
                    } catch (Exception exception) {
                        Assert.fail();
                    }
                });
            }

            for (int index = 0; index < 10; index++) {
                Topic channel = session.createTopic("topic.amqp.test." + index);
                MessageProducer producer = session.createProducer(channel);
                producer.send(session.createTextMessage(content));
            }

            latch.await();
        } finally {
            connection.close();
        }
    }

}
