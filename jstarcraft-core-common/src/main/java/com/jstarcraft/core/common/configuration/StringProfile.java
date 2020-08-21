package com.jstarcraft.core.common.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
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
public interface StringProfile extends Configurator {
    
    default BigDecimal getBigDecimal(String name, BigDecimal instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : new BigDecimal(value);
    }

    default BigDecimal getBigDecimal(String name) {
        return getBigDecimal(name, null);
    }
    
    default BigInteger getBigInteger(String name, BigInteger instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : new BigInteger(value);
    }

    default BigInteger getBigInteger(String name) {
        return getBigInteger(name, null);
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

    default Double getDouble(String name, Double instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Double.valueOf(value);
    }

    default Double getDouble(String name) {
        return getDouble(name, null);
    }

    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String name, T instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Enum.valueOf(clazz, value);
    }

    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String name) {
        return getEnumeration(clazz, name, null);
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

    default <T> T getObject(Class<T> clazz, String name, T instead) {
        Object value = null;
        if (clazz == Boolean.class || clazz == boolean.class) {
            value = getBoolean(name);
        }
        if (clazz == Byte.class || clazz == byte.class) {
            value = getByte(name);
        }
        if (clazz == Character.class || clazz == char.class) {
            value = getCharacter(name);
        }
        if (clazz == Class.class) {
            value = getClass(name);
        }
        if (clazz == Double.class || clazz == double.class) {
            value = getDouble(name);
        }
        if (clazz == Float.class || clazz == float.class) {
            value = getFloat(name);
        }
        if (clazz == Integer.class || clazz == int.class) {
            value = getInteger(name);
        }
        if (clazz == Long.class || clazz == long.class) {
            value = getLong(name);
        }
        if (clazz == LocalDateTime.class) {
            value = getLocalDateTime(name);
        }
        if (clazz == String.class) {
            value = getString(name);
        }
        if (clazz == ZonedDateTime.class) {
            value = getZonedDateTime(name);
        }
        // TODO 不支持的类型
        return value == null ? instead : (T) value;
    }

    default <T> T getObject(Class<T> clazz, String name) {
        return getObject(clazz, name, null);
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
