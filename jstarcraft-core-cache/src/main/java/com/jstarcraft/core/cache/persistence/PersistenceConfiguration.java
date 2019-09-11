package com.jstarcraft.core.cache.persistence;

import java.util.Map;

import com.jstarcraft.core.cache.persistence.PersistenceStrategy.PersistenceType;

/**
 * 持久策略配置
 * 
 * @author Birdy
 *
 */
public class PersistenceConfiguration {

    /** 策略名称 */
    private final String name;
    /** 策略类型 */
    private final PersistenceType type;
    /** 策略参数 */
    private final Map<String, String> values;

    public PersistenceConfiguration(String name, PersistenceType type, Map<String, String> values) {
        this.name = name;
        this.type = type;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public PersistenceType getType() {
        return type;
    }

    public String getValue(String key) {
        return values.get(key);
    }

}
