package com.jstarcraft.core.common.bloomfilter.local;

import java.math.BigInteger;
import java.util.Random;

import com.jstarcraft.core.common.bit.LocalByteArrayMap;
import com.jstarcraft.core.common.bloomfilter.AbstractBloomFilter;
import com.jstarcraft.core.common.bloomfilter.StringHashFamily;
import com.jstarcraft.core.common.hash.StringHashFunction;

/**
 * 基于BigInteger的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class BitNumberLocalBloomFilter extends AbstractBloomFilter<BigInteger, LocalByteArrayMap> {

    public BitNumberLocalBloomFilter(int bitSize, StringHashFamily hashFamily, int hashSize, Random random) {
        super(bitSize, new BigInteger(new byte[bitSize / Byte.SIZE + (bitSize % Byte.SIZE == 0 ? 0 : 1)]), getFunctions(hashFamily, hashSize, random));
    }

    @Override
    public boolean getBit(String data) {
        for (StringHashFunction function : functions) {
            int hash = function.hash(data);
            int index = Math.abs(hash % capacity);
            if (!bits.testBit(index)) {
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
            // TODO 每次都是拷贝,不建议使用.
            bits = bits.setBit(index);
        }
    }
    
    @Override
    public int bitCount() {
        return bits.bitCount();
    }

}
