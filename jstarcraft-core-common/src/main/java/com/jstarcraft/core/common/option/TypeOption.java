package com.jstarcraft.core.common.option;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 类型配置器
 * 
 * @author Birdy
 *
 */
public interface TypeOption extends Option {

    @Override
    default BigDecimal getBigDecimal(String key) {
        return getBigDecimal(key, null);
    }

    @Override
    default BigInteger getBigInteger(String key) {
        return getBigInteger(key, null);
    }

    @Override
    default Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    @Override
    default Byte getByte(String key) {
        return getByte(key, null);
    }

    @Override
    default Character getCharacter(String key) {
        return getCharacter(key, null);
    }

    @Override
    default Double getDouble(String key) {
        return getDouble(key, null);
    }

    @Override
    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key) {
        return getEnumeration(clazz, key, null);
    }

    @Override
    default Float getFloat(String key) {
        return getFloat(key, null);
    }

    @Override
    default Integer getInteger(String key) {
        return getInteger(key, null);
    }

    @Override
    default Long getLong(String key) {
        return getLong(key, null);
    }
    
    @Override
    default Short getShort(String key) {
        return getShort(key, null);
    }

    @Override
    default String getString(String key) {
        return getString(key, null);
    }

}
