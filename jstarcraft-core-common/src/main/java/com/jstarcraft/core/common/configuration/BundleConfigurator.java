package com.jstarcraft.core.common.configuration;

import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 捆绑配置器
 * 
 * @author Birdy
 *
 */
public class BundleConfigurator implements StringProfile {

    /** 配置项 */
    private ResourceBundle keyValues;

    public BundleConfigurator(String name) {
        this.keyValues = ResourceBundle.getBundle(name);
    }

    public BundleConfigurator(String name, Locale locale) {
        this.keyValues = ResourceBundle.getBundle(name, locale);
    }

    public BundleConfigurator(ResourceBundle keyValues) {
        this.keyValues = keyValues;
    }

    @Override
    public String getString(String name) {
        return keyValues.getString(name);
    }

    @Override
    public Collection<String> getKeys() {
        return keyValues.keySet();
    }

}
