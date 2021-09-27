package com.jstarcraft.core.common.bit;

import org.junit.Assert;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import redis.embedded.RedisServer;

public class BitMapTestCase {

    private final static int size = 1000;

    @Test
    public void testBits() {
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
            LocalByteArrayMap localByteArrayMap = new LocalByteArrayMap(size);
            LocalBitSetMap localBitSetMap = new LocalBitSetMap(size);
            GlobalByteArrayMap globalByteArrayMap = new GlobalByteArrayMap(redisson, "globalByteArrayMap", size);
            GlobalBitSetMap globalBitSetMap = new GlobalBitSetMap(redisson.getBitSet("globalBitSetMap"), size);
            localByteArrayMap.set(0);
            localBitSetMap.set(0);
            globalByteArrayMap.set(0);
            globalBitSetMap.set(0);
            localByteArrayMap.set(1);
            localBitSetMap.set(1);
            globalByteArrayMap.set(1);
            globalBitSetMap.set(1);
            localByteArrayMap.set(7);
            localBitSetMap.set(7);
            globalByteArrayMap.set(7);
            globalBitSetMap.set(7);
            localByteArrayMap.set(8);
            localBitSetMap.set(8);
            globalByteArrayMap.set(8);
            globalBitSetMap.set(8);
            Assert.assertArrayEquals(globalByteArrayMap.bits(), localByteArrayMap.bits());
            Assert.assertArrayEquals(globalByteArrayMap.bits(), localBitSetMap.bits());
            Assert.assertArrayEquals(globalByteArrayMap.bits(), globalBitSetMap.bits());
        } catch (Exception exception) {
            Assert.fail();
        } finally {
            redis.stop();
        }
    }

    @Test
    public void testBitSetMap() {
        LocalBitSetMap bits = new LocalBitSetMap(size);
        Assert.assertEquals(size, bits.capacity());
        Assert.assertEquals(0, bits.count());
        Assert.assertEquals(125, bits.bits().length);
        bits.set(0);
        Assert.assertEquals(125, bits.bits().length);
        Assert.assertEquals(1, bits.count());
        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.unset(size - 1);
        Assert.assertEquals(1, bits.count());
        bits.unset(0);
        Assert.assertEquals(0, bits.count());
        for (int index = 0; index < size; index++) {
            Assert.assertFalse(bits.get(index));
            bits.set(index);
            Assert.assertTrue(bits.get(index));
            bits.unset(index);
            Assert.assertFalse(bits.get(index));
        }
    }

    @Test
    public void testByteMap() {
        LocalByteArrayMap bits = new LocalByteArrayMap(size);
        Assert.assertEquals(size, bits.capacity());
        Assert.assertEquals(0, bits.count());
        Assert.assertEquals(125, bits.bits().length);
        bits.set(0);
        Assert.assertEquals(125, bits.bits().length);
        Assert.assertEquals(1, bits.count());
        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.unset(size - 1);
        Assert.assertEquals(1, bits.count());
        bits.unset(0);
        Assert.assertEquals(0, bits.count());
        for (int index = 0; index < size; index++) {
            Assert.assertFalse(bits.get(index));
            bits.set(index);
            Assert.assertTrue(bits.get(index));
            bits.unset(index);
            Assert.assertFalse(bits.get(index));
        }
    }

    @Test
    public void testIntegerMap() {
        LocalIntegerArrayMap bits = new LocalIntegerArrayMap(size);
        Assert.assertEquals(size, bits.capacity());
        Assert.assertEquals(0, bits.count());
        Assert.assertEquals(32, bits.bits().length);
        bits.set(0);
        Assert.assertEquals(32, bits.bits().length);
        Assert.assertEquals(1, bits.count());
        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.unset(size - 1);
        Assert.assertEquals(1, bits.count());
        bits.unset(0);
        Assert.assertEquals(0, bits.count());
        for (int index = 0; index < size; index++) {
            Assert.assertFalse(bits.get(index));
            bits.set(index);
            Assert.assertTrue(bits.get(index));
            bits.unset(index);
            Assert.assertFalse(bits.get(index));
        }
    }

    @Test
    public void testLongMap() {
        LocalLongArrayMap bits = new LocalLongArrayMap(size);
        Assert.assertEquals(size, bits.capacity());
        Assert.assertEquals(0, bits.count());
        Assert.assertEquals(16, bits.bits().length);
        bits.set(0);
        Assert.assertEquals(16, bits.bits().length);
        Assert.assertEquals(1, bits.count());
        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.unset(size - 1);
        Assert.assertEquals(1, bits.count());
        bits.unset(0);
        Assert.assertEquals(0, bits.count());
        for (int index = 0; index < size; index++) {
            Assert.assertFalse(bits.get(index));
            bits.set(index);
            Assert.assertTrue(bits.get(index));
            bits.unset(index);
            Assert.assertFalse(bits.get(index));
        }
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
                GlobalByteArrayMap bits = new GlobalByteArrayMap(redisson, "bits", size);
                Assert.assertEquals(size, bits.capacity());
                Assert.assertEquals(0, bits.count());
                Assert.assertEquals(125, bits.bits().length);
                bits.set(0);
                Assert.assertEquals(125, bits.bits().length);
                Assert.assertEquals(1, bits.count());
                bits.set(0);
                Assert.assertEquals(1, bits.count());
                bits.unset(size - 1);
                Assert.assertEquals(1, bits.count());
                bits.unset(0);
                Assert.assertEquals(0, bits.count());
                for (int index = 0; index < size; index++) {
                    Assert.assertFalse(bits.get(index));
                    bits.set(index);
                    Assert.assertTrue(bits.get(index));
                    bits.unset(index);
                    Assert.assertFalse(bits.get(index));
                }
            }

            {
                GlobalBitSetMap bits = new GlobalBitSetMap(redisson.getBitSet("bits"), size);
                Assert.assertEquals(size, bits.capacity());
                Assert.assertEquals(0, bits.count());
                Assert.assertEquals(125, bits.bits().length);
                bits.set(0);
                Assert.assertEquals(125, bits.bits().length);
                Assert.assertEquals(1, bits.count());
                bits.set(0);
                Assert.assertEquals(1, bits.count());
                bits.unset(size - 1);
                Assert.assertEquals(1, bits.count());
                bits.unset(0);
                Assert.assertEquals(0, bits.count());
                for (int index = 0; index < size; index++) {
                    Assert.assertFalse(bits.get(index));
                    bits.set(index);
                    Assert.assertTrue(bits.get(index));
                    bits.unset(index);
                    Assert.assertFalse(bits.get(index));
                }
            }
        } catch (Exception exception) {
            Assert.fail();
        } finally {
            redis.stop();
        }
    }

}
