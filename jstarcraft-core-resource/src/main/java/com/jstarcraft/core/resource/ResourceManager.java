package com.jstarcraft.core.resource;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.resource.annotation.ResourceConfiguration;
import com.jstarcraft.core.resource.definition.PropertyAccessor;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.resource.format.FormatAdapter;
import com.jstarcraft.core.resource.path.PathAdapter;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 资源管理
 * 
 * @author Birdy
 */
public class ResourceManager<K, V> extends Observable {

    private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    private final Class<?> clazz;

    private final String address;
    /** 格式适配器 */
    private final FormatAdapter formatAdapter;
    /** 路径适配器 */
    private final PathAdapter pathAdapter;

    /** 主键空间 */
    private Map<K, V> instances = new LinkedHashMap<>();
    /** 单值索引空间 */
    private Map<String, Map<Object, V>> singles = new HashMap<String, Map<Object, V>>();
    /** 多值索引空间 */
    private Map<String, Map<Object, List<V>>> multiples = new HashMap<String, Map<Object, List<V>>>();
    /** 标识获取器 */
    private final PropertyAccessor idAccessor;
    /** 索引获取器集合 */
    private final Map<String, PropertyAccessor> indexAccessors;
    /** 读取锁 */
    private final ReadLock readLock;
    /** 写入锁 */
    private final WriteLock writeLock;
    /** 状态 */
    private final AtomicBoolean state = new AtomicBoolean(false);

