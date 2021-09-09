package com.jstarcraft.core.common.bit;

/**
 * 
 * @author Birdy
 *
 */
// TODO 考虑实现Int2BooleanMap接口
public interface BitMap<T> {

    boolean get(int index);

    default int get(int[] indexes) {
        int hit = 0;
        for (int index : indexes) {
            if (get(index)) {
                hit++;
            }
        }
        return hit;
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