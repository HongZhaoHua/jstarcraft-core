package com.jstarcraft.core.common.bloomfilter.local;

import com.jstarcraft.core.common.bit.BitMap;
import com.jstarcraft.core.common.bloomfilter.AbstractBloomFilter;
import com.jstarcraft.core.common.hash.StringHashFunction;

/**
 * 基于BitMap的布隆过滤器
 * 
 * @author Birdy
 *
 */
public abstract class BitMapLocalBloomFilter<T> extends AbstractBloomFilter<BitMap<T>, BitMap<T>> {

    protected BitMapLocalBloomFilter(int capacity, BitMap<T> bits, StringHashFunction... functions) {
        super(capacity, bits, functions);
    }

    @Override
    public boolean getBit(String data) {
        int capacity = bits.capacity();
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
        int capacity = bits.capacity();
        for (StringHashFunction function : functions) {
            int hash = function.hash(data);
            int index = Math.abs(hash % capacity);
            bits.set(index);
        }
    }

    @Override
    public int bitCount() {
        return bits.count();
    }

}