    public ResourceManager(Class<?> clazz, FormatAdapter formatAdapter, PathAdapter pathAdapter) {
        // 获取资源信息
        this.clazz = clazz;
        ResourceConfiguration annotation = clazz.getAnnotation(ResourceConfiguration.class);
        StringBuilder buffer = new StringBuilder();
        buffer.append(File.separator);
        buffer.append(annotation.prefix());
        buffer.append(clazz.getSimpleName());
        buffer.append(annotation.suffix());
        this.address = buffer.toString();
        this.formatAdapter = formatAdapter;
        this.pathAdapter = pathAdapter;
        this.idAccessor = PropertyAccessor.getIdAccessor(clazz);
        this.indexAccessors = PropertyAccessor.getIndexAccessors(clazz);
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    private void checkState() {
        // 保证监控者可以访问
        if (!state.get() && !writeLock.isHeldByCurrentThread()) {
            String message = StringUtility.format("仓储状态异常");
            logger.error(message);
            throw new StorageException(message);
        }
    }

    /**
     * 根据指定的主键获取对应的实例
     * 
     * @param key
     * @param isThrow
     * @return
     */
    public V getInstance(K key, boolean isThrow) {
        try {
            readLock.lock();
            checkState();
            V value = instances.get(key);
            if (isThrow && value == null) {
                String message = StringUtility.format("仓储[{}]指定主键[{}]的实例不存在", clazz, key);
                logger.error(message);
                throw new StorageException(message);
            }
            return value;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 指定的主键是否存在
     * 
     * @param key
     * @return
     */
    public boolean hasKey(K key) {
        try {
            readLock.lock();
            checkState();
            return instances.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 获取所有的实例
     * 
     * @return
     */
    public Collection<V> getAll() {
        try {
            readLock.lock();
            checkState();
            return Collections.unmodifiableCollection(instances.values());
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 根据指定的值获取对应的单值索引
     * 
     * @param name
     * @param value
     * @return
     */
    public V getSingle(String name, Object value) {
        try {
            readLock.lock();
            checkState();
            Map<Object, V> indexes = singles.get(name);
            if (indexes == null) {
                return null;
            }
            return indexes.get(value);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 根据指定的值获取对应的多值索引
     * 
     * @param name
     * @param value
     * @return
     */
    public List<V> getMultiple(String name, Object value) {
        try {
            readLock.lock();
            checkState();
            Map<Object, List<V>> map = multiples.get(name);
            if (map == null) {
                return Collections.EMPTY_LIST;
            }
            List<V> list = map.get(value);
            if (list == null) {
                return Collections.EMPTY_LIST;
            }
            ArrayList<V> indexes = new ArrayList<V>(list);
            return indexes;
        } finally {
            readLock.unlock();
        }
    }

    private void load(V instance) {
        // 标识处理
        K key = (K) idAccessor.getValue(instance);
        if (key == null) {
            String message = StringUtility.format("仓储[{}]的实例[{}]主键不存在", clazz, instance);
            logger.error(message);
            throw new StorageException(message);
        }
        if (instances.containsKey(key)) {
            String message = StringUtility.format("仓储[{}]的实例[{}]主键存在冲突", clazz, instance);
            logger.error(message);
            throw new StorageException(message);
        }
        instances.put(key, instance);

        // 索引处理
        for (PropertyAccessor indexAccessor : indexAccessors.values()) {
            String name = indexAccessor.getName();
            Object index = indexAccessor.getValue(instance);
            // 索引内容存储
            if (indexAccessor.isUnique()) {
                Map<Object, V> map = getSingleMap(name);
                if (map.put(index, instance) != null) {
                    String message = StringUtility.format("仓储[{}]的实例[{}]索引[{}]存在冲突", clazz, instance, index);
                    logger.error(message);
                    throw new StorageException(message);
                }
            } else {
                List<V> list = getMultipleList(name, index);
                list.add(instance);
            }
        }
    }

    private Map<Object, V> getSingleMap(String name) {
        Map<Object, V> map = singles.get(name);
        if (map == null) {
            map = new HashMap<>();
            singles.put(name, map);
        }
        return map;
    }

    private List<V> getMultipleList(String name, Object index) {
        Map<Object, List<V>> multiple = multiples.get(name);
        if (multiple == null) {
            multiple = new HashMap<>();
            multiples.put(name, multiple);
        }
        List<V> list = multiple.get(index);
        if (list == null) {
            list = new LinkedList<>();
            multiple.put(index, list);
        }
        return list;
    }

    void load() {
        try {
            writeLock.lock();
            state.set(false);
            LinkedList<V> objects = new LinkedList<>();
            InputStream stream = pathAdapter.getStream(address);
            Iterator<V> iterator = formatAdapter.iterator((Class) clazz, stream);
            while (iterator.hasNext()) {
                V object = iterator.next();
                objects.add(object);
            }

            // 防止由于IO异常仓储彻底失效.
            instances.clear();
            singles.clear();
            multiples.clear();

            // 对标识排序
            Comparator comparator = idAccessor.getComparator();
            if (comparator != null) {
                Collections.sort(objects, comparator);
            }
            for (V object : objects) {
                load(object);
            }

            // 对单值索引排序
            for (Entry<String, Map<Object, V>> keyValue : singles.entrySet()) {
                String key = keyValue.getKey();
                PropertyAccessor indexAccessor = indexAccessors.get(key);
                comparator = indexAccessor.getComparator();
                if (comparator != null) {
                    List<V> values = new ArrayList<>(keyValue.getValue().values());
                    Collections.sort(values, comparator);
                    Map<Object, V> map = new LinkedHashMap<>();
                    for (V value : values) {
                        Object index = indexAccessor.getValue(value);
                        map.put(index, value);
                    }
                    keyValue.setValue(map);
                }
            }
            // 对多值索引排序
            for (Entry<String, Map<Object, List<V>>> keyValue : multiples.entrySet()) {
                String key = keyValue.getKey();
                PropertyAccessor indexAccessor = indexAccessors.get(key);
                comparator = indexAccessor.getComparator();
                if (comparator != null) {
                    for (List<V> values : keyValue.getValue().values()) {
                        Collections.sort(values, comparator);
                    }
                }
            }

            // 通知监控器与设置引用
            this.setChanged();
            this.notifyObservers();
        } catch (Exception exception) {
            String message = StringUtility.format("仓储[{}]装载异常", clazz);
            logger.error(message);
            throw new StorageException(message, exception);
        } finally {
            state.set(true);
            writeLock.unlock();
        }
    }

}
