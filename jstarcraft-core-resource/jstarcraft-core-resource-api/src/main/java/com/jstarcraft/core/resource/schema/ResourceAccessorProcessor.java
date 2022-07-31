package com.jstarcraft.core.resource.schema;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jstarcraft.core.common.conversion.ConversionUtility;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.resource.ResourceManager;
import com.jstarcraft.core.resource.ResourceMonitor;
import com.jstarcraft.core.resource.ResourceStorage;
import com.jstarcraft.core.resource.annotation.ResourceAccessor;
import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceId;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 仓储访问处理器,负责装配{@link ResourceAccessor}注解的资源
 * 
 * @author Birdy
 */
public class ResourceAccessorProcessor extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAccessorProcessor.class);

    private Map<String, ResourceStorage> storages;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        storages = applicationContext.getBeansOfType(ResourceStorage.class);
    }

    /**
     * 装配实例
     * 
     * @param object
     * @param field
     * @param annotation
     */
    private void assembleInstance(Object object, Field field, ResourceAccessor annotation) throws IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = annotation.clazz();
        if (clazz == Void.class || clazz == void.class) {
            if (StringUtility.isNotBlank(annotation.property())) {
                String message = StringUtility.format("仓储[{}]的属性[{}]不存在", clazz, annotation.property());
                logger.error(message);
                throw new StorageException(message);
            }
            clazz = field.getType();
        }
        ResourceConfiguration configuration = clazz.getAnnotation(ResourceConfiguration.class);
        if (configuration == null) {
            String message = StringUtility.format("仓储[{}]的配置不存在", clazz);
            logger.error(message);
            throw new StorageException(message);
        }

        // 获取仓储
        ResourceStorage storage = storages.get(annotation.storage());
        if (storage == null) {
            String message = StringUtility.format("仓储[{}]的实例不存在", annotation.storage());
            logger.error(message);
            throw new StorageException(message);
        }

        Object key = annotation.value();
        try {
            key = ConversionUtility.convert(annotation.value(), ReflectionUtility.uniqueField(clazz, ResourceId.class).getGenericType());
        } catch (Exception exception) {
            String message = StringUtility.format("仓储[{}]的主键转换异常", clazz);
            logger.error(message);
            throw new StorageException(message);
        }

        // 设置监听器
        ResourceManager manager = storage.getManager(clazz);
        ResourceMonitor monitor = new ResourceMonitor(annotation, object, field, clazz, key);
        manager.addObserver(monitor);

        // 触发改动
        monitor.update(manager, null);
    }

    /**
     * 装配仓库
     * 
     * @param object
     * @param field
     * @param annotation
     */
    private void assembleStorage(Object object, Field field, ResourceAccessor annotation) throws IllegalArgumentException, IllegalAccessException {
        // 获取仓储
        ResourceStorage storage = storages.get(annotation.storage());
        if (storage == null) {
            String message = StringUtility.format("仓储[{}]的实例不存在", annotation.storage());
            logger.error(message);
            throw new StorageException(message);
        }

        Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            String message = StringUtility.format("字段[{}]的类型非法,无法装配", field);
            logger.error(message);
            throw new StorageException(message);
        }

        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        Class<?> clazz;
        if (types[1] instanceof Class) {
            clazz = (Class<?>) types[1];
        } else if (types[1] instanceof ParameterizedType) {
            clazz = (Class<?>) ((ParameterizedType) types[1]).getRawType();
        } else {
            String message = StringUtility.format("字段[{}]的类型非法,无法装配", field);
            logger.error(message);
            throw new StorageException(message);
        }

        ResourceManager manager = storage.getManager(clazz);
        if (annotation.necessary() && manager == null) {
            String message = StringUtility.format("字段[{}]的类型非法,无法装配", field);
            logger.error(message);
            throw new StorageException(message);
        }
        ReflectionUtility.makeAccessible(field);
        field.set(object, manager);
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object object, String name) throws BeansException {
        ReflectionUtility.doWithFields(object.getClass(), (field) -> {
            ResourceAccessor annotation = field.getAnnotation(ResourceAccessor.class);
            if (annotation == null) {
                return;
            }
            if (ResourceManager.class.isAssignableFrom(field.getType())) {
                // 装配仓储
                assembleStorage(object, field, annotation);
            } else {
                // 装配实例
                assembleInstance(object, field, annotation);
            }
        });
        return super.postProcessAfterInstantiation(object, name);
    }

}
