package com.jstarcraft.core.common.option;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Properties配置器
 * 
 * @author Birdy
 *
 */
public class PropertyOption implements StringOption {

    /** 配置项 */
    private Map<String, String> keyValues;

    public PropertyOption(String properties) {
        Properties keyValues = new Properties();
        try (BufferedReader buffer = new BufferedReader(new StringReader(properties))) {
            keyValues.load(buffer);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        this.keyValues = (Map) keyValues;
    }

    @Override
    public String getString(String key) {
        return keyValues.get(key);
    }

    @Override
    public Iterator<String> getKeys() {
        return keyValues.keySet().iterator();
    }

}
