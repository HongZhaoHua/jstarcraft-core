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
        // 注意Maven的配置
        // https://github.com/salesforce/kafka-junit/tree/master/kafka-junit4
        KafkaTestServer server = new KafkaTestServer(properties);
        server.start();
        server.stop();
    }

}
