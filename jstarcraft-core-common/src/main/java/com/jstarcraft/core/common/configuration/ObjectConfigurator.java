package com.jstarcraft.core.common.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 对象配置器
 * 
 * @author Birdy
 *
 */
public interface ObjectConfigurator extends Configurator {

    @Override
    default BigDecimal getBigDecimal(String key, BigDecimal instead) {
        BigDecimal value = getObject(BigDecimal.class, key, instead);
        return value == null ? instead : value;
    }

    @Override
    default BigDecimal getBigDecimal(String key) {
        return getBigDecimal(key, null);
    }

    @Override
    default BigInteger getBigInteger(String key, BigInteger instead) {
        BigInteger value = getObject(BigInteger.class, key, instead);
        return value == null ? instead : value;
    }

    @Override
    default BigInteger getBigInteger(String key) {
        return getBigInteger(key, null);
    }

    @Override
    default Boolean getBoolean(String key, Boolean instead) {
        Boolean value = getObject(Boolean.class, key, instead);
        return value == null ? instead : value;
    }

    @Override
    default Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    @Override
    default Byte getByte(String key, Byte instead) {
        Byte value = getObject(Byte.class, key, instead);
        return value == null ? instead : value;
    }

    @Override
    default Byte getByte(String key) {
        return getByte(key, null);
    }

    @Override
    default Character getCharacter(String key, Character instead) {
        Character value = getObject(Character.class, key, instead);
        return value == null ? instead : value;
    }

    @Override
    default Character getCharacter(String key) {
        return getCharacter(key, null);
    }

    @Override
    default Class getClass(String key, Class instead) {
        Class value = getObject(Class.class, key, instead);
        return value == null ? instead : value;
    }

    @Override
    default Class getClass(String key) {
        return getClass(key, null);
    }

    @Override
    default Double getDouble(String key, Double instead) {
        Double value = getObject(Double.class, key, instead);
        return value == null ? instead : value;
    }

    @Override
    default Double getDouble(String key) {
        return getDouble(key, null);
    }

    @Override
    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key, T instead) {
        Enum value = getObject(Enum.class, key, instead);
        return value == null ? instead : (T) value;
    }

    @Override
    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key) {
        return getEnumeration(clazz, key, null);
    }

    @Override
    default Float getFloat(String key, Float instead) {
        Float value = getObject(Float.class, key, instead);
        return value == null ? instead : value;
    }

    @Override
    default Float getFloat(String key) {
        return getFloat(key, null);
    }

    @Override
    default Integer getInteger(String key, Integer instead) {
        Integer value = getObject(Integer.class, key, instead);
        return value == null ? instead : value;
    }

    @Override
    default Integer getInteger(String key) {
        return getInteger(key, null);
    }

    @Override
    default Long getLong(String key, Long instead) {
        Long value = getObject(Long.class, key, instead);
        return value == null ? instead : value;
    }

    @Override
    default Long getLong(String key) {
        return getLong(key, null);
    }

    @Override
    default <T> T getObject(Class<T> clazz, String key, T instead) {
        T value = getObject(clazz, key);
        return value == null ? instead : value;
    }

    @Override
    default String getString(String key, String instead) {
        String value = getObject(String.class, key, instead);
        return value == null ? instead : value;
    }

    @Override
    default String getString(String key) {
        return getString(key, null);
    }

}
