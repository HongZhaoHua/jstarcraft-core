package com.jstarcraft.core.common.selection.jsonpath.hocon;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;

public class TypesafeConfigObject implements ConfigObject {

    @Override
    public ConfigOrigin origin() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigValueType valueType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String render() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String render(ConfigRenderOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Config atPath(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Config atKey(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ConfigValue put(String key, ConfigValue value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigValue remove(Object key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends ConfigValue> m) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Set<String> keySet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<ConfigValue> values() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Entry<String, ConfigValue>> entrySet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Config toConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> unwrapped() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigObject withFallback(ConfigMergeable other) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigValue get(Object key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigObject withOnlyKey(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigObject withoutKey(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigObject withValue(String key, ConfigValue value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigObject withOrigin(ConfigOrigin origin) {
        // TODO Auto-generated method stub
        return null;
    }

}
