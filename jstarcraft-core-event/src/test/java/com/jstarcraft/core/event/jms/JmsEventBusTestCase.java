package com.jstarcraft.core.event.jms;

import javax.jms.JMSContext;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventBus;
import com.jstarcraft.core.event.EventBusTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockEvent;

public class JmsEventBusTestCase extends EventBusTestCase {

    private static ActiveMQConnectionFactory factory;

    @BeforeClass
    public static void start() {
        factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    @AfterClass
    public static void stop() {
        factory.close();
    }

    @Override
    protected EventBus getEventBus(EventMode mode) {
        JMSContext context = factory.createContext();
        CodecDefinition definition = CodecDefinition.instanceOf(MockEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        return new JmsEventBus(mode, context, codec);
    }

}
