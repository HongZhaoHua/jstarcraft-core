package com.jstarcraft.core.utility;

import java.util.Random;

/**
 * 随机工具
 * 
 * @author Birdy
 */
public final class RandomUtility {

    private static Random random = new Random(NumberUtility.LONG_ZERO);

    private RandomUtility() {
    }

    /**
     * 获取随机引擎
     * 
     * @return
     */
    public static Random getRandom() {
        return random;
    }

    /**
     * 设置随机引擎
     * 
     * @param engine
     */
    public static void setRandom(Random engine) {
        random = engine;
    }

    /**
     * 设置随机种子
     * 
     * @param seed
     */
    public static void setSeed(long seed) {
        random.setSeed(seed);
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    public static byte[] randomBytes(byte[] bytes) {
        random.nextBytes(bytes);
        return bytes;
    }

    public static int randomInteger(final int beginInclusive, final int endExclusive) {
        assert beginInclusive <= endExclusive;
        if (beginInclusive == endExclusive) {
            return beginInclusive;
        }
        return beginInclusive + random.nextInt(endExclusive - beginInclusive);
    }

    public static int randomInteger(final int range) {
        return randomInteger(0, range);
    }

    public static int randomInteger() {
        return randomInteger(Integer.MAX_VALUE);
    }

    public static long randomLong(final long beginInclusive, final long endExclusive) {
        assert beginInclusive <= endExclusive;
        if (beginInclusive == endExclusive) {
            return beginInclusive;
        }
        return (long) randomDouble(beginInclusive, endExclusive);
    }

    public static long randomLong(final long range) {
        return randomLong(0L, range);
    }

    public static long randomLong() {
        return randomLong(Long.MAX_VALUE);
    }

    public static float randomFloat(final float beginInclusive, final float endExclusive) {
        assert beginInclusive <= endExclusive;
        if (beginInclusive == endExclusive) {
            return beginInclusive;
        }
        return beginInclusive + ((endExclusive - beginInclusive) * random.nextFloat());
    }

    public static float randomFloat(final float range) {
        return randomFloat(0F, range);
    }

    public static float randomFloat() {
        return randomFloat(Float.MAX_VALUE);
    }

    public static double randomDouble(final double beginInclusive, final double endExclusive) {
        assert beginInclusive <= endExclusive;
        if (beginInclusive == endExclusive) {
            return beginInclusive;
        }
        return beginInclusive + ((endExclusive - beginInclusive) * random.nextDouble());
    }

    public static double randomDouble(final double range) {
        return randomDouble(0D, range);
    }

    public static double randomDouble() {
        return randomDouble(Double.MAX_VALUE);
    }
    
    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(boolean[] datas) {
        int size = datas.length;
        shuffle(datas, 0, size);
    }

    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(int[] datas) {
        int size = datas.length;
        shuffle(datas, 0, size);
    }

    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(long[] datas) {
        int size = datas.length;
        shuffle(datas, 0, size);
    }

    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(float[] datas) {
        int size = datas.length;
        shuffle(datas, 0, size);
    }

    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(double[] datas) {
        int size = datas.length;
        shuffle(datas, 0, size);
    }

    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(T[] datas) {
        int size = datas.length;
        shuffle(datas, 0, size);
    }

    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(boolean[] datas, int from, int to) {
        int size = to - from;
        if (size <= 1) {
            return;
        }
        for (int index = from; index < to; index++) {
            int random = RandomUtility.randomInteger(from, to);
            boolean data = datas[index];
            datas[index] = datas[random];
            datas[random] = data;
        }
    }

    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(int[] datas, int from, int to) {
        int size = to - from;
        if (size <= 1) {
            return;
        }
        for (int index = from; index < to; index++) {
            int random = RandomUtility.randomInteger(from, to);
            int data = datas[index];
            datas[index] = datas[random];
            datas[random] = data;
        }
    }

    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(long[] datas, int from, int to) {
        int size = to - from;
        if (size <= 1) {
            return;
        }
        for (int index = from; index < to; index++) {
            int random = RandomUtility.randomInteger(from, to);
            long data = datas[index];
            datas[index] = datas[random];
            datas[random] = data;
        }
    }

    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(float[] datas, int from, int to) {
        int size = to - from;
        if (size <= 1) {
            return;
        }
        for (int index = from; index < to; index++) {
            int random = RandomUtility.randomInteger(from, to);
            float data = datas[index];
            datas[index] = datas[random];
            datas[random] = data;
        }
    }

    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(double[] datas, int from, int to) {
        int size = to - from;
        if (size <= 1) {
            return;
        }
        for (int index = from; index < to; index++) {
            int random = RandomUtility.randomInteger(from, to);
            double data = datas[index];
            datas[index] = datas[random];
            datas[random] = data;
        }
    }

    /**
     * 洗牌
     * 
     * <pre>
     * 通过随机与交换的方式实现打乱排序的目的.
     * </pre>
     * 
     * @param datas
     */
    public static <T> void shuffle(T[] datas, int from, int to) {
        int size = to - from;
        if (size <= 1) {
            return;
        }
        for (int index = from; index < to; index++) {
            int random = RandomUtility.randomInteger(from, to);
            T data = datas[index];
            datas[index] = datas[random];
            datas[random] = data;
        }
    }

    /**
     * 随机分布
     * 
     * @return
     */
    // TODO 考虑是否使用概率分布替代
    public static double randomGaussian() {
        return random.nextGaussian();
    }

}