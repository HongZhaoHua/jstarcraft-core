package com.jstarcraft.core.event;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.event.jms.JmsEventBusTestCase;
import com.jstarcraft.core.event.memory.AsynchronousEventBusTestCase;
import com.jstarcraft.core.event.memory.SynchronousEventBusTestCase;
import com.jstarcraft.core.event.redis.RedisEventBusTestCase;

@RunWith(Suite.class)
@SuiteClasses({

        JmsEventBusTestCase.class,

        AsynchronousEventBusTestCase.class,

        SynchronousEventBusTestCase.class,

        RedisEventBusTestCase.class,

})
public class EventBusTestSuite {

}
