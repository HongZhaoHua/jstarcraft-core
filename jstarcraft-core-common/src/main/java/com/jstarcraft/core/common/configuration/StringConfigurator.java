package com.jstarcraft.core.common.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 字符串配置器
 * 
 * @author Birdy
 *
 */
public interface StringConfigurator extends Configurator {

    default BigDecimal getBigDecimal(String key, BigDecimal instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : new BigDecimal(value);
    }

    default BigDecimal getBigDecimal(String key) {
        return getBigDecimal(key, null);
    }

    default BigInteger getBigInteger(String key, BigInteger instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : new BigInteger(value);
    }

    default BigInteger getBigInteger(String key) {
        return getBigInteger(key, null);
    }

    default Boolean getBoolean(String key, Boolean instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Boolean.valueOf(value);
    }

    default Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    default Byte getByte(String key, Byte instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Byte.valueOf(value);
    }

    default Byte getByte(String key) {
        return getByte(key, null);
    }

    default Character getCharacter(String key, Character instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Character.valueOf(value.charAt(0));
    }

    default Character getCharacter(String key) {
        return getCharacter(key, null);
    }

    default Class getClass(String key, Class instead) {
        String value = getString(key);
        try {
            return StringUtility.isBlank(value) ? instead : Class.forName(value);
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    default Class getClass(String key) {
        return getClass(key, null);
    }

    default Double getDouble(String key, Double instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Double.valueOf(value);
    }

    default Double getDouble(String key) {
        return getDouble(key, null);
    }

    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key, T instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Enum.valueOf(clazz, value);
    }

    default <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key) {
        return getEnumeration(clazz, key, null);
    }

    default Float getFloat(String key, Float instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Float.valueOf(value);
    }

    default Float getFloat(String key) {
        return getFloat(key, null);
    }

    default Integer getInteger(String key, Integer instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Integer.valueOf(value);
    }

    default Integer getInteger(String key) {
        return getInteger(key, null);
    }

    default Long getLong(String key, Long instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Long.valueOf(value);
    }

    default Long getLong(String key) {
        return getLong(key, null);
    }

    default LocalDateTime getLocalDateTime(String key, LocalDateTime instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : LocalDateTime.parse(value);
    }

    default LocalDateTime getLocalDateTime(String key) {
        return getLocalDateTime(key, null);
    }

    default <T> T getObject(Class<T> clazz, String key, T instead) {
        Object value = null;
        if (clazz == Boolean.class || clazz == boolean.class) {
            value = getBoolean(key);
        }
        if (clazz == Byte.class || clazz == byte.class) {
            value = getByte(key);
        }
        if (clazz == Character.class || clazz == char.class) {
            value = getCharacter(key);
        }
        if (clazz == Class.class) {
            value = getClass(key);
        }
        if (clazz == Double.class || clazz == double.class) {
            value = getDouble(key);
        }
        if (clazz == Float.class || clazz == float.class) {
            value = getFloat(key);
        }
        if (clazz == Integer.class || clazz == int.class) {
            value = getInteger(key);
        }
        if (clazz == Long.class || clazz == long.class) {
            value = getLong(key);
        }
        if (clazz == LocalDateTime.class) {
            value = getLocalDateTime(key);
        }
        if (clazz == String.class) {
            value = getString(key);
        }
        if (clazz == ZonedDateTime.class) {
            value = getZonedDateTime(key);
        }
        // TODO 不支持的类型
        return value == null ? instead : (T) value;
    }

    default <T> T getObject(Class<T> clazz, String key) {
        return getObject(clazz, key, null);
    }

    default String getString(String key, String instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : value;
    }

    String getString(String key);

    default ZonedDateTime getZonedDateTime(String key, ZonedDateTime instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : ZonedDateTime.parse(value);
    }

    default ZonedDateTime getZonedDateTime(String key) {
        return getZonedDateTime(key, null);
    }

}
