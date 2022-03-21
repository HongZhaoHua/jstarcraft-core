package com.jstarcraft.core.common.bloomfilter;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;

/**
 * 基于Redis的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class RedissonBloomFilter<E> implements BloomFilter<E> {

    private RBloomFilter<E> bits;

    public RedissonBloomFilter(Redisson redisson, String name) {
        this.bits = redisson.getBloomFilter(name);
    }

    public RedissonBloomFilter(Redisson redisson, String name, int elments, float probability) {
        this.bits = redisson.getBloomFilter(name);
        if (!this.bits.tryInit(elments, probability)) {
            throw new RuntimeException("布隆过滤器冲突");
        }
    }

    @Override
    public int getElements(E... datas) {
        int count = 0;
        for (E data : datas) {
            if (bits.contains(data)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void putElements(E... datas) {
        for (E data : datas) {
            bits.add(data);
        }
    }

    @Override
    public int bitSize() {
        return (int) bits.getSize();
    }

    @Override
    public int hashSize() {
        return (int) bits.getHashIterations();
    }

}
