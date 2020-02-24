package com.jstarcraft.core.event.vertx;

import org.junit.After;
import org.junit.Before;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventBus;
import com.jstarcraft.core.event.EventBusTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockEvent;

import io.vertx.core.Vertx;

public class VertxEventBusTestCase extends EventBusTestCase {

    private Vertx vertx;

    @Before
    public void start() throws Exception {
        vertx = Vertx.vertx();
    }

    @After
    public void stop() throws Exception {
        vertx.close();
    }

    @Override
    protected EventBus getEventBus(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        return new VertxEventBus(mode, vertx.eventBus(), codec);
    }

}
