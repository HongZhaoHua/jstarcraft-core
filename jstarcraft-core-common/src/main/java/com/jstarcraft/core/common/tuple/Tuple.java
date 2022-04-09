package com.jstarcraft.core.common.tuple;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;

/**
 * 元组
 * 
 * @author Birdy
 *
 */
public class Tuple implements Iterable<Object> {

    protected Object[] datas;

    protected Iterator<Object> iterator;

    public Tuple(Object... datas) {
        this.datas = datas;
        this.iterator = Arrays.asList(datas).iterator();
    }

    public Object getData(int index) {
        return datas[index];
    }

    public void setData(int index, Object data) {
        datas[index] = data;
    }

    public <T> T getData(int index, Class<T> clazz) {
        return (T) datas[index];
    }

    public BigDecimal getBigDecimal(int index) {
        return getData(index, BigDecimal.class);
    }

    public BigInteger getBigInteger(int index) {
        return getData(index, BigInteger.class);
    }

    public Boolean getBoolean(int index) {
        return getData(index, Boolean.class);
    }

    public Byte getByte(int index) {
        return getData(index, Byte.class);
    }

    public Character getCharacter(int index) {
        return getData(index, Character.class);
    }

    public Double getDouble(int index) {
        return getData(index, Double.class);
    }

    public Float getFloat(int index) {
        return getData(index, Float.class);
    }

    public Integer getInteger(int index) {
        return getData(index, Integer.class);
    }

    public Long getLong(int index) {
        return getData(index, Long.class);
    }

    public Short getShort(int index) {
        return getData(index, Short.class);
    }

    public String getString(int index) {
        return getData(index, String.class);
    }

    @Override
    public Iterator<Object> iterator() {
        return iterator;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        Tuple that = (Tuple) object;
        if (!Arrays.equals(this.datas, that.datas))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = prime * hash + Arrays.hashCode(datas);
        return hash;
    }

}
