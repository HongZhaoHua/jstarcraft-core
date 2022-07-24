package com.jstarcraft.core.event.redis;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventChannel;
import com.jstarcraft.core.event.EventChannelTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockBroadcastEvent;
import com.jstarcraft.core.event.MockUnicastEvent;

import redis.embedded.RedisServer;

public class RedisEventChannelTestCase extends EventChannelTestCase {

    private Redisson redisson;
    private RKeys keys;

    private static RedisServer redis;

    @BeforeClass
    public static void startRedis() {
        redis = RedisServer.builder().port(6379).setting("maxmemory 64M").build();
        redis.start();
    }

    @AfterClass
    public static void stopRedis() {
        redis.stop();
    }

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
    protected EventChannel getEventChannel(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockUnicastEvent.class, MockBroadcastEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        switch (mode) {
        case QUEUE: {
            RedisQueueEventChannel bus = new RedisQueueEventChannel("redis", redisson, codec);
            return bus;
        }
        case TOPIC: {
            RedisTopicEventChannel bus = new RedisTopicEventChannel("redis", redisson, codec);
            return bus;
        }
        default: {
            return null;
        }
        }
    }

}
