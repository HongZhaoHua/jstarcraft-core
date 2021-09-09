package com.jstarcraft.core.common.bloomfilter.local;

import java.util.Random;

import com.jstarcraft.core.common.bit.ByteMap;
import com.jstarcraft.core.common.bloomfilter.StringHashFamily;

/**
 * 基于ByteMap的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class ByteMapLocalBloomFilter extends BitMapLocalBloomFilter<byte[]> {

    public ByteMapLocalBloomFilter(int bitSize, StringHashFamily hashFamily, int hashSize, Random random) {
        super(bitSize, new ByteMap(bitSize), getFunctions(hashFamily, hashSize, random));
    }

}
