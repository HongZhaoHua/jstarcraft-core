package com.jstarcraft.core.common.configuration;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 配置器
 * 
 * @author Birdy
 *
 */
public class Configurator {

    /** 配置项 */
    private TreeMap<String, String> keyValues;

    public Configurator(Map<String, String>... keyValues) {
        this.keyValues = new TreeMap<>();
        for (Map<String, String> property : keyValues) {
            this.keyValues.putAll(property);
        }
    }

    public Configurator(Properties... keyValues) {
        this.keyValues = new TreeMap<>();
        for (Map property : keyValues) {
            this.keyValues.putAll(property);
        }
    }

    public Class getClass(String name, Class instead) {
        String value = getString(name);
        try {
            return StringUtility.isBlank(value) ? instead : Class.forName(value);
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Class getClass(String name) {
        return getClass(name, null);
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
        return keyValues.get(name);
    }

    public Set<String> getKeys() {
        return keyValues.keySet();
    }

    public Set<String> getKeys(String from, String to) {
        return keyValues.subMap(from, to).keySet();
    }

}
