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
import com.jstarcraft.core.cache.persistence.PersistenceConfiguration;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy;
import com.jstarcraft.core.cache.persistence.PromptPersistenceStrategy;
import com.jstarcraft.core.cache.persistence.QueuePersistenceStrategy;
import com.jstarcraft.core.cache.persistence.SchedulePersistenceStrategy;
import com.jstarcraft.core.cache.transience.DelayedTransienceStrategy;
import com.jstarcraft.core.cache.transience.LeastRecentlyUsedTransienceStrategy;
import com.jstarcraft.core.cache.transience.TransienceConfiguration;
import com.jstarcraft.core.cache.transience.TransienceStrategy;
import com.jstarcraft.core.cache.transience.UserDefinedTransienceStrategy;
import com.jstarcraft.core.orm.OrmAccessor;
import com.jstarcraft.core.utility.IdentityObject;

/**
 * 缓存服务
 * 
 * @author Birdy
 */
@SuppressWarnings("rawtypes")
public class CacheService implements CacheMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

	/** 访问器 */
	private final OrmAccessor accessor;

	/** 缓存类型信息 */
	private final Map<Class<?>, CacheInformation> cacheInformations = new HashMap<>();
	/** 内存配置 */
	private final Map<String, TransienceConfiguration> transienceConfigurations;
	/** 内存策略映射 */
	private final Map<String, TransienceStrategy> transienceStrategies = new HashMap<>();
	/** 持久配置 */
	private final Map<String, PersistenceConfiguration> persistenceConfigurations;
	/** 持久策略映射 */
	private final Map<String, PersistenceStrategy> persistenceStrategies = new HashMap<>();

	/** 实体缓存管理器 */
	private final Map<Class<? extends IdentityObject>, EntityCacheManager> entityManagers = new HashMap<>();
	/** 区域缓存管理器 */
	private final Map<Class<? extends IdentityObject>, RegionCacheManager> regionManagers = new HashMap<>();

	/** 状态 */
	private AtomicReference<CacheState> state = new AtomicReference<>(null);

	public CacheService(Set<Class<IdentityObject>> cacheClasses, OrmAccessor accessor, Map<String, TransienceConfiguration> transienceConfigurations, Map<String, PersistenceConfiguration> persistenceConfigurations) {
		if (cacheClasses == null || accessor == null) {
			throw new IllegalArgumentException();
		}
		this.accessor = accessor;
		this.transienceConfigurations = new HashMap<>(transienceConfigurations);
		this.persistenceConfigurations = new HashMap<>(persistenceConfigurations);
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
	}

	/**
	 * 停止缓存服务
	 */
	public void stop() {
		if (!state.compareAndSet(CacheState.STARTED, CacheState.STOPPED)) {
			throw new CacheConfigurationException();
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
		TransienceStrategy transienceStrategy = getTransienceStrategy(configuration.transienceStrategy());
		PersistenceStrategy persistenceStrategy = getPersistenceStrategy(configuration.persistenceStrategy());
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
		TransienceStrategy transienceStrategy = getTransienceStrategy(configuration.transienceStrategy());
		PersistenceStrategy persistenceStrategy = getPersistenceStrategy(configuration.persistenceStrategy());
		manager = new RegionCacheManager(information, transienceStrategy, persistenceStrategy);
		regionManagers.put(information.getCacheClass(), manager);
		return manager;
	}

	/**
	 * 获取访问器
	 * 
	 * @return
	 */
	public OrmAccessor getAccessor() {
		return this.accessor;
	}

	/** 获取持久化处理器实例 */
	private TransienceStrategy getTransienceStrategy(String name) {
		TransienceStrategy result = transienceStrategies.get(name);
		if (result != null) {
			return result;
		}
		if (!transienceConfigurations.containsKey(name)) {
			throw new CacheConfigurationException("没有内存策略[" + name + "]的配置");
		}
		TransienceConfiguration configuration = transienceConfigurations.get(name);
		switch (configuration.getType()) {
		case DELAYED:
			result = new DelayedTransienceStrategy();
			break;
		case LEAST_RECENTLY_USED:
			result = new LeastRecentlyUsedTransienceStrategy();
			break;
		case USER_DEFINED:
			result = new UserDefinedTransienceStrategy();
			break;
		}
		result.start(configuration);
		transienceStrategies.put(name, result);
		return result;
	}

	/** 获取持久化处理器实例 */
	private PersistenceStrategy getPersistenceStrategy(String name) {
		PersistenceStrategy strategy = persistenceStrategies.get(name);
		if (strategy != null) {
			return strategy;
		}
		if (!persistenceConfigurations.containsKey(name)) {
			throw new CacheConfigurationException("没有持久策略[" + name + "]的配置");
		}
		PersistenceConfiguration configuration = persistenceConfigurations.get(name);
		switch (configuration.getType()) {
		case PROMPT:
			strategy = new PromptPersistenceStrategy();
			break;
		case QUEUE:
			strategy = new QueuePersistenceStrategy();
			break;
		case SCHEDULE:
			strategy = new SchedulePersistenceStrategy();
			break;
		}
		strategy.start(accessor, cacheInformations, configuration);
		persistenceStrategies.put(name, strategy);
		return strategy;
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
