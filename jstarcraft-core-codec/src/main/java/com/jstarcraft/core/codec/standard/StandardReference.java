package com.jstarcraft.core.codec.standard;

import java.util.ArrayList;

/**
 * Standard编解码引用
 * 
 * @author Birdy
 *
 * @param <T>
 */
public class StandardReference<T> {

    private final ArrayList<T> references = new ArrayList<>();

    public T getValue(int index) {
        if (index > references.size()) {
            return null;
        }
        T object = references.get(index);
        return object;
    }

    public int getIndex(T value) {
        // 不使用equal方法比较,而使用引用比较
        int size = references.size();
        for (int index = 0; index < size; index++) {
            if (references.get(index) == value) {
                return index;
            }
        }
        return -1;
    }

    public int putValue(T value) {
        int index = references.size();
        references.add(value);
        return index;
    }

}