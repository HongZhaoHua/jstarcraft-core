package com.jstarcraft.core.common.configuration;

import java.util.Map;
import java.util.Set;

/**
 * 映射配置器
 * 
 * @author Birdy
 *
 */
public class MapConfigurator implements Configurator {

    /** 配置项 */
    private Map<String, String> keyValues;
    

    public MapConfigurator(Map keyValues) {
        this.keyValues = keyValues;
    }

    public String getString(String name) {
        return keyValues.get(name);
    }

    public Set<String> getKeys() {
        return keyValues.keySet();
    }

}
