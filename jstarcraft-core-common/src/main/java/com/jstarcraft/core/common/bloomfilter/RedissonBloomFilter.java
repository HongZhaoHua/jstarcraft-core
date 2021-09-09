package com.jstarcraft.core.common.bloomfilter;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.client.codec.ByteArrayCodec;

import com.jstarcraft.core.common.bit.LocalByteArrayMap;

/**
 * 基于Redis的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class RedissonBloomFilter<E> implements BloomFilter<E, LocalByteArrayMap> {

    private RBloomFilter<E> bits;

    private RBucket<byte[]> bucket;

    public RedissonBloomFilter(Redisson redisson, String name) {
        this.bits = redisson.getBloomFilter(name);
        this.bucket = redisson.getBucket(name, ByteArrayCodec.INSTANCE);
    }

    public RedissonBloomFilter(Redisson redisson, String name, int elments, float probability) {
        this.bits = redisson.getBloomFilter(name);
        if (!this.bits.tryInit(elments, probability)) {
            throw new RuntimeException("布隆过滤器冲突");
        }
        this.bucket = redisson.getBucket(name, ByteArrayCodec.INSTANCE);
    }

    @Override
    public int getElements(E... datas) {
        int count = 0;
        for (E data : datas) {
            if (bits.contains(data)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void putElements(E... datas) {
        for (E data : datas) {
            bits.add(data);
        }
    }

    @Override
    public int bitSize() {
        return (int) bits.getSize();
    }

    @Override
    public int hashSize() {
        return (int) bits.getHashIterations();
    }

    public byte[] getBytes() {
        return bucket.get();
    }

}
