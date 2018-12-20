package com.jstarcraft.core.cache.schema;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import com.jstarcraft.core.cache.CacheObject;
import com.jstarcraft.core.cache.CacheService;
import com.jstarcraft.core.cache.CacheState;
import com.jstarcraft.core.cache.annotation.AfterCacheStarted;
import com.jstarcraft.core.cache.annotation.BeforeCacheStoped;
import com.jstarcraft.core.cache.persistence.PersistenceConfiguration;
import com.jstarcraft.core.cache.transience.TransienceConfiguration;
import com.jstarcraft.core.orm.OrmAccessor;
import com.jstarcraft.core.utility.ReflectionUtility;

/**
 * 缓存服务工厂
 * 
 * @author Birdy
 */
public class CacheServiceFactory implements FactoryBean<CacheService>, ApplicationListener<ApplicationEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheServiceFactory.class);

	public static final String CACHE_CLASSES_NAME = "cacheClasses";
	public static final String TRANSIENCE_CONFIGURATIONS_NAME = "transienceConfigurations";
	public static final String PERSISTENCE_CONFIGURATIONS_NAME = "persistenceConfigurations";

	@Autowired(required = true)
	private ApplicationContext applicationContext;

	private OrmAccessor accessor;
	private Set<Class<CacheObject>> cacheClasses;
	private Map<String, TransienceConfiguration> transienceConfigurations;
	private Map<String, PersistenceConfiguration> persistenceConfigurations;
	private CacheService cacheService;

	private void afterCacheServiceStarted() {
		String[] names = applicationContext.getBeanDefinitionNames();
		for (String name : names) {
			final Object instance = applicationContext.getBean(name);
			ReflectionUtility.doWithMethods(instance.getClass(), (method) -> {
				AfterCacheStarted annotation = method.getAnnotation(AfterCacheStarted.class);
				if (annotation == null) {
					return;
				}
				if (method.getParameterTypes().length > 0) {
					String message = String.format("对象[{}]@CacheServiceStarted方法参数数量不为 0", name);
					LOGGER.error(message);
					return;
				}
				try {
					method.setAccessible(true);
					method.invoke(instance);
				} catch (Exception exception) {
					String message = String.format("对象[{}]@CacheServiceStarted方法异常", name);
					LOGGER.error(message, exception);
					new RuntimeException(message, exception);
				}
			});
		}
	}

	private void beforeCacheServiceStoped() {
		String[] names = applicationContext.getBeanDefinitionNames();
		for (String name : names) {
			final Object instance = applicationContext.getBean(name);
			ReflectionUtility.doWithMethods(instance.getClass(), (method) -> {
				BeforeCacheStoped annotation = method.getAnnotation(BeforeCacheStoped.class);
				if (annotation == null) {
					return;
				}
				if (method.getParameterTypes().length > 0) {
					String message = String.format("对象[{}]@CacheServiceStoped方法参数数量不为 0", name);
					LOGGER.error(message);
					return;
				}
				try {
					method.setAccessible(true);
					method.invoke(instance);
				} catch (Exception exception) {
					String message = String.format("对象[{}]@CacheServiceStoped方法异常", name);
					LOGGER.error(message, exception);
					new RuntimeException(message, exception);
				}
			});
		}
	}

	@Override
	public synchronized void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			if (cacheService.getState() == null) {
				cacheService.start();
				afterCacheServiceStarted();
			}
			return;
		}

		if (event instanceof ContextClosedEvent) {
			if (cacheService.getState() == CacheState.STARTED) {
				beforeCacheServiceStoped();
				cacheService.stop();
			}
			return;
		}
	}

	public void setAccessor(OrmAccessor accessor) {
		this.accessor = accessor;
	}

	public void setCacheClasses(Set<Class<CacheObject>> cacheClasses) {
		this.cacheClasses = cacheClasses;
	}

	public void setTransienceConfigurations(Map<String, TransienceConfiguration> transienceConfigurations) {
		this.transienceConfigurations = transienceConfigurations;
	}

	public void setPersistenceConfigurations(Map<String, PersistenceConfiguration> persistenceConfigurations) {
		this.persistenceConfigurations = persistenceConfigurations;
	}

	@Override
	public synchronized CacheService getObject() throws Exception {
		if (cacheService == null) {
			cacheService = new CacheService(cacheClasses, accessor, transienceConfigurations, persistenceConfigurations);
		}
		return cacheService;

	}

	@Override
	public Class<?> getObjectType() {
		return CacheService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
