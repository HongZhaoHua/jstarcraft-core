package com.jstarcraft.core.common.configuration;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;

public interface ObjectProfile extends Configurator {

    default Boolean getBoolean(String name, Boolean instead) {
        Boolean value = getObject(Boolean.class, name, instead);
        return value == null ? instead : value;
    }

    default Boolean getBoolean(String name) {
        return getBoolean(name, null);
    }

    default Byte getByte(String name, Byte instead) {
        Byte value = getObject(Byte.class, name, instead);
        return value == null ? instead : value;
    }

    default Byte getByte(String name) {
        return getByte(name, null);
    }

    default Character getCharacter(String name, Character instead) {
        Character value = getObject(Character.class, name, instead);
        return value == null ? instead : value;
    }

    default Character getCharacter(String name) {
        return getCharacter(name, null);
    }

    default Class getClass(String name, Class instead) {
        Class value = getObject(Class.class, name, instead);
        return value == null ? instead : value;
    }

    default Class getClass(String name) {
        return getClass(name, null);
    }

    default Double getDouble(String name, Double instead) {
        Double value = getObject(Double.class, name, instead);
        return value == null ? instead : value;
    }

    default Double getDouble(String name) {
        return getDouble(name, null);
    }

    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String name, T instead) {
        Enum value = getObject(Enum.class, name, instead);
        return value == null ? instead : (T) value;
    }

    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String name) {
        return getEnumeration(clazz, name, null);
    }

    default Float getFloat(String name, Float instead) {
        Float value = getObject(Float.class, name, instead);
        return value == null ? instead : value;
    }

    default Float getFloat(String name) {
        return getFloat(name, null);
    }

    default Integer getInteger(String name, Integer instead) {
        Integer value = getObject(Integer.class, name, instead);
        return value == null ? instead : value;
    }

    default Integer getInteger(String name) {
        return getInteger(name, null);
    }

    default Long getLong(String name, Long instead) {
        Long value = getObject(Long.class, name, instead);
        return value == null ? instead : value;
    }

    default Long getLong(String name) {
        return getLong(name, null);
    }

    default LocalDateTime getLocalDateTime(String name, LocalDateTime instead) {
        LocalDateTime value = getObject(LocalDateTime.class, name, instead);
        return value == null ? instead : value;
    }

    default LocalDateTime getLocalDateTime(String name) {
        return getLocalDateTime(name, null);
    }

    default <T> T getObject(Class<T> clazz, String name, T instead) {
        T value = getObject(clazz, name);
        return value == null ? instead : value;
    }

    <T> T getObject(Class<T> clazz, String name);

    default String getString(String name, String instead) {
        String value = getObject(String.class, name, instead);
        return value == null ? instead : value;
    }

    default String getString(String name) {
        return getString(name, null);
    }

    default ZonedDateTime getZonedDateTime(String name, ZonedDateTime instead) {
        ZonedDateTime value = getObject(ZonedDateTime.class, name, instead);
        return value == null ? instead : value;
    }

    default ZonedDateTime getZonedDateTime(String name) {
        return getZonedDateTime(name, null);
    }

    public Collection<String> getKeys();

}
