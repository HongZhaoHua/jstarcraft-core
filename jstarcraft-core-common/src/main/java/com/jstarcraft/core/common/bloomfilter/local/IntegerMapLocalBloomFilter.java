package com.jstarcraft.core.common.bloomfilter.local;

import java.util.Random;

import com.jstarcraft.core.common.bit.LocalIntegerArrayMap;
import com.jstarcraft.core.common.bloomfilter.StringHashFamily;

/**
 * 基于IntegerMap的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class IntegerMapLocalBloomFilter extends BitMapLocalBloomFilter<int[]> {

    public IntegerMapLocalBloomFilter(int bitSize, StringHashFamily hashFamily, int hashSize, Random random) {
        super(bitSize, new LocalIntegerArrayMap(bitSize), getFunctions(hashFamily, hashSize, random));
    }

}
