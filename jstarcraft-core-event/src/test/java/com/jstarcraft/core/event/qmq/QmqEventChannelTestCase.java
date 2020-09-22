package com.jstarcraft.core.event.qmq;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventChannel;
import com.jstarcraft.core.event.EventChannelTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockBroadcastEvent;
import com.jstarcraft.core.event.MockUnicastEvent;

public class QmqEventChannelTestCase extends EventChannelTestCase {

//    private static MessageProducerProvider producer;
//
//    private static MessageConsumerProvider consumer;
//
//    @BeforeClass
//    public static void start() throws Exception {
//        final String applicationCode = "QmqTestCase";
//        final String metaAddress = "http://127.0.0.1:8080/meta/address";
//
//        producer = new MessageProducerProvider();
//        producer.setAppCode(applicationCode);
//        producer.setMetaServer(metaAddress);
//        producer.init();
//
//        consumer = new MessageConsumerProvider();
//        consumer.setAppCode(applicationCode);
//        consumer.setMetaServer(metaAddress);
//        consumer.init();
//    }
//
//    @AfterClass
//    public static void stop() throws Exception {
//        producer.destroy();
//
//        consumer.destroy();
//    }

    @Override
    protected EventChannel getEventChannel(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockUnicastEvent.class, MockBroadcastEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        return new QmqEventChannel(mode, "QMQ" + mode, "127.0.0.1", 8080, "token", codec);
    }

}
