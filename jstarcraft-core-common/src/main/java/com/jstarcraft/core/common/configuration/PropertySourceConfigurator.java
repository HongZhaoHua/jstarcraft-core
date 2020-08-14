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
public class PropertySourceConfigurator implements Configurator {

    /** 配置项 */
    private EnumerablePropertySource<?> propertySource;

    public PropertySourceConfigurator(EnumerablePropertySource<?> propertySource) {
        this.propertySource = propertySource;
    }

    @Override
    public String getString(String name) {
        return (String) propertySource.getProperty(name);
    }

    @Override
    public Collection<String> getKeys() {
        return Arrays.asList(propertySource.getPropertyNames());
    }

}
