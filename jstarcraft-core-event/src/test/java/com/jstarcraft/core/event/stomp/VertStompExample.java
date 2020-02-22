package com.jstarcraft.core.event.stomp;

import org.junit.Test;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.stomp.StompClient;
import io.vertx.ext.stomp.StompClientConnection;

public class VertStompExample {

    @Test
    public void testQueue() throws Exception {
        Vertx vertx = Vertx.vertx();
        StompClient client = StompClient.create(vertx);

        client.connect(61613, "localhost", ar -> {
            if (ar.succeeded()) {
                StompClientConnection connection = ar.result();

                connection.subscribe("/queue/stomp", frame -> {
                    System.out.println("Destination : " + frame.getDestination());
                    System.out.println("Just received a frame from /queue : " + frame);

                    connection.disconnect(d -> System.out.println("Disconnected the STOMP server"));
                });

                connection.send("/queue/stomp", Buffer.buffer("World"));

            } else {
                System.out.println("Failed to connect to the STOMP server: " + ar.cause().toString());
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

        client.connect(61613, "localhost", ar -> {
            if (ar.succeeded()) {
                StompClientConnection connection = ar.result();

                connection.subscribe("/topic/stomp.#", frame -> {
                    System.out.println("Destination : " + frame.getDestination());
                    System.out.println("Just received a frame from /queue : " + frame);

                    connection.disconnect(d -> System.out.println("Disconnected the STOMP server"));
                });

                connection.send("/topic/stomp.test", Buffer.buffer("World"));

            } else {
                System.out.println("Failed to connect to the STOMP server: " + ar.cause().toString());
            }
        });

        Thread.sleep(5000);

        client.close();
        vertx.close();
    }

}
