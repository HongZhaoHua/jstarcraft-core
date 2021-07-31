package com.jstarcraft.core.common.option;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 字符串配置器
 * 
 * @author Birdy
 *
 */
public interface StringOption extends Option {

    @Override
    default BigDecimal getBigDecimal(String key, BigDecimal instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : new BigDecimal(value);
    }

    @Override
    default BigDecimal getBigDecimal(String key) {
        return getBigDecimal(key, null);
    }

    @Override
    default BigInteger getBigInteger(String key, BigInteger instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : new BigInteger(value);
    }

    @Override
    default BigInteger getBigInteger(String key) {
        return getBigInteger(key, null);
    }

    @Override
    default Boolean getBoolean(String key, Boolean instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Boolean.valueOf(value);
    }

    @Override
    default Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    @Override
    default Byte getByte(String key, Byte instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Byte.valueOf(value);
    }

    @Override
    default Byte getByte(String key) {
        return getByte(key, null);
    }

    @Override
    default Character getCharacter(String key, Character instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Character.valueOf(value.charAt(0));
    }

    @Override
    default Character getCharacter(String key) {
        return getCharacter(key, null);
    }

    @Override
    default Double getDouble(String key, Double instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Double.valueOf(value);
    }

    @Override
    default Double getDouble(String key) {
        return getDouble(key, null);
    }

    @Override
    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key, T instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Enum.valueOf(clazz, value);
    }

    @Override
    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key) {
        return getEnumeration(clazz, key, null);
    }

    @Override
    default Float getFloat(String key, Float instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Float.valueOf(value);
    }

    @Override
    default Float getFloat(String key) {
        return getFloat(key, null);
    }

    @Override
    default Integer getInteger(String key, Integer instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Integer.valueOf(value);
    }

    @Override
    default Integer getInteger(String key) {
        return getInteger(key, null);
    }

    @Override
    default Long getLong(String key, Long instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Long.valueOf(value);
    }

    @Override
    default Long getLong(String key) {
        return getLong(key, null);
    }

    @Override
    default Short getShort(String key, Short instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Short.valueOf(value);
    }

    @Override
    default Short getShort(String key) {
        return getShort(key, null);
    }

    @Override
    default String getString(String key, String instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : value;
    }

}
