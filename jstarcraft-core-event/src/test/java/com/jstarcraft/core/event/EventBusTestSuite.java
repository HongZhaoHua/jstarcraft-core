package com.jstarcraft.core.event;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.event.amqp.AmqpEventBusTestCase;
import com.jstarcraft.core.event.jms.JmsEventBusTestCase;
import com.jstarcraft.core.event.memory.AsynchronousEventBusTestCase;
import com.jstarcraft.core.event.memory.SynchronousEventBusTestCase;
import com.jstarcraft.core.event.mqtt.MqttEventBusTestCase;
import com.jstarcraft.core.event.redis.RedisEventBusTestCase;
import com.jstarcraft.core.event.stomp.StompEventBusTestCase;

@RunWith(Suite.class)
@SuiteClasses({

        AmqpEventBusTestCase.class,

        JmsEventBusTestCase.class,

        AsynchronousEventBusTestCase.class,

        SynchronousEventBusTestCase.class,

        MqttEventBusTestCase.class,

        RedisEventBusTestCase.class,

        StompEventBusTestCase.class,

})
public class EventBusTestSuite {

}
