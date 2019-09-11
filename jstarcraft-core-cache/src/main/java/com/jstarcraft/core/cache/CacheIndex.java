package com.jstarcraft.core.cache;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 缓存索引
 * 
 * @author Birdy
 */
public class CacheIndex implements Comparable<CacheIndex> {

    /** 索引名 */
    private final String name;

    /** 索引值 */
    private final Comparable value;

    public CacheIndex(String key, Comparable value) {
        this.name = key;
        this.value = value;
    }

    /**
     * 获取索引名
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 获取索引值
     * 
     * @return
     */
    public Comparable getValue() {
        return value;
    }

    /**
     * 获取索引值
     * 
     * @param clazz
     * @return
     */
    public <T> T getValue(Class<T> clazz) {
        return clazz.cast(value);
    }

    @Override
    public int compareTo(CacheIndex that) {
        CompareToBuilder comparator = new CompareToBuilder();
        comparator.append(this.name, that.name);
        comparator.append(this.value, that.value);
        return comparator.toComparison();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        CacheIndex that = (CacheIndex) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.name, that.name);
        equal.append(this.value, that.value);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(name);
        hash.append(value);
        return hash.toHashCode();
    }

}
