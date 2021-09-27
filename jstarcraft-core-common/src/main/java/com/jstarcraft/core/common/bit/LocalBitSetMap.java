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
        int row = index / Byte.SIZE;
        int column = 7 - index % Byte.SIZE;
        index = row * Byte.SIZE + column;
        return bits.get(index);
    }

    @Override
    public void set(int index) {
        int row = index / Byte.SIZE;
        int column = 7 - index % Byte.SIZE;
        index = row * Byte.SIZE + column;
        bits.set(index, true);
    }

    @Override
    public void unset(int index) {
        int row = index / Byte.SIZE;
        int column = 7 - index % Byte.SIZE;
        index = row * Byte.SIZE + column;
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
        byte[] from = bits.toByteArray();
        int size = capacity % Byte.SIZE == 0 ? capacity / Byte.SIZE : capacity / Byte.SIZE + 1;
        byte[] to = new byte[size];
        System.arraycopy(from, 0, to, 0, from.length);
        return to;
    }

}