package com.jstarcraft.core.common.bloomfilter;

import com.jstarcraft.core.common.bit.BitMap;

/**
 * 布隆过滤器
 * 
 * @author Birdy
 *
 */
public interface BloomFilter<E> {

    int getElements(E... datas);

    void putElements(E... datas);

    int bitSize();

    int hashSize();

    /**
     * Calculates the optimal size <i>size</i> of the bloom filter in bits given
     * <i>expectedElements</i> (expected number of elements in bloom filter) and
     * <i>falsePositiveProbability</i> (tolerable false positive rate).
     *
     * @param n Expected number of elements inserted in the bloom filter
     * @param p Tolerable false positive rate
     * @return the optimal size <i>size</i> of the bloom filter in bits
     */
    public static int optimalBits(int n, float p) {
        return (int) Math.floor(-1 * (n * Math.log(p)) / Math.pow(Math.log(2), 2));
    }

    /**
     * Calculates the optimal <i>hashes</i> (number of hash function) given
     * <i>expectedElements</i> (expected number of elements in bloom filter) and
     * <i>size</i> (size of bloom filter in bits).
     *
     * @param m The size of the bloom filter in bits.
     * @param n Expected number of elements inserted in the bloom filter
     * @return the optimal amount of hash functions hashes
     */
    public static int optimalHashs(int m, int n) {
        return Math.max(1, (int) Math.round((Math.log(2) * m) / n));
    }

    /**
     * Calculates the amount of elements a Bloom filter for which the given
     * configuration of size and hashes is optimal.
     *
     * @param m The size of the bloom filter in bits.
     * @param k number of hashes
     * @return amount of elements a Bloom filter for which the given configuration
     *         of size and hashes is optimal.
     */
    public static int optimalElements(int m, int k) {
        return (int) Math.ceil((Math.log(2) * m) / k);
    }

    /**
     * Calculates the best-case (uniform hash function) false positive probability.
     *
     * @param m The size of the bloom filter in bits.
     * @param n number of elements inserted in the filter
     * @param k number of hashes
     * @return The calculated false positive probability
     */
    public static float optimalProbability(int m, int n, int k) {
        return (float) Math.pow((1 - Math.exp(-k * n / (float) m)), k);
    }

}