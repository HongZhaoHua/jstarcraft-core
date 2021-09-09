package com.jstarcraft.core.common.bloomfilter;

import java.util.Random;

import com.jstarcraft.core.common.bit.BitMap;
import com.jstarcraft.core.common.hash.StringHashFunction;

public abstract class AbstractBloomFilter<T, M extends BitMap<?>> implements BloomFilter<M> {

    protected int capacity;

    protected T bits;

    protected StringHashFunction[] functions;

    protected static StringHashFunction[] getFunctions(StringHashFamily hashFamily, int hashSize, Random random) {
        StringHashFunction[] functions = new StringHashFunction[hashSize];
        for (int index = 0; index < hashSize; index++) {
            functions[index] = hashFamily.getHashFunction(random);
        }
        return functions;
    }

    protected AbstractBloomFilter(int capacity, T bits, StringHashFunction... functions) {
        this.capacity = capacity;
        this.bits = bits;
        this.functions = functions;
    }

    @Override
    public int bitSize() {
        return capacity;
    }

    @Override
    public int hashSize() {
        return functions.length;
    }

}