package com.jstarcraft.core.cache.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.CacheInformation;
import com.jstarcraft.core.cache.CacheState;
import com.jstarcraft.core.cache.exception.CacheConfigurationException;
import com.jstarcraft.core.orm.OrmAccessor;

/**
 * 定时持久策略
 * 
 * @author Birdy
 *
 */
public class SchedulePersistenceStrategy implements PersistenceStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulePersistenceStrategy.class);

    /** CRON表达式 */
    public static final String PARAMETER_CRON = "cron";
    /** 名称 */
    private String name;
    /** ORM访问器 */
    private OrmAccessor accessor;
    /** 缓存类型信息 */
    private Map<Class<?>, CacheInformation> informations;
    /** 状态 */
    private AtomicReference<CacheState> state = new AtomicReference<>(null);

    private Map<Class, SchedulePersistenceManager> managers = new HashMap<>();
    /** CRON表达式 */
    private String cron;

    /** 处理大小 */
    private final AtomicInteger waitSize = new AtomicInteger();
    /** 创建统计 */
    private final AtomicLong createdCount = new AtomicLong();
    /** 更新统计 */
    private final AtomicLong updatedCount = new AtomicLong();
    /** 删除统计 */
    private final AtomicLong deletedCount = new AtomicLong();
    /** 异常统计 */
    private final AtomicInteger exceptionCount = new AtomicInteger();

    @Override
    public synchronized void start(OrmAccessor accessor, Map<Class<?>, CacheInformation> informations, PersistenceConfiguration configuration) {
        if (!state.compareAndSet(null, CacheState.STARTED)) {
            throw new CacheConfigurationException();
        }
        this.name = configuration.getName();
        this.accessor = accessor;
        this.informations = informations;
        this.cron = configuration.getValue(PARAMETER_CRON);
        for (Entry<Class<?>, CacheInformation> keyValue : informations.entrySet()) {
            Class clazz = keyValue.getKey();
            CacheInformation information = keyValue.getValue();
            SchedulePersistenceManager manager = new SchedulePersistenceManager<>(name, clazz, accessor, information, state, cron);
            this.managers.put(clazz, manager);
            manager.setDaemon(true);
            manager.start();
        }
    }

    @Override
    public void stop() {
        if (!state.compareAndSet(CacheState.STARTED, CacheState.STOPPED)) {
            throw new CacheConfigurationException();
        }
        LOGGER.info("开始等待写队列[{}]清理", name);
        for (SchedulePersistenceManager manager : this.managers.values()) {
            manager.interrupt();
            while (true) {
                if (!manager.isAlive()) {
                    break;
                }
            }
            while (true) {
                if (manager.getWaitSize() == 0) {
                    break;
                }
                Thread.yield();
            }
        }
        this.managers.clear();
        LOGGER.info("结束等待写队列[{}]清理", name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public synchronized PersistenceManager getPersistenceManager(Class clazz) {
        SchedulePersistenceManager manager = managers.get(clazz);
        return manager;
    }

}
