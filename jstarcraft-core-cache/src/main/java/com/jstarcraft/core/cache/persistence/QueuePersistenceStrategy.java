package com.jstarcraft.core.cache.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.CacheInformation;
import com.jstarcraft.core.cache.CacheState;
import com.jstarcraft.core.cache.exception.CacheConfigurationException;
import com.jstarcraft.core.orm.OrmAccessor;

/**
 * 队列持久策略
 * 
 * @author Birdy
 *
 */
public class QueuePersistenceStrategy implements PersistenceStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueuePersistenceStrategy.class);

    /** 大小参数名称 */
    public static final String PARAMETER_SIZE = "size";

    /** 名称 */
    private String name;
    /** ORM访问器 */
    private OrmAccessor accessor;
    /** 缓存类型信息 */
    private Map<Class<?>, CacheInformation> informations;
    /** 状态 */
    private AtomicReference<CacheState> state = new AtomicReference<>(null);

    private Map<Class, QueuePersistenceManager> managers = new HashMap<>();

    private int size;

    /** 创建统计 */
    private final AtomicLong createdCount = new AtomicLong();
    /** 更新统计 */
    private final AtomicLong updatedCount = new AtomicLong();
    /** 删除统计 */
    private final AtomicLong deletedCount = new AtomicLong();
    /** 异常统计 */
    private final AtomicLong exceptionCount = new AtomicLong();

    @Override
    public synchronized void start(OrmAccessor accessor, Map<Class<?>, CacheInformation> informations, PersistenceConfiguration configuration) {
        if (!state.compareAndSet(null, CacheState.STARTED)) {
            throw new CacheConfigurationException();
        }
        this.name = configuration.getName();
        this.accessor = accessor;
        this.informations = informations;
        this.size = Integer.parseInt(configuration.getValue(PARAMETER_SIZE));
        for (Entry<Class<?>, CacheInformation> keyValue : informations.entrySet()) {
            Class clazz = keyValue.getKey();
            CacheInformation information = keyValue.getValue();
            QueuePersistenceManager manager = new QueuePersistenceManager<>(name, clazz, accessor, information, state, size);
            this.managers.put(clazz, manager);
            manager.setDaemon(true);
            manager.start();
        }
    }

    @Override
    public synchronized void stop() {
        if (!state.compareAndSet(CacheState.STARTED, CacheState.STOPPED)) {
            throw new CacheConfigurationException();
        }
        LOGGER.info("开始等待写队列[{}]清理", name);
        for (QueuePersistenceManager manager : this.managers.values()) {
            manager.interrupt();
            while (true) {
                if (!manager.isAlive()) {
                    break;
                }
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
        QueuePersistenceManager manager = managers.get(clazz);
        return manager;
    }

}
