package com.jstarcraft.core.common.bit;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.bit.IntegerMap;
import com.jstarcraft.core.common.bit.LongMap;

public class BitMapTestCase {
    
    @Test
    public void testByteMap() {
        IntegerMap bits = new IntegerMap(Byte.SIZE);
        Assert.assertEquals(Byte.SIZE, bits.capacity());
        Assert.assertEquals(0, bits.count());
        for (int index = 0; index < Byte.SIZE; index++) {
            Assert.assertFalse(bits.get(index));
            bits.set(index);
            Assert.assertTrue(bits.get(index));
            bits.unset(index);
            Assert.assertFalse(bits.get(index));
        }

        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.unset(Byte.SIZE - 1);
        Assert.assertEquals(1, bits.count());
        bits.unset(0);
        Assert.assertEquals(0, bits.count());
    }

    @Test
    public void testIntegerMap() {
        IntegerMap bits = new IntegerMap(Integer.SIZE);
        Assert.assertEquals(Integer.SIZE, bits.capacity());
        Assert.assertEquals(0, bits.count());
        for (int index = 0; index < Integer.SIZE; index++) {
            Assert.assertFalse(bits.get(index));
            bits.set(index);
            Assert.assertTrue(bits.get(index));
            bits.unset(index);
            Assert.assertFalse(bits.get(index));
        }

        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.unset(Integer.SIZE - 1);
        Assert.assertEquals(1, bits.count());
        bits.unset(0);
        Assert.assertEquals(0, bits.count());
    }

    @Test
    public void testLongMap() {
        LongMap bits = new LongMap(Long.SIZE);
        Assert.assertEquals(Long.SIZE, bits.capacity());
        Assert.assertEquals(0, bits.count());
        for (int index = 0; index < Long.SIZE; index++) {
            Assert.assertFalse(bits.get(index));
            bits.set(index);
            Assert.assertTrue(bits.get(index));
            bits.unset(index);
            Assert.assertFalse(bits.get(index));
        }

        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.set(0);
        Assert.assertEquals(1, bits.count());
        bits.unset(Long.SIZE - 1);
        Assert.assertEquals(1, bits.count());
        bits.unset(0);
        Assert.assertEquals(0, bits.count());
    }

}
