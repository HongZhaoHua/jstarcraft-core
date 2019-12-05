package com.jstarcraft.core.resource;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.resource.format.FormatAdapter;
import com.jstarcraft.core.resource.path.PathAdapter;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 资源仓储
 * 
 * @author Birdy
 */
public class ResourceStorage {

    private static final Logger logger = LoggerFactory.getLogger(ResourceStorage.class);

    /** 管理映射 */
    private final ConcurrentHashMap<Class<?>, ResourceManager<?, ?>> managers = new ConcurrentHashMap<>();

    private ResourceStorage() {
    }

    public static ResourceStorage instanceOf(Collection<Class<?>> definitions, FormatAdapter format, PathAdapter path) {
        ResourceStorage storage = new ResourceStorage();
        for (Class<?> clazz : definitions) {
            ResourceManager<?, ?> manager = new ResourceManager<>(clazz, format, path);
            storage.managers.put(clazz, manager);
        }
        for (Class<?> clazz : definitions) {
            storage.loadManager(clazz);
        }
        return storage;
    }

    public Collection<Class<?>> getDefinitions() {
        return managers.keySet();
    }

    /**
     * 装载指定类型的管理器
     * 
     * @param clazz
     */
    public void loadManager(Class<?> clazz) {
        ResourceManager<?, ?> manager = this.managers.get(clazz);
        try {
            manager.load();
        } catch (Exception exception) {
            String message = StringUtility.format("资源[{}]装载异常", clazz);
            logger.error(message);
            throw new StorageException(message, exception);
        }
    }

    /**
     * 获取指定类型的管理器
     * 
     * @param clazz
     * @return
     */
    public ResourceManager<?, ?> getManager(Class<?> clazz) {
        ResourceManager<?, ?> manager = this.managers.get(clazz);
        return manager;
    }

}
