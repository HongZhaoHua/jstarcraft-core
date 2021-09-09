package com.jstarcraft.core.common.bloomfilter;

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

public class RedissonBloomFilterTestCase extends BloomFilterTestCase {

    private static RedisServer redis;

    private static Redisson redisson;

    @BeforeClass
    public static void beforeClass() {
        redis = RedisServer.builder().port(6379).setting("maxmemory 64M").build();
        redis.start();
        // 注意此处的编解码器
        Codec codec = new JsonJacksonCodec();
        Config configuration = new Config();
        configuration.setCodec(codec);
        configuration.useSingleServer().setAddress("redis://127.0.0.1:6379");
        redisson = (Redisson) Redisson.create(configuration);
    }

    @AfterClass
    public static void afterClass() {
        redisson.shutdown();
        redis.stop();
    }

    @Before
    public void beforeTest() {
        RKeys keys = redisson.getKeys();
        keys.flushdb();
    }

    @After
    public void afterTest() {
        RKeys keys = redisson.getKeys();
        keys.flushdb();
    }

    @Override
    protected BloomFilter getBloomFilter(int elments, float probability) {
        BloomFilter bloomFilter = new RedissonBloomFilter(redisson, "bloom", elments, probability);
        return bloomFilter;
    }

}
