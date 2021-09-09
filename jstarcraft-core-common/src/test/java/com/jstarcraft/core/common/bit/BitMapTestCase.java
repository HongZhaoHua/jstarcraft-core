package com.jstarcraft.core.common.bit;

import org.junit.Assert;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import redis.embedded.RedisServer;

public class BitMapTestCase {

    @Test
    public void testByteMap() {
        LocalIntegerArrayMap bits = new LocalIntegerArrayMap(Byte.SIZE);
        Assert.assertEquals(Byte.SIZE, bits.capacity());
        Assert.assertEquals(0, bits.count());
        for (int index = 0; index < Byte.SIZE; index++) {
            Assert.assertFalse(bits.get(index));
            bits.set(index);
            Assert.assertTrue(bits.get(index));
            bits.unset(index);
            Assert.assertFalse(bits.get(index));
        }

        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.unset(Byte.SIZE - 1);
        Assert.assertEquals(1, bits.count());
        bits.unset(0);
        Assert.assertEquals(0, bits.count());
    }

    @Test
    public void testIntegerMap() {
        LocalIntegerArrayMap bits = new LocalIntegerArrayMap(Integer.SIZE);
        Assert.assertEquals(Integer.SIZE, bits.capacity());
        Assert.assertEquals(0, bits.count());
        for (int index = 0; index < Integer.SIZE; index++) {
            Assert.assertFalse(bits.get(index));
            bits.set(index);
            Assert.assertTrue(bits.get(index));
            bits.unset(index);
            Assert.assertFalse(bits.get(index));
        }

        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.unset(Integer.SIZE - 1);
        Assert.assertEquals(1, bits.count());
        bits.unset(0);
        Assert.assertEquals(0, bits.count());
    }

    @Test
    public void testLongMap() {
        LocalLongArrayMap bits = new LocalLongArrayMap(Long.SIZE);
        Assert.assertEquals(Long.SIZE, bits.capacity());
        Assert.assertEquals(0, bits.count());
        for (int index = 0; index < Long.SIZE; index++) {
            Assert.assertFalse(bits.get(index));
            bits.set(index);
            Assert.assertTrue(bits.get(index));
            bits.unset(index);
            Assert.assertFalse(bits.get(index));
        }

        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.unset(Long.SIZE - 1);
        Assert.assertEquals(1, bits.count());
        bits.unset(0);
        Assert.assertEquals(0, bits.count());
    }

    @Test
    public void testRidisMap() {
        RedisServer redis = RedisServer.builder().port(6379).setting("maxmemory 64M").build();
        redis.start();

        // 注意此处的编解码器
        Codec codec = new JsonJacksonCodec();
        Config configuration = new Config();
        configuration.setCodec(codec);
        configuration.useSingleServer().setAddress("redis://127.0.0.1:6379");
        Redisson redisson = null;

        try {
            redisson = (Redisson) Redisson.create(configuration);

            {
                GlobalByteArrayMap bits = new GlobalByteArrayMap(redisson, "bits", 1000);
                Assert.assertEquals(1000, bits.capacity());
                Assert.assertEquals(0, bits.count());
                for (int index = 0; index < 1000; index++) {
                    Assert.assertFalse(bits.get(index));
                    bits.set(index);
                    Assert.assertTrue(bits.get(index));
                    bits.unset(index);
                    Assert.assertFalse(bits.get(index));
                }

                bits.set(0);
                Assert.assertEquals(1, bits.count());
                bits.set(0);
                Assert.assertEquals(1, bits.count());
                bits.unset(1000 - 1);
                Assert.assertEquals(1, bits.count());
                bits.unset(0);
                Assert.assertEquals(0, bits.count());
            }

            {
                GlobalBitSetMap bits = new GlobalBitSetMap(redisson.getBitSet("bits"), 1000);
                Assert.assertEquals(1000, bits.capacity());
                Assert.assertEquals(0, bits.count());
                for (int index = 0; index < 1000; index++) {
                    Assert.assertFalse(bits.get(index));
                    bits.set(index);
                    Assert.assertTrue(bits.get(index));
                    bits.unset(index);
                    Assert.assertFalse(bits.get(index));
                }

                bits.set(0);
                Assert.assertEquals(1, bits.count());
                bits.set(0);
                Assert.assertEquals(1, bits.count());
                bits.unset(1000 - 1);
                Assert.assertEquals(1, bits.count());
                bits.unset(0);
                Assert.assertEquals(0, bits.count());
            }
        } catch (Exception exception) {
            Assert.fail();
        } finally {
            redis.stop();
        }
    }

}
