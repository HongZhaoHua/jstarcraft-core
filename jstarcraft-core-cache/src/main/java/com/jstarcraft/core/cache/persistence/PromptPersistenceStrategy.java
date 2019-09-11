package com.jstarcraft.core.cache.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.CacheInformation;
import com.jstarcraft.core.cache.CacheState;
import com.jstarcraft.core.cache.exception.CacheConfigurationException;
import com.jstarcraft.core.orm.OrmAccessor;

/**
 * 立即持久策略
 * 
 * @author Birdy
 *
 */
public class PromptPersistenceStrategy implements PersistenceStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromptPersistenceStrategy.class);

    /** 名称 */
    private String name;
    /** ORM访问器 */
    private OrmAccessor accessor;
    /** 缓存类型信息 */
    private Map<Class<?>, CacheInformation> informations;
    /** 状态 */
    private AtomicReference<CacheState> state = new AtomicReference<>(null);

    private Map<Class, PromptPersistenceManager> managers = new HashMap<>();

    @Override
    public synchronized void start(OrmAccessor accessor, Map<Class<?>, CacheInformation> informations, PersistenceConfiguration configuration) {
        if (!state.compareAndSet(null, CacheState.STARTED)) {
            throw new CacheConfigurationException();
        }
        this.name = configuration.getName();
        this.accessor = accessor;
        this.informations = informations;
        for (Entry<Class<?>, CacheInformation> keyValue : informations.entrySet()) {
            Class clazz = keyValue.getKey();
            CacheInformation information = keyValue.getValue();
            PromptPersistenceManager manager = new PromptPersistenceManager<>(name, clazz, accessor, information, state);
            this.managers.put(clazz, manager);
        }
    }

    @Override
    public synchronized void stop() {
        if (!state.compareAndSet(CacheState.STARTED, CacheState.STOPPED)) {
            throw new CacheConfigurationException();
        }
        this.managers.clear();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public synchronized PersistenceManager getPersistenceManager(Class clazz) {
        PromptPersistenceManager manager = managers.get(clazz);
        return manager;
    }

}
