package com.jstarcraft.core.common.configuration;

import java.util.Collection;
import java.util.Map;

/**
 * 映射配置器
 * 
 * @author Birdy
 *
 */
public class MapConfigurator implements StringProfile {

    /** 配置项 */
    private Map<String, String> keyValues;
    

    public MapConfigurator(Map keyValues) {
        this.keyValues = keyValues;
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
