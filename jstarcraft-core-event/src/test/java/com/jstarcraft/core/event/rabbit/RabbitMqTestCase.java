package com.jstarcraft.core.event.rabbit;

import org.junit.Test;

import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMq;
import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMqConfig;

public class RabbitMqTestCase {

    @Test
    public void test() {
        EmbeddedRabbitMqConfig config = new EmbeddedRabbitMqConfig.Builder().build();
        EmbeddedRabbitMq rabbitMq = new EmbeddedRabbitMq(config);
        rabbitMq.start();

        rabbitMq.stop();
    }

}
