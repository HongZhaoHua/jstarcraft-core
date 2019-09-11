package com.jstarcraft.core.common.lockable;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 排序锁
 * 
 * <pre>
 * 排序顺序说明:
 * 1.不同类型的对象,按{@link Class}排序
 * 2.相同类型的对象,按{@link Comparable}排序
 * </pre>
 * 
 * @author Birdy
 */
public class CompareLock extends ReentrantLock implements Comparable<CompareLock> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompareLock.class);

    /** 对象的类型 */
    private final Class clazz;
    /** 对象的排序值 */
    private final Comparable value;

    /**
     * 排序锁
     * 
     * @param object
     * @param fair
     */
    public CompareLock(Comparable object, boolean fair) {
        super(fair);
        clazz = object.getClass();
        value = object;
    }

    /**
     * 获取对象的类型
     * 
     * @return
     */
    public Class getClazz() {
        return clazz;
    }

    /**
     * 获取对象的排序值
     * 
     * @return
     */
    public Comparable getValue() {
        return value;
    }

    @Override
    public int compareTo(CompareLock that) {
        int compare = this.clazz.getName().compareTo(that.clazz.getName());
        if (compare == 0) {
            return this.value.compareTo(that.value);
        } else {
            return compare;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        CompareLock that = (CompareLock) object;
        if (this.clazz == that.clazz) {
            return this.value.compareTo(that.value) == 0;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(value);
        return hash.toHashCode();
    }

    @Override
    public String toString() {
        return StringUtility.reflect(this);
    }

}
