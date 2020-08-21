package com.jstarcraft.core.common.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * 对象配置器
 * 
 * @author Birdy
 *
 */
public interface ObjectConfigurator extends Configurator {

    default BigDecimal getBigDecimal(String key, BigDecimal instead) {
        BigDecimal value = getObject(BigDecimal.class, key, instead);
        return value == null ? instead : value;
    }

    default BigDecimal getBigDecimal(String key) {
        return getBigDecimal(key, null);
    }

    default BigInteger getBigInteger(String key, BigInteger instead) {
        BigInteger value = getObject(BigInteger.class, key, instead);
        return value == null ? instead : value;
    }

    default BigInteger getBigInteger(String key) {
        return getBigInteger(key, null);
    }

    default Boolean getBoolean(String key, Boolean instead) {
        Boolean value = getObject(Boolean.class, key, instead);
        return value == null ? instead : value;
    }

    default Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    default Byte getByte(String key, Byte instead) {
        Byte value = getObject(Byte.class, key, instead);
        return value == null ? instead : value;
    }

    default Byte getByte(String key) {
        return getByte(key, null);
    }

    default Character getCharacter(String key, Character instead) {
        Character value = getObject(Character.class, key, instead);
        return value == null ? instead : value;
    }

    default Character getCharacter(String key) {
        return getCharacter(key, null);
    }

    default Class getClass(String key, Class instead) {
        Class value = getObject(Class.class, key, instead);
        return value == null ? instead : value;
    }

    default Class getClass(String key) {
        return getClass(key, null);
    }

    default Double getDouble(String key, Double instead) {
        Double value = getObject(Double.class, key, instead);
        return value == null ? instead : value;
    }

    default Double getDouble(String key) {
        return getDouble(key, null);
    }

    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key, T instead) {
        Enum value = getObject(Enum.class, key, instead);
        return value == null ? instead : (T) value;
    }

    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key) {
        return getEnumeration(clazz, key, null);
    }

    default Float getFloat(String key, Float instead) {
        Float value = getObject(Float.class, key, instead);
        return value == null ? instead : value;
    }

    default Float getFloat(String key) {
        return getFloat(key, null);
    }

    default Integer getInteger(String key, Integer instead) {
        Integer value = getObject(Integer.class, key, instead);
        return value == null ? instead : value;
    }

    default Integer getInteger(String key) {
        return getInteger(key, null);
    }

    default Long getLong(String key, Long instead) {
        Long value = getObject(Long.class, key, instead);
        return value == null ? instead : value;
    }

    default Long getLong(String key) {
        return getLong(key, null);
    }

    default LocalDateTime getLocalDateTime(String key, LocalDateTime instead) {
        LocalDateTime value = getObject(LocalDateTime.class, key, instead);
        return value == null ? instead : value;
    }

    default LocalDateTime getLocalDateTime(String key) {
        return getLocalDateTime(key, null);
    }

    default <T> T getObject(Class<T> clazz, String key, T instead) {
        T value = getObject(clazz, key);
        return value == null ? instead : value;
    }

    <T> T getObject(Class<T> clazz, String key);

    default String getString(String key, String instead) {
        String value = getObject(String.class, key, instead);
        return value == null ? instead : value;
    }

    default String getString(String key) {
        return getString(key, null);
    }

    default ZonedDateTime getZonedDateTime(String key, ZonedDateTime instead) {
        ZonedDateTime value = getObject(ZonedDateTime.class, key, instead);
        return value == null ? instead : value;
    }

    default ZonedDateTime getZonedDateTime(String key) {
        return getZonedDateTime(key, null);
    }

}
