package com.jstarcraft.core.common.hash;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

public class HashUtilityTestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testStringHash32() {
        int number = 1656899;
        String string = "abcdefghijklmnopqrstuvwxyz1234567890";
        Assert.assertEquals(-1434001724, HashUtility.murmur2NumberHash32(number));
        Assert.assertEquals(363720497, HashUtility.murmur3NumberHash32(number));
        Assert.assertEquals(-755170596, HashUtility.rjNumberHash32(number));
        Assert.assertEquals(800802560, HashUtility.twNumberHash32(number));

        Assert.assertEquals(3408, HashUtility.additiveStringHash32(string));
        Assert.assertEquals(-419280992, HashUtility.apStringHash32(string));

        Assert.assertEquals(1151693868, HashUtility.bernsteinStringHash32(string));
        Assert.assertEquals(-1141380680, HashUtility.bkdrStringHash32(string));
        Assert.assertEquals(1726880944, HashUtility.bpStringHash32(string));

        Assert.assertEquals(354978945, HashUtility.crcStringHash32(string));

        Assert.assertEquals(-1055497406, HashUtility.dekStringHash32(string));
        Assert.assertEquals(729241521, HashUtility.djbStringHash32(string));

        Assert.assertEquals(140307872, HashUtility.elfStringHash32(string));

        Assert.assertEquals(-1051872190, HashUtility.fnv0StringHash32(string));
        Assert.assertEquals(-672044013, HashUtility.fnv1StringHash32(string));

        Assert.assertEquals(1825986044, HashUtility.jsStringHash32(string));

        Assert.assertEquals(425323693, HashUtility.murmur1StringHash32(string));
        Assert.assertEquals(-275809860, HashUtility.murmur2StringHash32(string));
        Assert.assertEquals(1683129604, HashUtility.murmur3StringHash32(string));

        Assert.assertEquals(-1166771897, HashUtility.oneByOneStringHash32(string));

        Assert.assertEquals(140307872, HashUtility.pjwStringHash32(string));

        Assert.assertEquals(1361166149, HashUtility.rotatingStringHash32(string));
        Assert.assertEquals(-197131794, HashUtility.rsStringHash32(string));

