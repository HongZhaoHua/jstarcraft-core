package com.jstarcraft.core.transaction;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.embedded.RedisServer;

public class JedisTestCase {

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

    @Test
    public void test() {
        try (Jedis jedis = new Jedis("127.0.0.1")) {
            jedis.connect();
            jedis.ping();

            jedis.select(15);
            try {
                jedis.select(16);
                Assert.fail();
            } catch (JedisDataException exception) {
            }
        } catch (JedisConnectionException exception) {
            Assert.fail();
        }
    }

}
