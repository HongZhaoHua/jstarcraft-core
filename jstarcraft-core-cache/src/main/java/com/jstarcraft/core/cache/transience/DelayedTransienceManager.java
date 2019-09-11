package com.jstarcraft.core.cache.transience;

/**
 * 定时瞬时策略
 * 
 * @author Birdy
 *
 */
public class DelayedTransienceManager<K, T> implements TransienceManager<K, T> {

    private DelayedHashMap<K, T> transience;

    DelayedTransienceManager(int expire, int segment, TransienceMonitor monitor) {
        this.transience = DelayedHashMap.instanceOf(expire, segment, monitor);
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
