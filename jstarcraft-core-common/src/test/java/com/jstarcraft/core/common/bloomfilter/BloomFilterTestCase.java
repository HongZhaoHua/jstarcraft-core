package com.jstarcraft.core.common.bloomfilter;

import org.junit.Assert;
import org.junit.Test;

public abstract class BloomFilterTestCase {

    protected abstract BloomFilter getBloomFilter(int elments, float probability);

    @Test
    public void testBloomFilter() {
        int elments = 1000;
        float probability = 0.001F;
        BloomFilter bloomFilter = getBloomFilter(elments, probability);
        String[] datas = new String[elments];
        int times = 0;
        for (int index = 0; index < elments; index++) {
            String data = String.valueOf(index);
            datas[index] = data;
            times += bloomFilter.getElements(data);
            bloomFilter.putElements(data);
        }
        Assert.assertEquals(elments, bloomFilter.getElements(datas));
        Assert.assertTrue(times < elments * probability);
    }

}
