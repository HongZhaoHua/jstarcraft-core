package com.jstarcraft.core.cache.transience;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap.Builder;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;

/**
 * 最近最少使用瞬时策略
 * 
 * @author Birdy
 *
 */
public class LeastRecentlyUsedTransienceManager<K, T> implements TransienceManager<K, T> {

    private ConcurrentLinkedHashMap<K, T> transience;

    LeastRecentlyUsedTransienceManager(int minimunSize, int maximunSize, int concurrencyLevel, TransienceMonitor monitor) {
        Builder<K, T> builder = new Builder<>();
        builder.initialCapacity(minimunSize);
        builder.maximumWeightedCapacity(maximunSize);
        builder.concurrencyLevel(concurrencyLevel);
        if (monitor != null) {
            builder.listener(new EvictionListener<K, T>() {

                public void onEviction(K key, T value) {
                    monitor.notifyExchanged(key, value);
                };

            });
        }
        this.transience = builder.build();
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
