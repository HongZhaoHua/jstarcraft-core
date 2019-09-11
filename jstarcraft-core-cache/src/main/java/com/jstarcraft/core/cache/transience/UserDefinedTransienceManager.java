package com.jstarcraft.core.cache.transience;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义瞬时策略
 * 
 * @author Birdy
 *
 */
public class UserDefinedTransienceManager<K, T> implements TransienceManager<K, T> {

    private ConcurrentHashMap<K, T> transience;

    UserDefinedTransienceManager(int capacity, float factor, int concurrencyLevel) {
        this.transience = new ConcurrentHashMap<>(capacity, factor, concurrencyLevel);
    }

    @Override
    public void createInstance(K id, T instance) {
        transience.put(id, instance);
    }

    @Override
    public T deleteInstance(K id) {
        return transience.remove(id);
    }

    @Override
    public T retrieveInstance(K id) {
        return transience.get(id);
    }

    @Override
    public int getSize() {
        return transience.size();
    }

}
