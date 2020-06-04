package com.jstarcraft.core.event.kafka;

import java.util.Properties;

import org.junit.Test;

import com.salesforce.kafka.test.KafkaTestServer;

public class KafkaTestCase {

    @Test
    public void test() throws Exception {
        Properties properties = new Properties();
        properties.put("broker.id", "1");
        properties.put("port", "9092");
        KafkaTestServer server = new KafkaTestServer(properties);
        server.start();
        String string = server.getKafkaBrokers().getBrokerById(1).getConnectString();
        server.stop();
    }

}
