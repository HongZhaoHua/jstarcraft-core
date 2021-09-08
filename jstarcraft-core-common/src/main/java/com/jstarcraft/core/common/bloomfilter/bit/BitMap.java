package com.jstarcraft.core.common.bloomfilter.bit;

/**
 * 
 * @author Birdy
 *
 */
// TODO 考虑实现Int2BooleanMap接口
public interface BitMap<T> {

    boolean get(int index);

    void set(int index);

    void unset(int index);

    int capacity();

    int size();
    
    T bits();

}