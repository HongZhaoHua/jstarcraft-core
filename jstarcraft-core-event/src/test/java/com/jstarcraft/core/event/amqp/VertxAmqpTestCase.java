package com.jstarcraft.core.event.amqp;

import org.junit.Assert;
import org.junit.Test;

import io.vertx.amqp.AmqpClient;
import io.vertx.amqp.AmqpClientOptions;
import io.vertx.amqp.AmqpConnection;
import io.vertx.amqp.AmqpMessage;
import io.vertx.amqp.AmqpMessageBuilder;
import io.vertx.amqp.AmqpReceiver;
import io.vertx.amqp.AmqpSender;
import io.vertx.core.Vertx;

public class VertxAmqpTestCase {

    private static final String content = "message";

    @Test
    public void test() throws Exception {
        Vertx vertx = Vertx.vertx();
        AmqpClientOptions options = new AmqpClientOptions().setHost("localhost").setPort(5672);
        AmqpClient client = AmqpClient.create(vertx, options);

        client.connect((connect) -> {
            if (connect.succeeded()) {
                AmqpConnection connection = connect.result();
                // TODO 不知道应该如何指定QUEUE/TOPIC模式?
                connection.createSender("queue.amqp", (send) -> {
                    for (int index = 0; index < 5; index++) {
                        int id = index;
                        // TODO 不知道应该如何指定QUEUE/TOPIC模式?
                        connection.createReceiver("queue.amqp", (receive) -> {
                            AmqpReceiver receiver = receive.result();
                            receiver.handler((message) -> {
                                System.out.println(id + "_" + message.address() + "_" + message.bodyAsString());
                                Assert.assertEquals("queue.amqp", message.address());
                                Assert.assertEquals(content, message.bodyAsString());
                            });
                        });
                    }

                    AmqpSender sender = send.result();
                    AmqpMessageBuilder builder = AmqpMessage.create();
                    builder.withBody(content);
                    AmqpMessage message = builder.build();
                    sender.send(message);
                });
            } else {
                Assert.fail();
            }
        });

        Thread.sleep(5000);

        client.close((close) -> {
            vertx.close();
        });
    }

}
