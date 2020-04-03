package com.jstarcraft.core.common.configuration;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 捆绑配置器
 * 
 * @author Birdy
 *
 */
public class BundleConfigurator implements Configurator {

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

    public String getString(String name) {
        return keyValues.getString(name);
    }

    public Set<String> getKeys() {
        return keyValues.keySet();
    }

}
