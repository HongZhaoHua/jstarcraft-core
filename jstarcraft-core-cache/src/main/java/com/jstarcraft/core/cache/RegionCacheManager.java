package com.jstarcraft.core.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.exception.CacheException;
import com.jstarcraft.core.cache.exception.CacheIdentityException;
import com.jstarcraft.core.cache.persistence.PersistenceManager;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy;
import com.jstarcraft.core.cache.proxy.JavassistRegionProxy;
import com.jstarcraft.core.cache.proxy.ProxyManager;
import com.jstarcraft.core.cache.proxy.ProxyTransformer;
import com.jstarcraft.core.cache.transience.TransienceElement;
import com.jstarcraft.core.cache.transience.TransienceManager;
import com.jstarcraft.core.cache.transience.TransienceStrategy;
import com.jstarcraft.core.cache.transience.WeakElementManager;
import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 区域缓存管理器
 * 
 * @author Birdy
 *
 * @param <K>
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class RegionCacheManager<K extends Comparable<K> & Serializable, T extends IdentityObject<K>> implements RegionManager<K, T>, ProxyManager<K, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegionCacheManager.class);

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
    private WeakElementManager<K, T> transience;
    /** 持久 */
    private PersistenceManager<K, T> persistence;
    /** 区域索引 */
    private Map<String, TransienceManager<Object, Map<K, TransienceElement>>> indexes;

    /** 标识锁 */
    private Map<K, ReentrantLock> idLocks = new ConcurrentHashMap<>();
    /** 索引锁 */
    private Map<CacheIndex, ReentrantLock> indexLocks = new ConcurrentHashMap<>();

    RegionCacheManager(CacheInformation information, TransienceStrategy transienceStrategy, PersistenceStrategy persistenceStrategy) {
        this.cacheInformation = information;
        this.cacheClass = (Class<T>) information.getCacheClass();
        this.transienceStrategy = transienceStrategy;
        this.persistenceStrategy = persistenceStrategy;
        this.transformer = new JavassistRegionProxy(this, this.cacheInformation);
        this.transience = new WeakElementManager<>(transformer);
        this.indexes = new ConcurrentHashMap<>(information.getIndexNames().size());
        for (String name : information.getIndexNames()) {
            TransienceManager manager = this.transienceStrategy.getTransienceManager(null);
            this.indexes.put(name, manager);
        }
        this.persistence = persistenceStrategy.getPersistenceManager(cacheClass);
    }

    private Map<K, TransienceElement> getIndexValueMap(CacheIndex index) {
        return indexes.get(index.getName()).retrieveInstance(index.getValue());
    }

    private Map<K, TransienceElement> loadIndexValueMap(CacheIndex index) {
        Map<K, TransienceElement> elements = indexes.get(index.getName()).retrieveInstance(index.getValue());
        if (elements == null) {
            elements = new HashMap<>();
            indexes.get(index.getName()).createInstance(index.getValue(), elements);
            List<T> objects = persistence.getInstances(index.getName(), index.getValue());
            for (T object : objects) {
                TransienceElement element = transience.putElement(object);
                elements.put(object.getId(), element);
            }
        }
        return elements;
    }

    @Override
    public int getInstanceCount() {
        return transience.getCount();
    }

    @Override
    public Map<String, Integer> getIndexesCount() {
        Map<String, Integer> count = new HashMap<>();
        for (Entry<String, TransienceManager<Object, Map<K, TransienceElement>>> keyValue : indexes.entrySet()) {
            int size = 0;
            size += keyValue.getValue().getSize();
            count.put(keyValue.getKey(), size);
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

    private Collection<T> elementsToObjects(Collection<TransienceElement> elements) {
        return elements.stream().map((element) -> {
            return (T) element.getCacheObject();
        }).collect(Collectors.toSet());
    }

    @Override
    public Collection<T> getInstances(CacheIndex index) {
        if (!cacheInformation.hasIndex(index.getName())) {
            throw new CacheException();
        }
        // 使用indexLock与loadIndexValuesMap更新缓存
        ReentrantLock lock = lockIndexLock(index);
        try {
            Map<K, TransienceElement> elements = loadIndexValueMap(index);
            return Collections.unmodifiableCollection(elementsToObjects(elements.values()));
        } finally {
            unlockIndexLock(index, lock);
        }
    }

    @Override
    public T getInstance(CacheIndex index, K id) {
        if (!cacheInformation.hasIndex(index.getName())) {
            throw new CacheException();
        }
        // 使用indexLock与loadIndexValuesMap更新缓存
        ReentrantLock lock = lockIndexLock(index);
        try {
            Map<K, TransienceElement> elements = loadIndexValueMap(index);
            TransienceElement element = elements.get(id);
            if (element != null) {
                return (T) element.getCacheObject();
            } else {
                return null;
            }
        } finally {
            unlockIndexLock(index, lock);
        }
    }

    @Override
    public T loadInstance(CacheIndex index, K id, CacheObjectFactory<K, T> factory) {
        if (!cacheInformation.hasIndex(index.getName())) {
            throw new CacheException();
        }
        // 使用indexLock与loadIndexValuesMap更新缓存
        ReentrantLock lock = lockIndexLock(index);
        try {
            Map<K, TransienceElement> elements = loadIndexValueMap(index);
            TransienceElement element = elements.get(id);
            if (element != null) {
                return (T) element.getCacheObject();
            }
        } finally {
            unlockIndexLock(index, lock);
        }

        lock = lockIdLock(id);
        try {
            T object = factory.instanceOf(id);
            TransienceElement element = transience.getElement(object);
            if (element != null) {
                return (T) element.getCacheObject();
            }
            return createInstance(object);
        } finally {
            unlockIdLock(id, lock);
        }
    }

    @Override
    public T createInstance(T object) {
        if (object.getId() == null) {
            throw new CacheIdentityException();
        }
        K id = object.getId();
        ReentrantLock idLock = lockIdLock(id);
        try {
            if (transience.getElement(object) != null) {
                throw new CacheIdentityException();
            }
            if (persistence.getInstance(id) != null) {
                throw new CacheIdentityException();
            }
            persistence.createInstance(object);
            TransienceElement element = transience.putElement(object);
            // 使用indexLock与loadIndexValuesMap更新缓存
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
                    Map<K, TransienceElement> elements = loadIndexValueMap(index);
                    elements.put(id, element);
                }
            } finally {
                int number = 0;
                for (CacheIndex index : indexes) {
                    unlockIndexLock(index, locks.get(number++));
                }
            }
            return (T) element.getCacheObject();
        } finally {
            unlockIdLock(id, idLock);
        }
    }

    @Override
    public void deleteInstance(T object) {
        K id = object.getId();
        ReentrantLock idLock = lockIdLock(id);
        try {
            persistence.deleteInstance(id);
            // 使用indexLock与loadIndexValuesMap更新缓存
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
                    Map<K, TransienceElement> elements = loadIndexValueMap(index);
                    elements.remove(id);
                }
            } finally {
                int number = 0;
                for (CacheIndex index : indexes) {
                    unlockIndexLock(index, locks.get(number++));
                }
            }
            transience.takeElement(object);
        } finally {
            unlockIdLock(id, idLock);
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
                // 使用indexLock与loadIndexValuesMap更新缓存
                Map<String, Comparable> values = cacheInformation.getIndexValues(instance);
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
                        Map<K, TransienceElement> elements = loadIndexValueMap(index);
                    }
                } finally {
                    int number = 0;
                    for (CacheIndex index : indexes) {
                        unlockIndexLock(index, locks.get(number++));
                    }
                }
                TransienceElement element = transience.putElement(instance);
                caches.add((T) element.getCacheObject());
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
