package com.jstarcraft.core.cache.transience;

import java.util.Map;

import com.jstarcraft.core.cache.transience.TransienceStrategy.TransienceType;

/**
 * 内存策略配置
 * 
 * @author Birdy
 *
 */
public class TransienceConfiguration {

    /** 策略名称 */
    private final String name;
    /** 策略类型 */
    private final TransienceType type;
    /** 策略参数 */
    private final Map<String, String> values;

    public TransienceConfiguration(String name, TransienceType type, Map<String, String> values) {
        this.name = name;
        this.type = type;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public TransienceType getType() {
        return type;
    }

    public String getValue(String name) {
        return values.get(name);
    }

}
