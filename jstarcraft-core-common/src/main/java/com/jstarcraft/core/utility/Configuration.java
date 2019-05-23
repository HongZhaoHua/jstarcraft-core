package com.jstarcraft.core.utility;

import java.util.Map;
import java.util.Properties;

/**
 * 配置
 * 
 * @author Birdy
 *
 */
public class Configuration {

    /** 配置项 */
    private Properties keyValues;
    
    public Configuration(Map<?, ?>... properties) {
        this.keyValues = new Properties();
        for (Map<?, ?> property : properties) {
            this.keyValues.putAll(property);
        }
    }

    public Configuration(Properties... properties) {
        this.keyValues = new Properties();
        for (Properties property : properties) {
            this.keyValues.putAll(property);
        }
    }

    public Boolean getBoolean(String name, Boolean instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Boolean.valueOf(value);
    }

    public Boolean getBoolean(String name) {
        return getBoolean(name, null);
    }

    public Character getCharacter(String name, Character instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Character.valueOf(value.charAt(0));
    }

    public Character getCharacter(String name) {
        return getCharacter(name, null);
    }

    public Double getDouble(String name, Double instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Double.valueOf(value);
    }

    public Double getDouble(String name) {
        return getDouble(name, null);
    }

    public Float getFloat(String name, Float instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Float.valueOf(value);
    }

    public Float getFloat(String name) {
        return getFloat(name, null);
    }

    public Integer getInteger(String name, Integer instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Integer.valueOf(value);
    }

    public Integer getInteger(String name) {
        return getInteger(name, null);
    }

    public Long getLong(String name, Long instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : Long.valueOf(value);
    }

    public Long getLong(String name) {
        return getLong(name, null);
    }

    public String getString(String name, String instead) {
        String value = getString(name);
        return StringUtility.isBlank(value) ? instead : value;
    }

    public String getString(String name) {
        return keyValues.getProperty(name);
    }

}
