package com.jstarcraft.core.event.kafka;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventChannel;
import com.jstarcraft.core.event.EventChannelTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockBroadcastEvent;
import com.jstarcraft.core.event.MockUnicastEvent;
import com.salesforce.kafka.test.KafkaTestServer;

public class KafkaEventChannelTestCase extends EventChannelTestCase {

    private static KafkaTestServer server;

    static {
        Properties properties = new Properties();
        properties.put("broker.id", "1");
        properties.put("port", "9092");
        // 注意Maven的配置
        // https://github.com/salesforce/kafka-junit/tree/master/kafka-junit4
        server = new KafkaTestServer(properties);
    }

    @BeforeClass
    public static void start() throws Exception {
        server.start();
    }

    @AfterClass
    public static void stop() throws Exception {
        server.stop();
    }

    @Override
    protected EventChannel getEventChannel(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockUnicastEvent.class, MockBroadcastEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        return new KafkaEventChannel(mode, "Kafka" + mode, server.getKafkaConnectString(), codec);
    }

}
