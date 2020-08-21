package com.jstarcraft.core.common.configuration;

import java.util.Arrays;
import java.util.Iterator;

import org.springframework.core.env.EnumerablePropertySource;

/**
 * Spring配置器
 * 
 * @author Birdy
 *
 */
public class SpringConfigurator implements ObjectConfigurator {

    /** 配置项 */
    private EnumerablePropertySource<?> keyValues;

    public SpringConfigurator(EnumerablePropertySource<?> keyValues) {
        this.keyValues = keyValues;
    }

    @Override
    public <T> T getObject(Class<T> clazz, String key) {
        return clazz.cast(keyValues.getProperty(key));
    }

    @Override
    public Iterator<String> getKeys() {
        return Arrays.asList(keyValues.getPropertyNames()).iterator();
    }

}
