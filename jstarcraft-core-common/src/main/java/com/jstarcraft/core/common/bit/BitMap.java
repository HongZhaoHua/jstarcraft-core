package com.jstarcraft.core.common.bit;

/**
 * 
 * @author Birdy
 *
 */
// TODO 考虑实现Int2BooleanMap接口
public interface BitMap<T> {

    boolean get(int index);

    default void get(int[] indexes, boolean[] values) {
        for (int index = 0, size = indexes.length; index < size; index++) {
            values[index] = get(indexes[index]);
        }
    }

    void set(int index);

    default void set(int[] indexes) {
        for (int index : indexes) {
            set(index);
        }
    }

    void unset(int index);

    default void unset(int[] indexes) {
        for (int index : indexes) {
            unset(index);
        }
    }

    int capacity();

    int count();

    T bits();

}