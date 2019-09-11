package com.jstarcraft.core.transaction;

import org.junit.Assert;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

public class JedisTestCase {

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
