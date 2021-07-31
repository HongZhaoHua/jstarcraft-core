package com.jstarcraft.core.common.option;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

/**
 * 配置器
 * 
 * @author Birdy
 *
 */
// TODO 考虑改名Option
public interface Option {

    BigDecimal getBigDecimal(String key, BigDecimal instead);

    BigDecimal getBigDecimal(String key);

    BigInteger getBigInteger(String key, BigInteger instead);

    BigInteger getBigInteger(String key);

    Boolean getBoolean(String key, Boolean instead);

    Boolean getBoolean(String key);

    Byte getByte(String key, Byte instead);

    Byte getByte(String key);

    Character getCharacter(String key, Character instead);

    Character getCharacter(String key);

    Double getDouble(String key, Double instead);

    Double getDouble(String key);

    <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key, T instead);

    <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key);

    Float getFloat(String key, Float instead);

    Float getFloat(String key);

    Integer getInteger(String key, Integer instead);

    Integer getInteger(String key);

    Long getLong(String key, Long instead);

    Long getLong(String key);
    
    Short getShort(String key, Short instead);

    Short getShort(String key);

    String getString(String key, String instead);

    String getString(String key);

    Iterator<String> getKeys();

}
