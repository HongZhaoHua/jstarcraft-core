package com.jstarcraft.core.event.redis;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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
import com.jstarcraft.core.event.redis.RedisQueueEventBus;
import com.jstarcraft.core.event.redis.RedisTopicEventBus;

public class RedisEventBusTestCase extends EventBusTestCase {

    private static Redisson redisson;
    private static RKeys keys;

    @Override
    protected EventBus getEventBus(EventMode mode) {
        switch (mode) {
        case QUEUE: {
            CodecDefinition definition = CodecDefinition.instanceOf(MockEvent.class);
            ContentCodec codec = new JsonContentCodec(definition);
            RedisQueueEventBus bus = new RedisQueueEventBus("redis", redisson, codec);
            return bus;
        }
        case TOPIC: {
            CodecDefinition definition = CodecDefinition.instanceOf(MockEvent.class);
            ContentCodec codec = new JsonContentCodec(definition);
            RedisTopicEventBus bus = new RedisTopicEventBus("redis", redisson, codec);
            return bus;
        }
        default: {
            return null;
        }
        }
    }

    @BeforeClass
    public static void start() {
        // 注意此处的编解码器
        Codec codec = new ByteArrayCodec();
        Config configuration = new Config();
        configuration.setCodec(codec);
        configuration.useSingleServer().setAddress("redis://127.0.0.1:6379");

        redisson = (Redisson) Redisson.create(configuration);
        keys = redisson.getKeys();
        keys.flushdb();
    }

    @AfterClass
    public static void stop() {
        keys.flushdb();
        redisson.shutdown();
    }

}
