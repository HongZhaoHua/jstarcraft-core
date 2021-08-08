package com.jstarcraft.core.common.selection.jsonpath.hocon;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;

public class TypesafeConfigArray implements ConfigList {

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
    public boolean contains(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<ConfigValue> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean add(ConfigValue e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends ConfigValue> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends ConfigValue> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ConfigValue get(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigValue set(int index, ConfigValue element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void add(int index, ConfigValue element) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ConfigValue remove(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int indexOf(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ListIterator<ConfigValue> listIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListIterator<ConfigValue> listIterator(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ConfigValue> subList(int fromIndex, int toIndex) {
        // TODO Auto-generated method stub
        return null;
    }

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
    public ConfigValue withFallback(ConfigMergeable other) {
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
    public List<Object> unwrapped() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigList withOrigin(ConfigOrigin origin) {
        // TODO Auto-generated method stub
        return null;
    }

}
