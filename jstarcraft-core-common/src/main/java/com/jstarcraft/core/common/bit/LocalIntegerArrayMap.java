package com.jstarcraft.core.common.bit;

/**
 * 
 * @author Birdy
 *
 */
public class LocalIntegerArrayMap implements BitMap<int[]> {

    private int[] bits;

    private int capacity;

    private int count;

    protected LocalIntegerArrayMap(int[] bits, int capacity, int count) {
        this.bits = bits;
        this.capacity = capacity;
        this.count = count;
    }

    public LocalIntegerArrayMap(int capacity) {
        assert capacity > 0;
        int size = capacity % Integer.SIZE == 0 ? capacity / Integer.SIZE : capacity / Integer.SIZE + 1;
        this.bits = new int[size];
        this.capacity = capacity;
        this.count = 0;
    }

    @Override
    public boolean get(int index) {
        int row = index / Integer.SIZE;
        int column = 31 - index % Integer.SIZE;
        return ((bits[row] >>> column) & 1) == 1;
    }

    @Override
    public void set(int index) {
        int row = index / Integer.SIZE;
        int column = 31 - index % Integer.SIZE;
        if (((bits[row] >>> column) & 1) == 0) {
            bits[row] |= (1 << column);
            count++;
        }
    }

    @Override
    public void unset(int index) {
        int row = index / Integer.SIZE;
        int column = 31 - index % Integer.SIZE;
        if (((bits[row] >>> column) & 1) == 1) {
            bits[row] &= ~(1 << column);
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
    public int[] bits() {
        return bits;
    }

}