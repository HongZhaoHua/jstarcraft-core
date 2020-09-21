package com.jstarcraft.core.event.rabbit;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventChannel;
import com.jstarcraft.core.event.EventChannelTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockBroadcastEvent;
import com.jstarcraft.core.event.MockUnicastEvent;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMq;
import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMqConfig;

public class RabbitEventChannelTestCase extends EventChannelTestCase {

    private static EmbeddedRabbitMq rabbitMq;

    private static Connection connection;

    private static Channel channel;

    static {
        EmbeddedRabbitMqConfig configuration = new EmbeddedRabbitMqConfig.Builder()
                // 时间过短会启动失败
                .rabbitMqServerInitializationTimeoutInMillis(60000)
                // 时间过短会停止失败
                .defaultRabbitMqCtlTimeoutInMillis(60000)

                .build();
        rabbitMq = new EmbeddedRabbitMq(configuration);
    }

    @BeforeClass
    public static void start() throws Exception {
        rabbitMq.start();
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
    }

    @AfterClass
    public static void stop() throws Exception {
        channel.close();
        connection.close();
        rabbitMq.stop();
    }

    @Override
    protected EventChannel getEventChannel(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockUnicastEvent.class, MockBroadcastEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        return new RabbitEventChannel(mode, "RabbitMQ" + mode, channel, codec);
    }

}
