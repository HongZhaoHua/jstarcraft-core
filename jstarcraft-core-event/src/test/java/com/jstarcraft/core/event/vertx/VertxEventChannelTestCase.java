package com.jstarcraft.core.event.vertx;

import org.junit.After;
import org.junit.Before;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventChannel;
import com.jstarcraft.core.event.EventChannelTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockEvent;

import io.vertx.core.Vertx;

public class VertxEventChannelTestCase extends EventChannelTestCase {

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
    protected EventChannel getEventChannel(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        return new VertxEventChannel(mode, "VERTX" + mode, vertx.eventBus(), codec);
    }

}
