package com.jstarcraft.core.event.stomp;

import java.util.concurrent.CountDownLatch;

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
import io.vertx.ext.stomp.StompClient;
import io.vertx.ext.stomp.StompClientConnection;

public class StompEventChannelTestCase extends EventChannelTestCase {

    private Vertx vertx;

    private StompClient session;

    private StompClientConnection connection;

    @Before
    public void start() throws Exception {
        vertx = Vertx.vertx();
        session = StompClient.create(vertx);
        CountDownLatch latch = new CountDownLatch(1);
        session.connect(61613, "localhost", (connect) -> {
            connection = connect.result();
            latch.countDown();
        });
        latch.await();
    }

    @After
    public void stop() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        connection.disconnect((disconnect) -> {
            latch.countDown();
        });
        latch.await();
        session.close();
        vertx.close();
    }

    @Override
    protected EventChannel getEventChannel(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        return new StompEventChannel(mode, "STOMP" + mode, connection, codec);
    }

    @Override
    public void testTriggerQueueEvent() throws Exception {
        // TODO 无需测试队列模式
    }

}
