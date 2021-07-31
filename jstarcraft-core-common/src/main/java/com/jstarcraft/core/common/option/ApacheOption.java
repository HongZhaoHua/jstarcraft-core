package com.jstarcraft.core.common.option;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

import org.apache.commons.configuration2.Configuration;

import com.jstarcraft.core.utility.StringUtility;

/**
 * Apache Configuration2配置器
 * 
 * @author Birdy
 *
 */
public class ApacheOption implements TypeOption {

    private Configuration keyValues;

    public ApacheOption(Configuration keyValues) {
        this.keyValues = keyValues;
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal instead) {
        return keyValues.getBigDecimal(key, instead);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger instead) {
        return keyValues.getBigInteger(key, instead);
    }

    @Override
    public Boolean getBoolean(String key, Boolean instead) {
        return keyValues.getBoolean(key, instead);
    }

    @Override
    public Byte getByte(String key, Byte instead) {
        return keyValues.getByte(key, instead);
    }

    @Override
    public Character getCharacter(String key, Character instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Character.valueOf(value.charAt(0));
    }

    @Override
    public Double getDouble(String key, Double instead) {
        return keyValues.getDouble(key, instead);
    }

    @Override
    public <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key, T instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Enum.valueOf(clazz, value);
    }

    @Override
    public Float getFloat(String key, Float instead) {
        return keyValues.getFloat(key, instead);
    }

    @Override
    public Integer getInteger(String key, Integer instead) {
        return keyValues.getInteger(key, instead);
    }

    @Override
    public Long getLong(String key, Long instead) {
        return keyValues.getLong(key, instead);
    }

    @Override
    public Short getShort(String key, Short instead) {
        return keyValues.getShort(key, instead);
    }

    @Override
    public String getString(String key, String instead) {
        return keyValues.getString(key, instead);
    }

    @Override
    public Iterator<String> getKeys() {
        return keyValues.getKeys();
    }

}
