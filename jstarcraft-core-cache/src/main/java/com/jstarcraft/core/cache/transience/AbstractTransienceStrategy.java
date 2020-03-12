package com.jstarcraft.core.cache.transience;

import java.util.Map;

public abstract class AbstractTransienceStrategy implements TransienceStrategy {

    /** 策略名称 */
    protected String name;

    /** 策略配置 */
    protected Map<String, String> configuration;

    protected AbstractTransienceStrategy(String name, Map<String, String> configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return name;
    }

}
