package com.jstarcraft.core.common.bloomfilter;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.bit.LocalByteArrayMap;
import com.jstarcraft.core.common.hash.HashFunction;
import com.jstarcraft.core.common.hash.HashUtility;

public class BitMapBloomLocalFilterTestCase extends BloomFilterTestCase {

    protected static Random random = new Random();

    @Test
    public void testOptimal() {
        int elments = 1000;
        float probability = 0.001F;
        int bits = BloomFilter.optimalBits(elments, probability);
        int hashs = BloomFilter.optimalHashs(bits, elments);
        Assert.assertEquals(997, BloomFilter.optimalElements(bits, hashs));
        Assert.assertEquals(0.0010003077F, BloomFilter.optimalProbability(bits, elments, hashs), 0F);
    }

    @Override
    protected BloomFilter getBloomFilter(int elments, float probability) {
        random.setSeed(0L);
        int bits = BloomFilter.optimalBits(elments, probability);
        int hashs = BloomFilter.optimalHashs(bits, elments);
        HashFunction<String>[] functions = new HashFunction[hashs];
        for (int index = 0; index < hashs; index++) {
            int seed = random.nextInt();
            functions[index] = (data) -> {
                return HashUtility.murmur2StringHash32(seed, data);
            };
        }
        LocalByteArrayMap bitMap = new LocalByteArrayMap(bits);
        BloomFilter bloomFilter = new BitMapBloomFilter<>(bitMap, functions);
        return bloomFilter;
    }

}