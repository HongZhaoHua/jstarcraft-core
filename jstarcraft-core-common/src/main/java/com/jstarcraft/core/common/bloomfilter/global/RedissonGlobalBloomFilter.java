package com.jstarcraft.core.common.bloomfilter.global;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.client.codec.ByteArrayCodec;

import com.jstarcraft.core.common.bit.ByteMap;
import com.jstarcraft.core.common.bloomfilter.BloomFilter;

/**
 * 基于Redis的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class RedissonGlobalBloomFilter implements BloomFilter<ByteMap> {

    private RBloomFilter<String> bits;

    private RBucket<byte[]> bucket;

    public RedissonGlobalBloomFilter(Redisson redisson, String name) {
        this.bits = redisson.getBloomFilter(name);
        this.bucket = redisson.getBucket(name, ByteArrayCodec.INSTANCE);
    }

    public RedissonGlobalBloomFilter(Redisson redisson, String name, int elments, float probability) {
        this.bits = redisson.getBloomFilter(name);
        if (!this.bits.tryInit(elments, probability)) {
            throw new RuntimeException("布隆过滤器冲突");
        }
        this.bucket = redisson.getBucket(name, ByteArrayCodec.INSTANCE);
    }

    @Override
    public boolean getBit(String data) {
        return bits.contains(data);
    }

    @Override
    public void putBit(String data) {
        bits.add(data);
    }

    @Override
    public int bitSize() {
        return (int) bits.getSize();
    }

    @Override
    public int bitCount() {
        return (int) bits.count();
    }

    @Override
    public int hashSize() {
        return (int) bits.getHashIterations();
    }

    public byte[] getBytes() {
        return bucket.get();
    }

}
