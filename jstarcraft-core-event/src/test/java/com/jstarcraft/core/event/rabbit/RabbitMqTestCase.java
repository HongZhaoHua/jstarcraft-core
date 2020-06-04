package com.jstarcraft.core.event.rabbit;

import org.junit.Assert;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMq;
import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMqConfig;

public class RabbitMqTestCase {

    @Test
    public void test() throws Exception {
        EmbeddedRabbitMqConfig configuration = new EmbeddedRabbitMqConfig.Builder()
                // 时间过短会启动失败
                .rabbitMqServerInitializationTimeoutInMillis(60000)
                // 时间过短会停止失败
                .defaultRabbitMqCtlTimeoutInMillis(60000)

                .build();
        EmbeddedRabbitMq rabbitMq = new EmbeddedRabbitMq(configuration);
        try {
            rabbitMq.start();

            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            connectionFactory.setVirtualHost("/");
            connectionFactory.setUsername("guest");
            connectionFactory.setPassword("guest");

            Connection connection = connectionFactory.newConnection();
            Assert.assertTrue(connection.isOpen());
            Channel channel = connection.createChannel();
            Assert.assertTrue(channel.isOpen());
            channel.close();
            Assert.assertFalse(channel.isOpen());
            connection.close();
            Assert.assertFalse(connection.isOpen());
        } finally {
            rabbitMq.stop();
        }
    }

}
