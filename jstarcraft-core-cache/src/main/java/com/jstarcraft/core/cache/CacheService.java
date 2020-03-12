package com.jstarcraft.core.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.cache.annotation.CacheConfiguration.Unit;
import com.jstarcraft.core.cache.exception.CacheConfigurationException;
import com.jstarcraft.core.cache.exception.CacheException;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy;
import com.jstarcraft.core.cache.transience.TransienceStrategy;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.StorageAccessor;

/**
 * 缓存服务
 * 
 * @author Birdy
 */
@SuppressWarnings("rawtypes")
public class CacheService implements CacheMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    /** 访问器 */
    private final StorageAccessor accessor;

    /** 缓存类型信息 */
    private final Map<Class<?>, CacheInformation> cacheInformations = new HashMap<>();
    /** 内存策略映射 */
    private final Map<String, TransienceStrategy> transienceStrategies;
    /** 持久策略映射 */
    private final Map<String, PersistenceStrategy> persistenceStrategies;

    /** 实体缓存管理器 */
    private final Map<Class<? extends IdentityObject>, EntityCacheManager> entityManagers = new HashMap<>();
    /** 区域缓存管理器 */
    private final Map<Class<? extends IdentityObject>, RegionCacheManager> regionManagers = new HashMap<>();

    /** 状态 */
    private AtomicReference<CacheState> state = new AtomicReference<>(null);

    public CacheService(Set<Class<? extends IdentityObject>> cacheClasses, StorageAccessor accessor, Set<TransienceStrategy> transienceStrategies, Set<PersistenceStrategy> persistenceStrategies) {
        if (cacheClasses == null || accessor == null) {
            throw new IllegalArgumentException();
        }
        this.accessor = accessor;
        this.transienceStrategies = new HashMap<>();
        for (TransienceStrategy strategy : transienceStrategies) {
            this.transienceStrategies.put(strategy.getName(), strategy);
        }
        this.persistenceStrategies = new HashMap<>();
        for (PersistenceStrategy strategy : persistenceStrategies) {
            this.persistenceStrategies.put(strategy.getName(), strategy);
        }
        for (Class<? extends IdentityObject> cacheClass : cacheClasses) {
            if (!CacheInformation.checkCacheClass(cacheClass)) {
                throw new CacheConfigurationException("非法缓存类型[" + cacheClass.getName() + "]配置");
            }
            // 缓存信息
            CacheInformation information = CacheInformation.instanceOf(cacheClass);
            cacheInformations.put(cacheClass, information);
        }
    }

    /**
     * 启动缓存服务
     */
    public void start() {
        if (!state.compareAndSet(null, CacheState.STARTED)) {
            throw new CacheConfigurationException();
        }
        for (TransienceStrategy strategy : transienceStrategies.values()) {
            strategy.start();
        }
        for (PersistenceStrategy strategy : persistenceStrategies.values()) {
            strategy.start(accessor, cacheInformations);
        }
    }

    /**
     * 停止缓存服务
     */
    public void stop() {
        if (!state.compareAndSet(CacheState.STARTED, CacheState.STOPPED)) {
            throw new CacheConfigurationException();
        }
        for (TransienceStrategy strategy : transienceStrategies.values()) {
            strategy.stop();
        }
        for (PersistenceStrategy strategy : persistenceStrategies.values()) {
            strategy.stop();
        }
    }

    /**
     * 获取缓存服务状态
     */
    public CacheState getState() {
        return state.get();
    }

    /**
     * 获取指定的实体缓存管理器
     * 
     * @param cacheClass
     * @return
     */
    public EntityManager getEntityManager(Class<? extends IdentityObject> cacheClass) {
        CacheInformation information = cacheInformations.get(cacheClass);
        if (information == null) {
            throw new CacheException("[" + cacheClass.getName() + "]不是缓存类型");
        }
        if (information.getCacheUnit() != Unit.ENTITY) {
            throw new CacheException("[" + cacheClass.getName() + "]的缓存单位不是[" + Unit.ENTITY + "]");
        }
        EntityCacheManager manager = entityManagers.get(cacheClass);
        if (manager != null) {
            return manager;
        }
        if (entityManagers.containsKey(cacheClass)) {
            return entityManagers.get(cacheClass);
        }
        CacheConfiguration configuration = information.getCacheConfiguration();
        TransienceStrategy transienceStrategy = transienceStrategies.get(configuration.transienceStrategy());
        PersistenceStrategy persistenceStrategy = persistenceStrategies.get(configuration.persistenceStrategy());
        manager = new EntityCacheManager(information, transienceStrategy, persistenceStrategy);
        entityManagers.put(information.getCacheClass(), manager);
        return manager;
    }

    /**
     * 获取指定的区域缓存管理器
     * 
     * @param cacheClass
     * @return
     */
    public RegionManager getRegionManager(Class<? extends IdentityObject> cacheClass) {
        CacheInformation information = cacheInformations.get(cacheClass);
        if (information == null) {
            throw new CacheException("[" + cacheClass.getName() + "]不是缓存类型");
        }
        if (information.getCacheUnit() != Unit.REGION) {
            throw new CacheException("[" + cacheClass.getName() + "]的缓存单位不是[" + Unit.REGION + "]");
        }
        RegionCacheManager manager = regionManagers.get(cacheClass);
        if (manager != null) {
            return manager;
        }
        if (regionManagers.containsKey(cacheClass)) {
            return regionManagers.get(cacheClass);
        }
        CacheConfiguration configuration = information.getCacheConfiguration();
        TransienceStrategy transienceStrategy = transienceStrategies.get(configuration.transienceStrategy());
        PersistenceStrategy persistenceStrategy = persistenceStrategies.get(configuration.persistenceStrategy());
        manager = new RegionCacheManager(information, transienceStrategy, persistenceStrategy);
        regionManagers.put(information.getCacheClass(), manager);
        return manager;
    }

    /**
     * 获取访问器
     * 
     * @return
     */
    public StorageAccessor getAccessor() {
        return this.accessor;
    }

    @Override
    public Map<String, Integer> getInstanceCounts() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (Entry<Class<? extends IdentityObject>, EntityCacheManager> keyValue : entityManagers.entrySet()) {
            Class<? extends IdentityObject> key = keyValue.getKey();
            EntityCacheManager value = keyValue.getValue();
            result.put(key.getName(), value.getInstanceCount());
        }
        for (Entry<Class<? extends IdentityObject>, RegionCacheManager> keyValue : regionManagers.entrySet()) {
            Class<? extends IdentityObject> key = keyValue.getKey();
            RegionCacheManager value = keyValue.getValue();
            result.put(key.getName(), value.getInstanceCount());
        }
        return result;
    }

    @Override
    public Map<String, Map<String, Integer>> getIndexesCounts() {
        Map<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();
        for (Entry<Class<? extends IdentityObject>, EntityCacheManager> keyValue : entityManagers.entrySet()) {
            Class<? extends IdentityObject> key = keyValue.getKey();
            EntityCacheManager value = keyValue.getValue();
            result.put(key.getName(), value.getIndexesCount());
        }
        for (Entry<Class<? extends IdentityObject>, RegionCacheManager> keyValue : regionManagers.entrySet()) {
            Class<? extends IdentityObject> key = keyValue.getKey();
            RegionCacheManager value = keyValue.getValue();
            result.put(key.getName(), value.getIndexesCount());
        }
        return result;
    }
}
