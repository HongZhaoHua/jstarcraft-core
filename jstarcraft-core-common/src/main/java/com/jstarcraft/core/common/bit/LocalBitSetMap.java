package com.jstarcraft.core.common.bit;

import java.util.BitSet;

/**
 * 
 * @author Birdy
 *
 */
public class LocalBitSetMap implements BitMap<byte[]> {

    private BitSet bits;

    private int capacity;

    protected LocalBitSetMap(BitSet bits, int capacity) {
        this.bits = bits;
        this.capacity = capacity;
    }

    public LocalBitSetMap(int capacity) {
        assert capacity > 0;
        this.bits = new BitSet(capacity);
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
        return bits.cardinality();
    }

    @Override
    public byte[] bits() {
        return bits.toByteArray();
    }

}