package com.jstarcraft.core.common.configuration.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.stream.Collectors;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.jstarcraft.core.common.configuration.TypeConfigurator;

/**
 * NightConfig配置器
 * 
 * @author Birdy
 *
 */
public class NightConfigurator implements TypeConfigurator {

    private UnmodifiableConfig keyValues;

    public NightConfigurator(UnmodifiableConfig keyValues) {
        this.keyValues = keyValues;
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public Boolean getBoolean(String key, Boolean instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public Byte getByte(String key, Byte instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public Character getCharacter(String key, Character instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public Double getDouble(String key, Double instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public <T extends Enum<T>> T getEnumeration(Class<T> clazz, String key, T instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public Float getFloat(String key, Float instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public Integer getInteger(String key, Integer instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public Long getLong(String key, Long instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public Short getShort(String key, Short instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public String getString(String key, String instead) {
        return keyValues.getOrElse(key, instead);
    }

    @Override
    public Iterator<String> getKeys() {
        return keyValues.entrySet().stream().map((keyValue) -> keyValue.getKey()).collect(Collectors.toList()).iterator();
    }

}
