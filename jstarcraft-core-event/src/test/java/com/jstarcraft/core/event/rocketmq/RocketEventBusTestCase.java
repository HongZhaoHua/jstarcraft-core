package com.jstarcraft.core.event.rocketmq;

import org.junit.After;
import org.junit.Before;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventBus;
import com.jstarcraft.core.event.EventBusTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockEvent;
import com.jstarcraft.core.event.rocket.RocketEventBus;

public class RocketEventBusTestCase extends EventBusTestCase {

    @Before
    public void start() {
    }

    @After
    public void stop() {
        bus.stop();
    }

    private RocketEventBus bus;

    @Override
    protected EventBus getEventBus(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        bus = new RocketEventBus(mode, "Rocket" + mode, "localhost:9876", codec);
        return bus;
    }

}
