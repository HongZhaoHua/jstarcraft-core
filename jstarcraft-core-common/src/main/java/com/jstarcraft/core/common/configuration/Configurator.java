package com.jstarcraft.core.common.configuration;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;

/**
 * 配置器
 * 
 * @author Birdy
 *
 */
public interface Configurator {

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
