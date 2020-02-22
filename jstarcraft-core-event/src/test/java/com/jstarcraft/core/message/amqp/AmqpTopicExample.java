package com.jstarcraft.core.message.amqp;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.qpid.jms.JmsConnectionFactory;

public class AmqpTopicExample {

    public static void main(String[] args) throws Exception {
        Connection connection = null;
        ConnectionFactory connectionFactory = new JmsConnectionFactory("amqp://localhost:5672");

        try {

            // Step 1. Create an amqp qpid 1.0 connection
            connection = connectionFactory.createConnection();
            connection.start();

            // Step 2. Create a session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            {
                // Step 3. Create a sender
                Topic topic = session.createTopic("topic.amqp.#");

                for (int index = 0; index < 5; index++) {
                    int id = index;
                    // Step 5. create a moving receiver, this means the message will be removed from
                    // the queue
                    MessageConsumer consumer = session.createConsumer(topic);
                    // Step 7. receive the simple message
                    consumer.setMessageListener((message) -> {
                        try {
                            Topic t = (Topic) message.getJMSDestination();
                            System.out.println(t.getTopicName());
                            TextMessage m = (TextMessage) message;
                            System.out.println(id + ": message = " + m.getText());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                }
            }

            {
                for (int index = 0; index < 10; index++) {
                    Topic topic = session.createTopic("topic.amqp.test." + index);
                    MessageProducer sender = session.createProducer(topic);

                    // Step 4. send a few simple message
                    sender.send(session.createTextMessage("Hello world " + index));
                }
            }

            Thread.sleep(1000L);
        } finally {
            if (connection != null) {
                // Step 9. close the connection
                connection.close();
            }
        }
    }
}
