package com.jstarcraft.core.common.bloomfilter;

import com.jstarcraft.core.common.bloomfilter.local.BitNumberLocalBloomFilter;
import com.jstarcraft.core.common.hash.HashUtility;

public class BitNumberLocalBloomFilterTestCase extends LocalBloomFilterTestCase {

	@Override
	protected BloomFilter getBloomFilter(int elments, float probability) {
		random.setSeed(0L);
		int bits = BloomFilter.optimalBits(elments, probability);
		int hashs = BloomFilter.optimalHashs(bits, elments);
		StringHashFamily hashFamily = (random) -> {
			int seed = random.nextInt();
			return (data) -> {
				return HashUtility.murmur2StringHash32(seed, data);
			};
		};
		BloomFilter bloomFilter = new BitNumberLocalBloomFilter(bits, hashFamily, hashs, random);
		return bloomFilter;
	}

}