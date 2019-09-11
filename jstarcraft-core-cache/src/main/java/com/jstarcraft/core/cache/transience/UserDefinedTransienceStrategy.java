package com.jstarcraft.core.cache.transience;

import java.util.concurrent.atomic.AtomicReference;

import com.jstarcraft.core.cache.CacheState;
import com.jstarcraft.core.cache.exception.CacheConfigurationException;

/**
 * 自定义瞬时策略
 * 
 * @author Birdy
 *
 */
public class UserDefinedTransienceStrategy implements TransienceStrategy {

    /** 参数:调整容量 */
    public static final String PARAMETER_CAPACITY = "capacity";
    /** 参数:调整因素 */
    private static final String PARAMETER_FACTOR = "factor";
    /** 参数:并发线程数预计值 */
    private static final String PARAMETER_CONCURRENCY_LEVEL = "concurrencyLevel";

    /** 名称 */
    private String name;
    /** 最小大小 */
    private int capacity;
    /** 最大大小 */
    private float factor;
    /** 并发线程数预计值 */
    private int concurrencyLevel;

    /** 状态 */
    private AtomicReference<CacheState> state = new AtomicReference<>(null);

    @Override
    public void start(TransienceConfiguration configuration) {
        if (!state.compareAndSet(null, CacheState.STARTED)) {
            throw new CacheConfigurationException();
        }
        this.name = configuration.getName();
        this.capacity = Integer.parseInt(configuration.getValue(PARAMETER_CAPACITY));
        this.factor = Float.parseFloat(configuration.getValue(PARAMETER_FACTOR));
        this.concurrencyLevel = Integer.parseInt(configuration.getValue(PARAMETER_CONCURRENCY_LEVEL));

        if (capacity <= 0 || factor <= 0 || concurrencyLevel <= 0) {
            throw new CacheConfigurationException();
        }
    }

    @Override
    public synchronized void stop() {
        if (!state.compareAndSet(CacheState.STARTED, CacheState.STOPPED)) {
            throw new CacheConfigurationException();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TransienceManager getTransienceManager(TransienceMonitor monitor) {
        return new UserDefinedTransienceManager<>(capacity, factor, concurrencyLevel);
    }

}
