package com.jstarcraft.core.event;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.event.amqp.AmqpEventBusTestCase;
import com.jstarcraft.core.event.jms.JmsEventChannelTestCase;
import com.jstarcraft.core.event.memory.MemoryEventChannelTestCase;
import com.jstarcraft.core.event.mqtt.MqttEventChannelTestCase;
import com.jstarcraft.core.event.redis.RedisEventChannelTestCase;
import com.jstarcraft.core.event.rocketmq.RocketEventChannelTestCase;
import com.jstarcraft.core.event.stomp.StompEventChannelTestCase;
import com.jstarcraft.core.event.vertx.VertxEventChannelTestCase;

@RunWith(Suite.class)
@SuiteClasses({

        AmqpEventBusTestCase.class,

        JmsEventChannelTestCase.class,

        MemoryEventChannelTestCase.class,

        MqttEventChannelTestCase.class,

        RedisEventChannelTestCase.class,

        RocketEventChannelTestCase.class,

        StompEventChannelTestCase.class,

        VertxEventChannelTestCase.class,

})
public class EventChannelTestSuite {

}
