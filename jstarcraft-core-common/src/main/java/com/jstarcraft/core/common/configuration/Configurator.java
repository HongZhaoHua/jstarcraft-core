package com.jstarcraft.core.common.configuration;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 配置器
 * 
 * @author Birdy
 *
 */
public interface Configurator {

    default Class getClass(String name, Class instead) {
        String value = getString(name);
        try {
            return StringUtility.isBlank(value) ? instead : Class.forName(value);
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    default Class getClass(String name) {
        return getClass(name, null);
    }

    default Boolean getBoolean(String name, Boolean instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Boolean.valueOf(value);
    }

    default Boolean getBoolean(String name) {
        return getBoolean(name, null);
    }

    default Byte getByte(String name, Byte instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Byte.valueOf(value);
    }

    default Byte getByte(String name) {
        return getByte(name, null);
    }

    default Character getCharacter(String name, Character instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Character.valueOf(value.charAt(0));
    }

    default Character getCharacter(String name) {
        return getCharacter(name, null);
    }

    default Double getDouble(String name, Double instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Double.valueOf(value);
    }

    default Double getDouble(String name) {
        return getDouble(name, null);
    }

    default Float getFloat(String name, Float instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Float.valueOf(value);
    }

    default Float getFloat(String name) {
        return getFloat(name, null);
    }

    default Integer getInteger(String name, Integer instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Integer.valueOf(value);
    }

    default Integer getInteger(String name) {
        return getInteger(name, null);
    }

    default Long getLong(String name, Long instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Long.valueOf(value);
    }

    default Long getLong(String name) {
        return getLong(name, null);
    }
    
    default LocalDateTime getLocalDateTime(String name, LocalDateTime instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : LocalDateTime.parse(value);
    }

    default LocalDateTime getLocalDateTime(String name) {
        return getLocalDateTime(name, null);
    }

    default String getString(String name, String instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : value;
    }

    String getString(String name);
    
    default ZonedDateTime getZonedDateTime(String name, ZonedDateTime instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : ZonedDateTime.parse(value);
    }

    default ZonedDateTime getZonedDateTime(String name) {
        return getZonedDateTime(name, null);
    }

    public Collection<String> getKeys();

}
