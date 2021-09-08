package com.jstarcraft.core.common.bloomfilter.bit;

/**
 * 
 * @author Birdy
 *
 */
public class LongMap implements BitMap<long[]> {

    private long[] bits;

    private int capacity;

    private int count;

    public LongMap(int capacity) {
        assert capacity > 0;
        int elements = capacity % Long.SIZE == 0 ? capacity / Long.SIZE : capacity / Long.SIZE + 1;
        this.bits = new long[elements];
        this.capacity = capacity;
        this.count = 0;
    }

    @Override
    public boolean get(int index) {
        int row = index / Long.SIZE;
        int column = index % Long.SIZE;
        return ((bits[row] >>> column) & 1L) == 1L;
    }

    @Override
    public void set(int index) {
        int row = index / Long.SIZE;
        int column = index % Long.SIZE;
        if (((bits[row] >>> column) & 1L) == 0L) {
            bits[row] |= (1L << column);
            count++;
        }
    }

    @Override
    public void unset(int index) {
        int row = index / Long.SIZE;
        int column = index % Long.SIZE;
        if (((bits[row] >>> column) & 1L) == 1L) {
            bits[row] &= ~(1L << column);
            count--;
        }
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public long[] bits() {
        return bits;
    }

}