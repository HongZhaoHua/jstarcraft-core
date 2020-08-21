package com.jstarcraft.core.common.configuration;

import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 捆绑配置器
 * 
 * @author Birdy
 *
 */
public class BundleConfigurator implements StringConfigurator {

    /** 配置项 */
    private ResourceBundle keyValues;

    public BundleConfigurator(String key) {
        this.keyValues = ResourceBundle.getBundle(key);
    }

    public BundleConfigurator(String key, Locale locale) {
        this.keyValues = ResourceBundle.getBundle(key, locale);
    }

    public BundleConfigurator(ResourceBundle keyValues) {
        this.keyValues = keyValues;
    }

    @Override
    public String getString(String key) {
        return keyValues.getString(key);
    }

    @Override
    public Iterator<String> getKeys() {
        return keyValues.keySet().iterator();
    }

}
