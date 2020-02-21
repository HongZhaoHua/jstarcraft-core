package com.jstarcraft.core.orm;

/**
 * ORM迭代器
 * 
 * 配合{@link OrmAccessor#iterate}实现遍历
 * 
 * @author Birdy
 *
 * @param <T>
 */
public interface OrmIterator<T> {

    /**
     * 遍历指定的对象
     * 
     * @param object
     */
    void iterate(T object);

}