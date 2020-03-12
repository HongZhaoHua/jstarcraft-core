package com.jstarcraft.core.cache.persistence;

import java.util.Map;

public abstract class AbstractPersistenceStrategy implements PersistenceStrategy {

    /** 策略名称 */
    protected String name;

    /** 策略配置 */
    protected Map<String, String> configuration;

    protected AbstractPersistenceStrategy(String name, Map<String, String> configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return name;
    }

}
