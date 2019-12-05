package com.jstarcraft.core.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.exception.CacheException;
import com.jstarcraft.core.cache.exception.CacheIdentityException;
import com.jstarcraft.core.cache.persistence.PersistenceManager;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy;
import com.jstarcraft.core.cache.proxy.JavassistEntityProxy;
import com.jstarcraft.core.cache.proxy.ProxyManager;
import com.jstarcraft.core.cache.proxy.ProxyTransformer;
import com.jstarcraft.core.cache.transience.TransienceManager;
import com.jstarcraft.core.cache.transience.TransienceStrategy;
import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 实体缓存管理器
 * 
 * @author Birdy
 *
 * @param <K>
 * @param <T>
 */
public class EntityCacheManager<K extends Comparable<K> & Serializable, T extends IdentityObject<K>> implements EntityManager<K, T>, ProxyManager<K, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityCacheManager.class);

    /** 缓存类型 */
    private Class<T> cacheClass;
    /** 缓存配置信息 */
    private CacheInformation cacheInformation;
    /** 内存策略 */
    private TransienceStrategy transienceStrategy;
    /** 持久策略 */
    private PersistenceStrategy persistenceStrategy;
    /** 转换器 */
    private ProxyTransformer transformer;

    /** 内存 */
    private TransienceManager<K, T> transience;
    /** 持久 */
    private PersistenceManager<K, T> persistence;
    /**
     * 唯一索引(key:唯一键名称, value:{key:唯一键值, value:缓存对象标识})
     * 
     * <pre>
     * 配合{@link CacheInformation}的读写锁使用
     * </pre>
     */
    private Map<String, TransienceManager<Object, Collection<K>>> indexes;

    private Object[] hashLocks;
    /** 标识锁 */
    private Map<K, ReentrantLock> idLocks = new ConcurrentHashMap<K, ReentrantLock>();
    /** 索引锁 */
    private Map<CacheIndex, ReentrantLock> indexLocks = new ConcurrentHashMap<>();

    EntityCacheManager(final CacheInformation information, TransienceStrategy transienceStrategy, PersistenceStrategy persistenceStrategy) {
        this.cacheInformation = information;
        this.cacheClass = (Class<T>) information.getCacheClass();
        this.transienceStrategy = transienceStrategy;
        this.persistenceStrategy = persistenceStrategy;
        this.transformer = new JavassistEntityProxy(this, this.cacheInformation);
        this.transience = this.transienceStrategy.getTransienceManager(null);
        this.indexes = new ConcurrentHashMap<>();
        Collection<String> indexNames = information.getIndexNames();
        for (String name : indexNames) {
            TransienceManager manager = this.transienceStrategy.getTransienceManager(null);
            indexes.put(name, manager);
        }
        this.persistence = persistenceStrategy.getPersistenceManager(cacheClass);
    }

    private Collection<K> getIndexValueMap(CacheIndex index) {
        return indexes.get(index.getName()).retrieveInstance(index.getValue());
    }

    private Collection<K> loadIndexValueMap(CacheIndex index) {
        Collection<K> elements = indexes.get(index.getName()).retrieveInstance(index.getValue());
        if (elements == null) {
            elements = new HashSet<>();
            indexes.get(index.getName()).createInstance(index.getValue(), elements);
            Map<K, Object> identities = persistence.getIdentities(index.getName(), index.getValue());
            elements.addAll(identities.keySet());
        }
        return elements;
    }

    @Override
    public int getInstanceCount() {
        return transience.getSize();
    }

    @Override
    public Map<String, Integer> getIndexesCount() {
        Map<String, Integer> count = new HashMap<>();
        for (Entry<String, TransienceManager<Object, Collection<K>>> keyValue : indexes.entrySet()) {
            count.put(keyValue.getKey(), keyValue.getValue().getSize());
        }
        return count;
    }

    private ReentrantLock lockIdLock(K id) {
        // 此处可以考虑使用HashLock代替synchronized降低锁竞争
        synchronized (idLocks) {
            ReentrantLock lock = idLocks.get(id);
            if (lock == null) {
                lock = new ReentrantLock();
                ReentrantLock value = idLocks.putIfAbsent(id, lock);
                lock = value != null ? value : lock;
            }
            lock.lock();
            return lock;
        }
    }

    private void unlockIdLock(K id, ReentrantLock lock) {
        // 此处可以考虑使用HashLock代替synchronized降低锁竞争
        synchronized (idLocks) {
            lock.unlock();
            if (lock.getHoldCount() == 0) {
                idLocks.remove(id, lock);
            }
        }
    }

    private ReentrantLock lockIndexLock(CacheIndex index) {
        // 此处可以考虑使用HashLock代替synchronized降低锁竞争
        synchronized (indexLocks) {
            ReentrantLock lock = indexLocks.get(index);
            if (lock == null) {
                lock = new ReentrantLock();
                ReentrantLock value = indexLocks.putIfAbsent(index, lock);
                lock = value != null ? value : lock;
            }
            lock.lock();
            return lock;
        }
    }

    private void unlockIndexLock(CacheIndex index, ReentrantLock lock) {
        // 此处可以考虑使用HashLock代替synchronized降低锁竞争
        synchronized (indexLocks) {
            lock.unlock();
            if (lock.getHoldCount() == 0) {
                indexLocks.remove(index, lock);
            }
        }
    }

    @Override
    public T getInstance(K id) {
        ReentrantLock lock = lockIdLock(id);
        try {
            T object = transience.retrieveInstance(id);
            if (object != null) {
                return object;
            }
            if (persistence != null) {
                object = persistence.getInstance(id);
            }
            if (object != null) {
                object = transformer.transform(object);
                transience.createInstance(id, object);
            }
            return object;
        } finally {
            unlockIdLock(id, lock);
        }
    }

    @Override
    public T loadInstance(K id, CacheObjectFactory<K, T> factory) {
        ReentrantLock lock = lockIdLock(id);
        try {
            T object = transience.retrieveInstance(id);
            if (object != null) {
                return object;
            }
            if (persistence != null) {
                object = persistence.getInstance(id);
            }
            if (object == null) {
                object = factory.instanceOf(id);
                if (object == null) {
                    throw new CacheException();
                }
                if (object.getId() == null) {
                    throw new CacheIdentityException();
                }
                if (cacheInformation.hasIndexes()) {
                    // 使用indexLock与getIndexValuesMap更新缓存
                    Map<String, Comparable> values = cacheInformation.getIndexValues(object);
                    TreeSet<CacheIndex> indexes = new TreeSet<>();
                    for (Entry<String, Comparable> keyValue : values.entrySet()) {
                        indexes.add(new CacheIndex(keyValue.getKey(), keyValue.getValue()));
                    }
                    ArrayList<ReentrantLock> locks = new ArrayList<>(indexes.size());
                    try {
                        for (CacheIndex index : indexes) {
                            locks.add(lockIndexLock(index));
                        }
                        for (CacheIndex index : indexes) {
                            Collection<K> identities = getIndexValueMap(index);
                            if (identities != null) {
                                identities.add(id);
                            }
                        }
                    } finally {
                        int number = 0;
                        for (CacheIndex index : indexes) {
                            unlockIndexLock(index, locks.get(number++));
                        }
                    }
                }
                if (persistence != null) {
                    persistence.createInstance(object);
                }
            }
            object = transformer.transform(object);
            transience.createInstance(id, object);
            return object;
        } finally {
            unlockIdLock(id, lock);
        }
    }

    @Override
    public T deleteInstance(K id) {
        ReentrantLock lock = lockIdLock(id);
        try {
            T object = transience.deleteInstance(id);
            if (object != null) {
                if (cacheInformation.hasIndexes()) {
                    // 使用indexLock与getIndexValuesMap更新缓存
                    Map<String, Comparable> values = cacheInformation.getIndexValues(object);
                    TreeSet<CacheIndex> indexes = new TreeSet<>();
                    for (Entry<String, Comparable> keyValue : values.entrySet()) {
                        indexes.add(new CacheIndex(keyValue.getKey(), keyValue.getValue()));
                    }
                    ArrayList<ReentrantLock> locks = new ArrayList<>(indexes.size());
                    try {
                        for (CacheIndex index : indexes) {
                            locks.add(lockIndexLock(index));
                        }
                        for (CacheIndex index : indexes) {
                            Collection<K> identities = getIndexValueMap(index);
                            if (identities != null) {
                                identities.remove(id);
                            }
                        }
                    } finally {
                        int number = 0;
                        for (CacheIndex index : indexes) {
                            unlockIndexLock(index, locks.get(number++));
                        }
                    }
                }
            }
            if (persistence != null) {
                persistence.deleteInstance(id);
            }
            return object;
        } finally {
            unlockIdLock(id, lock);
        }
    }

    @Override
    public Collection<K> getIdentities(CacheIndex index) {
        // 使用indexLock与loadIndexValuesMap更新缓存
        ReentrantLock lock = lockIndexLock(index);
        try {
            Collection<K> identities = loadIndexValueMap(index);
            return identities;
        } finally {
            unlockIndexLock(index, lock);
        }
    }

    @Override
    public Collection<T> cacheInstances(Collection<T> instances) {
        // 考虑缓存冲突的情况
        Collection<T> caches = new ArrayList<>(instances.size());
        for (T instance : instances) {
            K id = instance.getId();
            ReentrantLock lock = lockIdLock(id);
            try {
                T object = transience.retrieveInstance(id);
                if (object == null) {
                    object = transformer.transform(instance);
                    transience.createInstance(id, object);
                }
                caches.add(object);
            } finally {
                unlockIdLock(id, lock);
            }
        }
        return caches;
    }

    @Override
    public void modifyInstance(T object) {
        persistence.updateInstance(object);
    }

}
