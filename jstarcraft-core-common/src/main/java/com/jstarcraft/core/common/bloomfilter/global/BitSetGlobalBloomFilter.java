package com.jstarcraft.core.common.bloomfilter.global;

import org.redisson.Redisson;
import org.redisson.api.RBitSet;

import com.jstarcraft.core.common.bit.ByteMap;
import com.jstarcraft.core.common.bloomfilter.AbstractBloomFilter;
import com.jstarcraft.core.common.hash.StringHashFunction;

/**
 * 基于Redis的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class BitSetGlobalBloomFilter extends AbstractBloomFilter<RBitSet, ByteMap> {

    public BitSetGlobalBloomFilter(Redisson redisson, String name, int capacity, StringHashFunction... functions) {
        super(capacity, redisson.getBitSet(name), functions);
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
    public int bitCount() {
        return (int) bits.cardinality();
    }

    @Override
    public int hashSize() {
        return functions.length;
    }

    public byte[] getBytes() {
        return bits.toByteArray();
    }

}
