package com.jstarcraft.core.common.option;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map.Entry;

import com.jstarcraft.core.utility.StringUtility;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException.Missing;
import com.typesafe.config.ConfigValue;

/**
 * HOCON配置器
 * 
 * @author Birdy
 *
 */
public class HoconOption implements TypeOption {

    private Config keyValues;

    public HoconOption(Config keyValues) {
        this.keyValues = keyValues;
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal instead) {
        try {
            String value = getString(key);
            return new BigDecimal(value);
        } catch (Missing exception) {
            return instead;
        }
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger instead) {
        try {
            String value = getString(key);
            return new BigInteger(value);
        } catch (Missing exception) {
            return instead;
        }
    }

    @Override
    public Boolean getBoolean(String key, Boolean instead) {
        try {
            return keyValues.getBoolean(key);
        } catch (Missing exception) {
            return instead;
        }
    }

    @Override
    public Byte getByte(String key, Byte instead) {
        try {
            Number value = keyValues.getNumber(key);
            return value.byteValue();
        } catch (Missing exception) {
            return instead;
        }
    }

    @Override
    public Character getCharacter(String key, Character instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Character.valueOf(value.charAt(0));
    }

    @Override
    public Double getDouble(String key, Double instead) {
        try {
            return keyValues.getDouble(key);
        } catch (Missing exception) {
            return instead;
        }
    }

    @Override
    public <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key, T instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Enum.valueOf(clazz, value);
    }

    @Override
    public Float getFloat(String key, Float instead) {
        try {
            Number value = keyValues.getNumber(key);
            return value.floatValue();
        } catch (Missing exception) {
            return instead;
        }
    }

    @Override
    public Integer getInteger(String key, Integer instead) {
        try {
            return keyValues.getInt(key);
        } catch (Missing exception) {
            return instead;
        }
    }

    @Override
    public Long getLong(String key, Long instead) {
        try {
            return keyValues.getLong(key);
        } catch (Missing exception) {
            return instead;
        }
    }

    @Override
    public Short getShort(String key, Short instead) {
        try {
            Number value = keyValues.getNumber(key);
            return value.shortValue();
        } catch (Missing exception) {
            return instead;
        }
    }

    @Override
    public String getString(String key, String instead) {
        try {
            return keyValues.getString(key);
        } catch (Missing exception) {
            return instead;
        }
    }

    private class HoconKeyIterator implements Iterator<String> {

        private Iterator<Entry<String, ConfigValue>> iterator;

        private HoconKeyIterator(Iterator<Entry<String, ConfigValue>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public String next() {
            Entry<String, ConfigValue> keyValue = iterator.next();
            return keyValue.getKey();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public Iterator<String> getKeys() {
        return new HoconKeyIterator(keyValues.entrySet().iterator());
    }

}
