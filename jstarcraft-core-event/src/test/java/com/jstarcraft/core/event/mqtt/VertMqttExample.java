package com.jstarcraft.core.event.mqtt;

import org.junit.Test;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;

/**
 * An example of using the MQTT client as a verticle
 */
public class VertMqttExample {

    private static final String MQTT_TOPIC = "/topic/mqtt";
    private static final String MQTT_MESSAGE = "Hello Vert.x MQTT Client";
    private static final String BROKER_HOST = "localhost";
    private static final int BROKER_PORT = 1883;

    @Test
    public void test() throws Exception {
        Vertx vertx = Vertx.vertx();
        MqttClient mqttClient = MqttClient.create(vertx);

        mqttClient.connect(BROKER_PORT, BROKER_HOST, ch -> {
            if (ch.succeeded()) {
                System.out.println("Connected to a server");

                mqttClient.publishHandler(s -> {
                    System.out.println("There are new message in topic: " + s.topicName());
                    System.out.println("Content(as string) of the message: " + s.payload().toString());
                    System.out.println("QoS: " + s.qosLevel());
                });

                mqttClient.subscribe(MQTT_TOPIC, 2, (r) -> {
                    System.out.println("X");
                });

                mqttClient.subscribeCompletionHandler((r) -> {
                    System.out.println("Y");
                    mqttClient.publish(MQTT_TOPIC, Buffer.buffer(MQTT_MESSAGE), MqttQoS.EXACTLY_ONCE, false, false);
                });
            } else {
                System.out.println("Failed to connect to a server");
                System.out.println(ch.cause());
            }
        });

        Thread.sleep(5000);

        mqttClient.disconnect(d -> System.out.println("Disconnected from server"));
        vertx.close();
    }
}
