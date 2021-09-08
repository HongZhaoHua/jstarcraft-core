package com.jstarcraft.core.common.bloomfilter;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.client.codec.ByteArrayCodec;

/**
 * 基于Redis的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class GlobalBloomFilter implements BloomFilter {

    private RBloomFilter<String> bits;

    private RBucket<byte[]> bytes;

    public GlobalBloomFilter(Redisson redisson, String name) {
        this.bits = redisson.getBloomFilter(name);
        this.bytes = redisson.getBucket(name, ByteArrayCodec.INSTANCE);
    }

    public GlobalBloomFilter(Redisson redisson, String name, int elments, float probability) {
        this.bits = redisson.getBloomFilter(name);
        if (!this.bits.tryInit(elments, probability)) {
            throw new RuntimeException("布隆过滤器冲突");
        }
        this.bytes = redisson.getBucket(name, ByteArrayCodec.INSTANCE);
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
    public int hashSize() {
        return (int) bits.getHashIterations();
    }

    public byte[] getBytes() {
        return bytes.get();
    }

}
