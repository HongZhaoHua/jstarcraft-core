package com.jstarcraft.core.event.redis;

import org.junit.After;
import org.junit.Before;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventBus;
import com.jstarcraft.core.event.EventBusTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockEvent;

public class RedisEventBusTestCase extends EventBusTestCase {

    private Redisson redisson;
    private RKeys keys;

    @Before
    public void start() {
        // 注意此处的编解码器
        Codec codec = new ByteArrayCodec();
        Config configuration = new Config();
        configuration.setCodec(codec);
        configuration.useSingleServer().setAddress("redis://127.0.0.1:6379");

        redisson = (Redisson) Redisson.create(configuration);
        keys = redisson.getKeys();
        keys.flushdb();
    }

    @After
    public void stop() {
        keys.flushdb();
        redisson.shutdown();
    }

    @Override
    protected EventBus getEventBus(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        switch (mode) {
        case QUEUE: {
            RedisQueueEventBus bus = new RedisQueueEventBus("redis", redisson, codec);
            return bus;
        }
        case TOPIC: {
            RedisTopicEventBus bus = new RedisTopicEventBus("redis", redisson, codec);
            return bus;
        }
        default: {
            return null;
        }
        }
    }

}