        Assert.assertEquals(-845395960, HashUtility.sdbmStringHash32(string));
    }

    @Test
    public void testStringHash64() {
        String string = "abcdefghijklmnopqrstuvwxyz1234567890";
        Assert.assertEquals(-6348894989450125275L, HashUtility.apStringHash64(string));

        Assert.assertEquals(-3340620259685570120L, HashUtility.bkdrStringHash64(string));
        Assert.assertEquals(-5591551353237398352L, HashUtility.bpStringHash64(string));

        Assert.assertEquals(-2883402263783970963L, HashUtility.dekStringHash64(string));
        Assert.assertEquals(-999317002173065295L, HashUtility.djbStringHash64(string));

        Assert.assertEquals(126631744L, HashUtility.elfStringHash64(string));

        Assert.assertEquals(1012045081597555778L, HashUtility.fnv0StringHash64(string));

        Assert.assertEquals(-5709909030471576674L, HashUtility.jsStringHash64(string));

        Assert.assertEquals(126631744L, HashUtility.pjwStringHash64(string));

        Assert.assertEquals(-9134725312221806098L, HashUtility.rsStringHash64(string));

        Assert.assertEquals(-6905727910056015864L, HashUtility.sdbmStringHash64(string));

        Assert.assertEquals(603797126L, HashUtility.tianlStringHash64(string));
    }

    private final TreeMap<String, Method> numberHash32Methods = new TreeMap<>();

    private final TreeMap<String, Method> stringHash32Methods = new TreeMap<>();

    private final TreeMap<String, Method> stringHash64Methods = new TreeMap<>();

    {
        ReflectionUtility.doWithMethods(HashUtility.class, (method) -> {
            if (method.getName().endsWith("NumberHash32")) {
                numberHash32Methods.put(method.getName(), method);
            }
            if (method.getName().endsWith("StringHash32")) {
                stringHash32Methods.put(method.getName(), method);
            }
            if (method.getName().endsWith("StringHash64")) {
                stringHash64Methods.put(method.getName(), method);
            }
        }, (method) -> {
            // 选择参数数量为1的静态方法
            return method.getParameters().length == 1 && Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers());
        });
    }

    private double getVariance(Collection<Integer> counts) {
        double mean = Double.NaN;
        double variance = Double.NaN;
        int size = 0;
        Iterator<Integer> iterator = counts.iterator();
        if (iterator.hasNext()) {
            Integer term = iterator.next();
            double value = term;
            mean = value;
            size = 1;
            variance = 0;
        }
        while (iterator.hasNext()) {
            Integer term = iterator.next();
            double value = term;
            double delta = (value - mean);
            size++;
            mean += delta / size;
            variance += delta * (value - mean);
        }
        return variance / size;
    }

    private void test32Collision(String message, Function<Object, Integer> function, Object[] datas, Collection<Object> hashes, TreeMap<Integer, Integer> counts) throws Exception {
        double size = 1000D;
        double step = Integer.MAX_VALUE;
        step -= Integer.MIN_VALUE;
        step /= size;

        for (int index = 0; index < size; index++) {
            counts.put((int) (Integer.MAX_VALUE - step * index), 0);
        }

        long time = System.currentTimeMillis();
        int collision = 0;
        for (Object data : datas) {
            Integer hash = function.apply(data);
            Entry<Integer, Integer> term = counts.higherEntry(hash);
            counts.put(term.getKey(), term.getValue() + 1);
            if (!hashes.add(hash)) {
                collision++;
            }
        }

        message = StringUtility.format("{},哈希{}次,冲突{}次,方差{},时间{}毫秒", message, datas.length, collision, (long) getVariance(counts.values()), System.currentTimeMillis() - time);
        logger.debug(message);
        hashes.clear();
        counts.clear();
    }

    private void test64Collision(String message, Function<Object, Long> function, Object[] datas, Collection<Object> hashes, TreeMap<Long, Integer> counts) throws Exception {
        double size = 1000D;
        double step = Long.MAX_VALUE;
        step -= Long.MIN_VALUE;
        step /= size;

        for (int index = 0; index < size; index++) {
            counts.put((long) (Long.MAX_VALUE - step * index), 0);
        }

        long time = System.currentTimeMillis();
        int collision = 0;
        for (Object data : datas) {
            Long hash = function.apply(data);
            Entry<Long, Integer> term = counts.higherEntry(hash);
            counts.put(term.getKey(), term.getValue() + 1);
            if (!hashes.add(hash)) {
                collision++;
            }
        }

        message = StringUtility.format("{},哈希{}次,冲突{}次,方差{},时间{}毫秒", message, datas.length, collision, (long) getVariance(counts.values()), System.currentTimeMillis() - time);
        logger.debug(message);
        hashes.clear();
        counts.clear();
    }

    /**
     * 测试冲突
     */
    @Test
    public void testStringCollision() throws Exception {
        int size = 1000000;
        Object[] datas = new Object[size];
        Collection<Object> hashes = new HashSet<>(size);
        for (int index = 0; index < size; index++) {
            datas[index] = new UUID(RandomUtility.randomLong(), RandomUtility.randomLong()).toString();
        }
        TreeMap<Integer, Integer> count32s = new TreeMap<>();
        for (Method method : stringHash32Methods.values()) {
            test32Collision("UUID-" + method.getName(), (data) -> {
                try {
                    return Integer.class.cast(method.invoke(null, data));
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }, datas, hashes, count32s);
        }
        TreeMap<Long, Integer> count64s = new TreeMap<>();
        for (Method method : stringHash64Methods.values()) {
            test64Collision("UUID-" + method.getName(), (data) -> {
                try {
                    return Long.class.cast(method.invoke(null, data));
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }, datas, hashes, count64s);
        }

        for (int index = 0; index < size; index++) {
            datas[index] = index + size;
        }
        for (Method method : numberHash32Methods.values()) {
            test32Collision("连续整数-" + method.getName(), (data) -> {
                try {
                    return Integer.class.cast(method.invoke(null, data));
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }, datas, hashes, count32s);
        }
    }

}
