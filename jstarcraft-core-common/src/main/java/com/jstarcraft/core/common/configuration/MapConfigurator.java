package com.jstarcraft.core.common.configuration;

import java.util.Iterator;
import java.util.Map;

/**
 * 映射配置器
 * 
 * @author Birdy
 *
 */
public class MapConfigurator implements StringConfigurator {

    /** 配置项 */
    private Map<String, String> keyValues;

    public MapConfigurator(Map keyValues) {
        this.keyValues = keyValues;
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
