package com.jstarcraft.core.cache.transience;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.jstarcraft.core.cache.CacheState;
import com.jstarcraft.core.cache.exception.CacheConfigurationException;

/**
 * 自定义瞬时策略
 * 
 * @author Birdy
 *
 */
public class UserDefinedTransienceStrategy extends AbstractTransienceStrategy {

    /** 参数:调整容量 */
    public static final String PARAMETER_CAPACITY = "capacity";
    /** 参数:调整因素 */
    private static final String PARAMETER_FACTOR = "factor";
    /** 参数:并发线程数预计值 */
    private static final String PARAMETER_CONCURRENCY_LEVEL = "concurrencyLevel";

    /** 最小大小 */
    private int capacity;
    /** 最大大小 */
    private float factor;
    /** 并发线程数预计值 */
    private int concurrencyLevel;

    /** 状态 */
    private AtomicReference<CacheState> state = new AtomicReference<>(null);

    public UserDefinedTransienceStrategy(String name, Map<String, String> configuration) {
        super(name, configuration);
    }

    @Override
    public void start() {
        if (!state.compareAndSet(null, CacheState.STARTED)) {
            throw new CacheConfigurationException();
        }
        this.capacity = Integer.parseInt(configuration.get(PARAMETER_CAPACITY));
        this.factor = Float.parseFloat(configuration.get(PARAMETER_FACTOR));
        this.concurrencyLevel = Integer.parseInt(configuration.get(PARAMETER_CONCURRENCY_LEVEL));

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
    public TransienceManager getTransienceManager(TransienceMonitor monitor) {
        return new UserDefinedTransienceManager<>(capacity, factor, concurrencyLevel);
    }

}
