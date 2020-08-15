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
public class SpringConfigurator implements Configurator {

    /** 配置项 */
    private EnumerablePropertySource<?> source;

    public SpringConfigurator(EnumerablePropertySource<?> source) {
        this.source = source;
    }

    @Override
    public String getString(String name) {
        return (String) source.getProperty(name);
    }

    @Override
    public Collection<String> getKeys() {
        return Arrays.asList(source.getPropertyNames());
    }

}
