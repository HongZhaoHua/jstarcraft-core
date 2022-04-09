package com.jstarcraft.core.common.tuple;

import java.math.BigDecimal;
import java.math.BigInteger;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * 映射元组
 * 
 * @author Birdy
 *
 */
public class MapTuple extends Tuple {

    protected Object2IntMap<String> indexes;

    public MapTuple(Object2IntMap<String> indexes, Object... datas) {
        super(datas);
        this.indexes = indexes;
    }

    public Object getData(String key) {
        return datas[indexes.getInt(key)];
    }

    public void setData(String key, Object data) {
        datas[indexes.getInt(key)] = data;
    }

    public <T> T getData(String key, Class<T> clazz) {
        return (T) datas[indexes.getInt(key)];
    }

    public BigDecimal getBigDecimal(String key) {
        return getData(key, BigDecimal.class);
    }

    public BigInteger getBigInteger(String key) {
        return getData(key, BigInteger.class);
    }

    public Boolean getBoolean(String key) {
        return getData(key, Boolean.class);
    }

    public Byte getByte(String key) {
        return getData(key, Byte.class);
    }

    public Character getCharacter(String key) {
        return getData(key, Character.class);
    }

    public Double getDouble(String key) {
        return getData(key, Double.class);
    }

    public Float getFloat(String key) {
        return getData(key, Float.class);
    }

    public Integer getInteger(String key) {
        return getData(key, Integer.class);
    }

    public Long getLong(String key) {
        return getData(key, Long.class);
    }

    public Short getShort(String key) {
        return getData(key, Short.class);
    }

    public String getString(String key) {
        return getData(key, String.class);
    }

}
