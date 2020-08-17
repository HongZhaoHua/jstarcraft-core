package com.jstarcraft.core.common.configuration;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.core.env.EnumerablePropertySource;

/**
 * Spring配置器
 * 
 * @author Birdy
 *
 */
public class SpringConfigurator implements ObjectProfile {

    /** 配置项 */
    private EnumerablePropertySource<?> keyValues;

    public SpringConfigurator(EnumerablePropertySource<?> keyValues) {
        this.keyValues = keyValues;
    }

    @Override
    public <T> T getObject(Class<T> clazz, String name) {
        return clazz.cast(keyValues.getProperty(name));
    }

    @Override
    public Collection<String> getKeys() {
        return Arrays.asList(keyValues.getPropertyNames());
    }

}
