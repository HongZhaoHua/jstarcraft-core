package com.jstarcraft.core.common.identification;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import redis.embedded.RedisServer;

public abstract class RedisIdentityFactoryTestCase extends IdentityFactoryTestCase {

    protected Redisson redisson;

    protected RKeys keys;

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
    public void testBefore() {
        // 注意此处的编解码器
        Codec codec = new JsonJacksonCodec();
        Config configuration = new Config();
        configuration.setCodec(codec);
        configuration.useSingleServer().setAddress("redis://127.0.0.1:6379");
        redisson = (Redisson) Redisson.create(configuration);
        keys = redisson.getKeys();
        keys.flushdb();
    }

    @After
    public void testAfter() {
        keys.flushdb();
        redisson.shutdown();
    }

}
