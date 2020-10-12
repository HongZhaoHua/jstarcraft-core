package com.jstarcraft.core.common.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 字符串配置器
 * 
 * @author Birdy
 *
 */
public interface StringConfigurator extends Configurator {

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
    default Class getClass(String key, Class instead) {
        String value = getString(key);
        try {
            return StringUtility.isBlank(value) ? instead : Class.forName(value);
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    default Class getClass(String key) {
        return getClass(key, null);
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
        if (clazz == String.class) {
            value = getString(key);
        }
        // TODO 不支持的类型
        return value == null ? instead : (T) value;
    }

    @Override
    default <T> T getObject(Class<T> clazz, String key) {
        return getObject(clazz, key, null);
    }

    @Override
    default String getString(String key, String instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : value;
    }

}
