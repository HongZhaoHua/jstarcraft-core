package com.jstarcraft.core.event;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.event.amqp.AmqpEventChannelTestCase;
import com.jstarcraft.core.event.jms.JmsEventChannelTestCase;
import com.jstarcraft.core.event.kafka.KafkaEventChannelTestCase;
import com.jstarcraft.core.event.memory.MemoryEventChannelTestCase;
import com.jstarcraft.core.event.mqtt.MqttEventChannelTestCase;
import com.jstarcraft.core.event.rabbit.RabbitEventChannelTestCase;
import com.jstarcraft.core.event.redis.RedisEventChannelTestCase;
import com.jstarcraft.core.event.rocket.RocketEventChannelTestCase;
import com.jstarcraft.core.event.stomp.StompEventChannelTestCase;
import com.jstarcraft.core.event.vertx.VertxEventChannelTestCase;

@RunWith(Suite.class)
@SuiteClasses({

        AmqpEventChannelTestCase.class,

        JmsEventChannelTestCase.class,

        KafkaEventChannelTestCase.class,

        MemoryEventChannelTestCase.class,

        MqttEventChannelTestCase.class,

        RabbitEventChannelTestCase.class,

        RedisEventChannelTestCase.class,

        RocketEventChannelTestCase.class,

        StompEventChannelTestCase.class,

        VertxEventChannelTestCase.class,

})
public class EventChannelTestSuite {

}
