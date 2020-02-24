package com.jstarcraft.core.event.stomp;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.stomp.StompClient;
import io.vertx.ext.stomp.StompClientConnection;

public class VertxStompTestCase {

    private static final String content = "message";

    @Test
    public void testQueue() throws Exception {
        Vertx vertx = Vertx.vertx();
        StompClient client = StompClient.create(vertx);

        client.connect(61613, "localhost", (connect) -> {
            if (connect.succeeded()) {
                StompClientConnection connection = connect.result();
                connection.subscribe("/queue/stomp", (frame) -> {
                    System.out.println(frame.getDestination());
                    Assert.assertEquals("/queue/stomp", frame.getDestination());
                    Assert.assertEquals(content, frame.getBodyAsString());
                    connection.disconnect();
                });
                Map<String, String> headers = new HashMap<>();
                headers.put("destination", "/queue/stomp");
                headers.put("destination-type", "ANYCAST");
                connection.send(headers, Buffer.buffer(content));
                connection.send(headers, Buffer.buffer(content));
            } else {
                Assert.fail();
            }
        });

        Thread.sleep(5000);

        client.close();
        vertx.close();
    }

    @Test
    public void testTopic() throws Exception {
        Vertx vertx = Vertx.vertx();
        StompClient client = StompClient.create(vertx);

        client.connect(61613, "localhost", (connect) -> {
            if (connect.succeeded()) {
                StompClientConnection connection = connect.result();
                connection.subscribe("/topic/stomp.#", (frame) -> {
                    System.out.println(frame.getDestination());
                    Assert.assertEquals("/topic/stomp.test", frame.getDestination());
                    Assert.assertEquals(content, frame.getBodyAsString());
                    connection.disconnect();
                });
                Map<String, String> headers = new HashMap<>();
                headers.put("destination", "/topic/stomp.test");
                headers.put("destination-type", "MULTICAST");
                connection.send(headers, Buffer.buffer(content));
                connection.send(headers, Buffer.buffer(content));
            } else {
                Assert.fail();
            }
        });

        Thread.sleep(5000);

        client.close();
        vertx.close();
    }

}
