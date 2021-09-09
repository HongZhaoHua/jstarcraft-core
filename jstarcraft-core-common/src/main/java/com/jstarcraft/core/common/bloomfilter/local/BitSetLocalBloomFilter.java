package com.jstarcraft.core.common.bloomfilter.local;

import java.util.BitSet;
import java.util.Random;

import com.jstarcraft.core.common.bit.ByteMap;
import com.jstarcraft.core.common.bloomfilter.AbstractBloomFilter;
import com.jstarcraft.core.common.bloomfilter.StringHashFamily;
import com.jstarcraft.core.common.hash.StringHashFunction;

/**
 * 基于BitSet的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class BitSetLocalBloomFilter extends AbstractBloomFilter<BitSet, ByteMap> {

    public BitSetLocalBloomFilter(int bitSize, StringHashFamily hashFamily, int hashSize, Random random) {
        super(bitSize, new BitSet(bitSize), getFunctions(hashFamily, hashSize, random));
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

}
