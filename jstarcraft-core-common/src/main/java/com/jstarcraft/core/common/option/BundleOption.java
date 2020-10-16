package com.jstarcraft.core.common.option;

import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 捆绑配置器
 * 
 * @author Birdy
 *
 */
public class BundleOption implements StringOption {

    /** 配置项 */
    private ResourceBundle keyValues;

    public BundleOption(String name) {
        this.keyValues = ResourceBundle.getBundle(name);
    }

    public BundleOption(String name, Locale locale) {
        this.keyValues = ResourceBundle.getBundle(name, locale);
    }

    public BundleOption(ResourceBundle keyValues) {
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
