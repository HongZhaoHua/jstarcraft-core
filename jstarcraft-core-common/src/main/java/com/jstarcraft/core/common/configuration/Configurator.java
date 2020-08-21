package com.jstarcraft.core.common.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;

/**
 * 配置器
 * 
 * @author Birdy
 *
 */
// TODO 准备兼容Apache Commons Configuration
public interface Configurator {

    BigDecimal getBigDecimal(String name, BigDecimal instead);

    BigDecimal getBigDecimal(String name);

    BigInteger getBigInteger(String name, BigInteger instead);

    BigInteger getBigInteger(String name);

    Boolean getBoolean(String name, Boolean instead);

    Boolean getBoolean(String name);

    Byte getByte(String name, Byte instead);

    Byte getByte(String name);

    Character getCharacter(String name, Character instead);

    Character getCharacter(String name);

    Class getClass(String name, Class instead);

    Class getClass(String name);

    Double getDouble(String name, Double instead);

    Double getDouble(String name);

    <T extends Enum<T>> T getEnumeration(Class<T> clazz, String name, T instead);

    <T extends Enum<T>> T getEnumeration(Class<T> clazz, String name);

    Float getFloat(String name, Float instead);

    Float getFloat(String name);

    Integer getInteger(String name, Integer instead);

    Integer getInteger(String name);

    Long getLong(String name, Long instead);

    Long getLong(String name);

    LocalDateTime getLocalDateTime(String name, LocalDateTime instead);

    LocalDateTime getLocalDateTime(String name);

    <T> T getObject(Class<T> clazz, String name, T instead);

    <T> T getObject(Class<T> clazz, String name);

    String getString(String name, String instead);

    String getString(String name);

    ZonedDateTime getZonedDateTime(String name, ZonedDateTime instead);

    ZonedDateTime getZonedDateTime(String name);

    Collection<String> getKeys();

}
