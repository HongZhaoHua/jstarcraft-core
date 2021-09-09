package com.jstarcraft.core.common.bloomfilter.local;

import java.util.Random;

import com.jstarcraft.core.common.bit.LongMap;
import com.jstarcraft.core.common.bloomfilter.StringHashFamily;

/**
 * 基于LongMap的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class LongMapLocalBloomFilter extends BitMapLocalBloomFilter<long[]> {

	public LongMapLocalBloomFilter(int bitSize, StringHashFamily hashFamily, int hashSize, Random random) {
		super(bitSize, new LongMap(bitSize), getFunctions(hashFamily, hashSize, random));
	}

}
