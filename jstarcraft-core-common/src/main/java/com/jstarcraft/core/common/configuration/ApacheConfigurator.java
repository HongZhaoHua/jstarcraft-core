package com.jstarcraft.core.common.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

import org.apache.commons.configuration2.Configuration;

import com.jstarcraft.core.utility.StringUtility;

/**
 * Apache配置器
 * 
 * @author Birdy
 *
 */
public class ApacheConfigurator implements Configurator {

    private Configuration keyValues;

    public ApacheConfigurator(Configuration keyValues) {
        this.keyValues = keyValues;
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal instead) {
        return keyValues.getBigDecimal(key, instead);
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return keyValues.getBigDecimal(key);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger instead) {
        return keyValues.getBigInteger(key, instead);
    }

    @Override
    public BigInteger getBigInteger(String key) {
        return keyValues.getBigInteger(key);
    }

    @Override
    public Boolean getBoolean(String key, Boolean instead) {
        return keyValues.getBoolean(key, instead);
    }

    @Override
    public Boolean getBoolean(String key) {
        return keyValues.getBoolean(key);
    }

    @Override
    public Byte getByte(String key, Byte instead) {
        return keyValues.getByte(key, instead);
    }

    @Override
    public Byte getByte(String key) {
        return keyValues.getByte(key);
    }

    @Override
    public Character getCharacter(String key, Character instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Character.valueOf(value.charAt(0));
    }

    @Override
    public Character getCharacter(String key) {
        return getCharacter(key, null);
    }

    @Override
    public Double getDouble(String key, Double instead) {
        return keyValues.getDouble(key, instead);
    }

    @Override
    public Double getDouble(String key) {
        return keyValues.getDouble(key, null);
    }

    @Override
    public <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key, T instead) {
        String value = getString(key);
        return StringUtility.isBlank(value) ? instead : Enum.valueOf(clazz, value);
    }

    @Override
    public <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key) {
        return getEnumeration(clazz, key, null);
    }

    @Override
    public Float getFloat(String key, Float instead) {
        return keyValues.getFloat(key, instead);
    }

    @Override
    public Float getFloat(String key) {
        return keyValues.getFloat(key, null);
    }

    @Override
    public Integer getInteger(String key, Integer instead) {
        return keyValues.getInteger(key, instead);
    }

    @Override
    public Integer getInteger(String key) {
        return keyValues.getInteger(key, null);
    }

    @Override
    public Long getLong(String key, Long instead) {
        return keyValues.getLong(key, instead);
    }

    @Override
    public Long getLong(String key) {
        return keyValues.getLong(key, null);
    }

    @Override
    public String getString(String key, String instead) {
        return keyValues.getString(key, instead);
    }

    @Override
    public String getString(String key) {
        return keyValues.getString(key);
    }

    @Override
    public Iterator<String> getKeys() {
        return keyValues.getKeys();
    }

}
