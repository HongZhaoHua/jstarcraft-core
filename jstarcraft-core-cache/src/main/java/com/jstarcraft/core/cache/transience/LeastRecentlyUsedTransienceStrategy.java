package com.jstarcraft.core.cache.transience;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.jstarcraft.core.cache.CacheState;
import com.jstarcraft.core.cache.exception.CacheConfigurationException;

/**
 * 最近最少使用瞬时策略
 * 
 * @author Birdy
 *
 */
public class LeastRecentlyUsedTransienceStrategy extends AbstractTransienceStrategy {

    /** 参数:最小大小 */
    public static final String PARAMETER_MINIMUN_SIZE = "minimunSize";
    /** 参数:最大大小 */
    public static final String PARAMETER_MAXIMUN_SIZE = "maximunSize";
    /** 参数:并发线程数预计值 */
    public static final String PARAMETER_CONCURRENCY_LEVEL = "concurrencyLevel";

    /** 最小大小 */
    private int minimunSize;
    /** 最大大小 */
    private int maximunSize;
    /** 并发线程数预计值 */
    private int concurrencyLevel;

    /** 状态 */
    private AtomicReference<CacheState> state = new AtomicReference<>(null);

    public LeastRecentlyUsedTransienceStrategy(String name, Map<String, String> configuration) {
        super(name, configuration);
    }

    @Override
    public void start() {
        if (!state.compareAndSet(null, CacheState.STARTED)) {
            throw new CacheConfigurationException();
        }
        this.minimunSize = Integer.parseInt(configuration.get(PARAMETER_MINIMUN_SIZE));
        this.maximunSize = Integer.parseInt(configuration.get(PARAMETER_MAXIMUN_SIZE));
        this.concurrencyLevel = Integer.parseInt(configuration.get(PARAMETER_CONCURRENCY_LEVEL));

        if (minimunSize <= 0 || maximunSize <= 0 || concurrencyLevel <= 0) {
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
        return new LeastRecentlyUsedTransienceManager<>(minimunSize, maximunSize, concurrencyLevel, monitor);
    }

}
