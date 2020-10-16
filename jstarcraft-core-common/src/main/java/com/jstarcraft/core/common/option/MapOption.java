package com.jstarcraft.core.common.option;

import java.util.Iterator;
import java.util.Map;

/**
 * 映射配置器
 * 
 * @author Birdy
 *
 */
public class MapOption implements StringOption {

    /** 配置项 */
    private Map<String, String> keyValues;

    public MapOption(Map keyValues) {
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
