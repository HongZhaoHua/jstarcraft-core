package com.jstarcraft.core.storage;

/**
 * ORM迭代器
 * 
 * 配合{@link StorageAccessor#iterate}实现遍历
 * 
 * @author Birdy
 *
 * @param <T>
 */
public interface StorageIterator<T> {

    /**
     * 遍历指定的对象
     * 
     * @param object
     */
    void iterate(T object);

}