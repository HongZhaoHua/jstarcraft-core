package com.jstarcraft.core.common.bit;

/**
 * 
 * @author Birdy
 *
 */
public class LocalByteArrayMap implements BitMap<byte[]> {

    private byte[] bits;

    private int capacity;

    private int count;

    protected LocalByteArrayMap(byte[] bits, int capacity, int count) {
        this.bits = bits;
        this.capacity = capacity;
        this.count = count;
    }

    public LocalByteArrayMap(int capacity) {
        assert capacity > 0;
        int size = capacity % Byte.SIZE == 0 ? capacity / Byte.SIZE : capacity / Byte.SIZE + 1;
        this.bits = new byte[size];
        this.capacity = capacity;
        this.count = 0;
    }

    @Override
    public boolean get(int index) {
        int row = index / Byte.SIZE;
        int column = 7 - index % Byte.SIZE;
        return ((bits[row] >>> column) & 1) == 1;
    }

    @Override
    public void set(int index) {
        int row = index / Byte.SIZE;
        int column = 7 - index % Byte.SIZE;
        if (((bits[row] >>> column) & 1) == 0) {
            bits[row] |= (1 << column);
            count++;
        }
    }

    @Override
    public void unset(int index) {
        int row = index / Byte.SIZE;
        int column = 7 - index % Byte.SIZE;
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
    public byte[] bits() {
        return bits;
    }

}