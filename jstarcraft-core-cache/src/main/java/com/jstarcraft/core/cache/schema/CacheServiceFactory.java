package com.jstarcraft.core.cache.schema;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.jstarcraft.core.cache.CacheService;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy;
import com.jstarcraft.core.cache.transience.TransienceStrategy;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.StorageAccessor;

/**
 * 缓存服务工厂
 * 
 * @author Birdy
 */
public class CacheServiceFactory implements FactoryBean<CacheService>, InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheServiceFactory.class);

    public static final String CACHE_CLASSES_NAME = "cacheClasses";
    public static final String TRANSIENCE_STRATEGIES_NAME = "transienceStrategies";
    public static final String PERSISTENCE_STRATEGIES_NAME = "persistenceStrategies";

    private StorageAccessor accessor;
    private Set<Class<? extends IdentityObject>> cacheClasses;
    private Set<TransienceStrategy> transienceStrategies;
    private Set<PersistenceStrategy> persistenceStrategies;
    private CacheService cacheService;

    public void setAccessor(StorageAccessor accessor) {
        this.accessor = accessor;
    }

    public void setCacheClasses(Set<Class<? extends IdentityObject>> cacheClasses) {
        this.cacheClasses = cacheClasses;
    }

    public void setTransienceStrategies(Set<TransienceStrategy> transienceStrategies) {
        this.transienceStrategies = transienceStrategies;
    }

    public void setPersistenceStrategies(Set<PersistenceStrategy> persistenceStrategies) {
        this.persistenceStrategies = persistenceStrategies;
    }

    @Override
    public synchronized CacheService getObject() throws Exception {
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

    @Override
    public void afterPropertiesSet() throws Exception {
        if (cacheService == null) {
            cacheService = new CacheService(cacheClasses, accessor, transienceStrategies, persistenceStrategies);
        }
        cacheService.start();
    }

    @Override
    public void destroy() throws Exception {
        cacheService.stop();
    }

}
