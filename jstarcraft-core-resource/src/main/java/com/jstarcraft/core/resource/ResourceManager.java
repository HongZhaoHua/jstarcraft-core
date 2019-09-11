package com.jstarcraft.core.resource;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.resource.adapter.FormatAdapter;
import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.annotation.ResourceReference;
import com.jstarcraft.core.resource.definition.FormatDefinition;
import com.jstarcraft.core.resource.definition.ReferenceDefinition;
import com.jstarcraft.core.resource.definition.SpringReferenceDefinition;
import com.jstarcraft.core.resource.definition.StorageDefinition;
import com.jstarcraft.core.resource.definition.StorageReferenceDefinition;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 资源管理器
 * 
 * @author Birdy
 */
public class ResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    /** 定义映射 */
    private final ConcurrentHashMap<Class<?>, StorageDefinition> definitions = new ConcurrentHashMap<>();
    /** 仓储映射 */
    private final ConcurrentHashMap<Class<?>, KeyValue<ResourceStorage<?, ?>, Resource>> keyValues = new ConcurrentHashMap<>();

    private ResourceManager() {
    }

    /**
     * 
     * @param adapters
     * @param definitions
     * @param resourceLoader
     */
    public static ResourceManager instanceOf(Map<Class<?>, FormatDefinition> definitions, ConfigurableBeanFactory factory) {
        ResourceManager manager = new ResourceManager();
        ResourceLoader resourceLoader = (ApplicationContext) factory.getParentBeanFactory();
        for (Entry<Class<?>, FormatDefinition> keyValue : definitions.entrySet()) {
            Collection<ReferenceDefinition> references = new HashSet<>();
            ReflectionUtility.doWithFields(keyValue.getKey(), (field) -> {
                if (field.getAnnotation(ResourceReference.class) != null) {
                    Class type = field.getType();
                    if (type.isAnnotationPresent(ResourceConfiguration.class) || ResourceStorage.class.isAssignableFrom(type)) {
                        ReferenceDefinition definition = new StorageReferenceDefinition(field, manager);
                        references.add(definition);
                    } else {
                        ReferenceDefinition definition = new SpringReferenceDefinition(field, factory);
                        references.add(definition);
                    }
                }
            });
            // 替代仓储路径
            String path = factory.resolveEmbeddedValue(keyValue.getValue().getPath());
            StorageDefinition definition = new StorageDefinition(keyValue.getKey(), keyValue.getValue(), references, path);
            manager.definitions.put(definition.getClazz(), definition);
        }

        for (StorageDefinition definition : manager.definitions.values()) {
            FormatAdapter adapter = definition.getFormat().getAdapter();
            if (adapter == null) {
                String message = StringUtility.format("格式定义不存在[{}]", definition.getFormat().getAdapter());
                logger.error(message);
                throw new StorageException(message);
            }
            ResourceStorage<?, ?> storage = new ResourceStorage<>(definition, adapter);
            Resource resource = resourceLoader.getResource(definition.getPath());
            KeyValue<ResourceStorage<?, ?>, Resource> keyValue = new KeyValue<>(storage, resource);
            manager.keyValues.put(definition.getClazz(), keyValue);
        }

        for (StorageDefinition definition : manager.definitions.values()) {
            Collection<ReferenceDefinition> references = definition.getReferences();
            for (ReferenceDefinition reference : references) {
                if (reference instanceof StorageReferenceDefinition) {
                    StorageReferenceDefinition storageReference = StorageReferenceDefinition.class.cast(reference);
                    ResourceStorage storage = manager.getStorage(storageReference.getMonitorStorage());
                    storage.addObserver(storageReference);
                } else {
                    SpringReferenceDefinition springReference = SpringReferenceDefinition.class.cast(reference);
                    ResourceStorage storage = manager.getStorage(springReference.getMonitorStorage());
                    storage.addObserver(springReference);
                }
            }
        }

        for (StorageDefinition definition : manager.definitions.values()) {
            manager.loadStorage(definition.getClazz());
        }

        return manager;
    }

    /**
     * 获取资源定义集合
     */
    public Map<Class<?>, StorageDefinition> getDefinitions() {
        return new HashMap<>(definitions);
    }

    /**
     * 装载指定类型的仓储
     * 
     * @param clazz
     */
    public void loadStorage(Class<?> clazz) {
        KeyValue<ResourceStorage<?, ?>, Resource> keyValue = this.keyValues.get(clazz);
        ResourceStorage<?, ?> storage = keyValue.getKey();
        Resource resource = keyValue.getValue();
        try (InputStream stream = resource.getInputStream()) {
            storage.load(stream);
        } catch (Exception exception) {
            String message = StringUtility.format("仓储[{}]装载异常", clazz);
            logger.error(message);
            throw new StorageException(message, exception);
        }
    }

    /**
     * 根据指定类型获取对应的仓储
     * 
     * @param clazz
     * @return
     */
    public ResourceStorage<?, ?> getStorage(Class<?> clazz) {
        KeyValue<ResourceStorage<?, ?>, Resource> keyValue = this.keyValues.get(clazz);
        return keyValue.getKey();
    }

    /**
     * 根据指定类型获取对应的资源
     * 
     * @param clazz
     * @return
     */
    public Resource getResource(Class<?> clazz) {
        KeyValue<ResourceStorage<?, ?>, Resource> keyValue = this.keyValues.get(clazz);
        return keyValue.getValue();
    }

}
