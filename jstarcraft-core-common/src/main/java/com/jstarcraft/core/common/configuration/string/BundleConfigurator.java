package com.jstarcraft.core.common.configuration.string;

import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import com.jstarcraft.core.common.configuration.StringConfigurator;

/**
 * 捆绑配置器
 * 
 * @author Birdy
 *
 */
public class BundleConfigurator implements StringConfigurator {

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
    public String getString(String key) {
        return keyValues.getString(key);
    }

    @Override
    public Iterator<String> getKeys() {
        return keyValues.keySet().iterator();
    }

}
