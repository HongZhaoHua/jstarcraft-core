package com.jstarcraft.core.cache.schema;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import com.jstarcraft.core.cache.CacheService;
import com.jstarcraft.core.cache.EntityManager;
import com.jstarcraft.core.cache.RegionManager;
import com.jstarcraft.core.cache.annotation.CacheAccessor;
import com.jstarcraft.core.cache.exception.CacheConfigurationException;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 缓存访问处理器,负责装配{@link CacheAccessor}注解的资源
 * 
 * @author Birdy
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CacheAccessorProcessor extends InstantiationAwareBeanPostProcessorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheAccessorProcessor.class);

    @Autowired
    private CacheService cacheService;

    private void assembleEntityManager(Object instance, String name, Field field) {
        Class<? extends IdentityObject> clazz = null;
        EntityManager manager = null;
        try {
            Type type = field.getGenericType();
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            clazz = (Class<? extends IdentityObject>) types[1];
            manager = cacheService.getEntityManager(clazz);
        } catch (Exception exception) {
            String message = StringUtility.format("无法装配Bean[{}]的属性[{}]", name, field.getName());
            LOGGER.error(message);
            throw new CacheConfigurationException(message, exception);
        }
        if (manager == null) {
            String message = StringUtility.format("无法装配Bean[{}]的属性[{}]", name, field.getName());
            LOGGER.error(message);
            throw new CacheConfigurationException(message);
        }
        assembleField(instance, field, manager);
    }

    private void assembleRegionManager(Object instance, String name, Field field) {
        Class<? extends IdentityObject> clazz = null;
        RegionManager manager = null;
        try {
            Type type = field.getGenericType();
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            clazz = (Class<? extends IdentityObject>) types[1];
            manager = cacheService.getRegionManager(clazz);
        } catch (Exception exception) {
            String message = StringUtility.format("无法装配Bean[{}]的属性[{}]", name, field.getName());
            LOGGER.error(message);
            throw new CacheConfigurationException(message, exception);
        }
        if (manager == null) {
            String message = StringUtility.format("无法装配Bean[{}]的属性[{}]", name, field.getName());
            LOGGER.error(message);
            throw new CacheConfigurationException(message);
        }
        assembleField(instance, field, manager);
    }

    private void assembleField(Object instance, Field field, Object value) {
        ReflectionUtility.makeAccessible(field);
        try {
            field.set(instance, value);
        } catch (Exception exception) {
            String message = StringUtility.format("无法装配属性[{}]", field);
            LOGGER.error(message);
            throw new CacheConfigurationException(message);
        }
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object instance, final String name) throws BeansException {
        ReflectionUtility.doWithFields(instance.getClass(), (field) -> {
            CacheAccessor annotation = field.getAnnotation(CacheAccessor.class);
            if (annotation == null) {
                return;
            }
            if (field.getType().equals(EntityManager.class)) {
                // 注入实体单位缓存服务
                assembleEntityManager(instance, name, field);
            } else if (field.getType().equals(RegionManager.class)) {
                // 注入区域单位缓存服务
                assembleRegionManager(instance, name, field);
            } else {
                String message = StringUtility.format("无法装配Bean[{}]的属性[{}]", name, field.getName());
                LOGGER.error(message);
                throw new CacheConfigurationException(message);
            }
        });
        return super.postProcessAfterInstantiation(instance, name);
    }

}
