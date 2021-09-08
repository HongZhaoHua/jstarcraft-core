package com.jstarcraft.core.common.bloomfilter.global;

import org.redisson.Redisson;
import org.redisson.api.RBitSet;
import org.redisson.api.RBucket;
import org.redisson.client.codec.ByteArrayCodec;

import com.jstarcraft.core.common.bloomfilter.BloomFilter;
import com.jstarcraft.core.common.hash.StringHashFunction;

/**
 * 基于Redis的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class BitSetGlobalBloomFilter implements BloomFilter {

    private RBitSet bits;

    private RBucket<byte[]> bytes;

    private int capacity;

    private StringHashFunction[] functions;

    public BitSetGlobalBloomFilter(Redisson redisson, String name, int capacity, StringHashFunction... functions) {
        this.bits = redisson.getBitSet(name);
        this.bytes = redisson.getBucket(name, ByteArrayCodec.INSTANCE);
        this.capacity = capacity;
        this.functions = functions;
    }

    @Override
    public boolean getBit(String data) {
        for (StringHashFunction function : functions) {
            int hash = function.hash(data);
            int index = Math.abs(hash % capacity);
            if (!bits.get(index)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void putBit(String data) {
        for (StringHashFunction function : functions) {
            int hash = function.hash(data);
            int index = Math.abs(hash % capacity);
            bits.set(index);
        }
    }

    @Override
    public int bitSize() {
        return capacity;
    }

    @Override
    public int hashSize() {
        return functions.length;
    }

    public byte[] getBytes() {
        return bytes.get();
    }

}
