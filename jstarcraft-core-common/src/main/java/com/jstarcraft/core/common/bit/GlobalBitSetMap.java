package com.jstarcraft.core.common.bit;

import org.redisson.api.RBitSet;

/**
 * 
 * @author Birdy
 *
 */
public class GlobalBitSetMap implements BitMap<byte[]> {

    private RBitSet bits;

    private int capacity;

    public GlobalBitSetMap(RBitSet bits, int capacity) {
        assert capacity > 0;
        this.bits = bits;
        this.capacity = capacity;
    }

    @Override
    public boolean get(int index) {
        return bits.get(index);
    }

    @Override
    public void set(int index) {
        bits.set(index, true);
    }

    @Override
    public void unset(int index) {
        bits.set(index, false);
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int count() {
        return (int) bits.cardinality();
    }

    @Override
    public byte[] bits() {
        return bits.toByteArray();
    }

}