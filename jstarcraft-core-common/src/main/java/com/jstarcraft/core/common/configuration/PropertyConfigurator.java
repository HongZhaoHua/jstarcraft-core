package com.jstarcraft.core.common.configuration;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * Properties配置器
 * 
 * @author Birdy
 *
 */
public class PropertyConfigurator implements StringProfile {

    /** 配置项 */
    private Map<String, String> keyValues;

    public PropertyConfigurator(String properties) {
        Properties keyValues = new Properties();
        try (BufferedReader buffer = new BufferedReader(new StringReader(properties))) {
            keyValues.load(buffer);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        this.keyValues = (Map) keyValues;
    }

    @Override
    public String getString(String name) {
        return keyValues.get(name);
    }

    @Override
    public Collection<String> getKeys() {
        return keyValues.keySet();
    }

}
