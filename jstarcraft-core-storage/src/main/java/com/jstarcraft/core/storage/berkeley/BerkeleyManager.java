package com.jstarcraft.core.storage.berkeley;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.storage.StorageIterator;
import com.jstarcraft.core.storage.StoragePagination;
import com.jstarcraft.core.storage.berkeley.exception.BerkeleyOperationException;
import com.jstarcraft.core.utility.ClassUtility;
import com.jstarcraft.core.utility.StringUtility;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityJoin;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.ForwardCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

/**
 * Berkeley管理器
 * 
 * @author Birdy
 *
 * @param <K>
 * @param <T>
 */
public class BerkeleyManager<K extends Comparable, T extends IdentityObject<K>> {

    private static final Logger logger = LoggerFactory.getLogger(BerkeleyManager.class);

    /** 元信息 */
    private BerkeleyMetadata metadata;

    /** 主键索引 */
    private PrimaryIndex primaryIndex;
    /** 次键索引集合 */
    private HashMap<String, SecondaryIndex> secondaryIndexes = new HashMap<>();
    /** 存储 */
    private EntityStore store;

    /**
     * 构造方法
     * 
     * @param entityInfo
     * @param accessor
     */
    public BerkeleyManager(BerkeleyMetadata metadata, EntityStore store) {
        this.metadata = metadata;
        this.store = store;

        // 通过获取主键索引准备Berkeley环境(有意义,别删除)
        primaryIndex = this.store.getPrimaryIndex(metadata.getPrimaryClass(), metadata.getStoreClass());
        for (String name : metadata.getIndexNames()) {
            Field field = metadata.getSecondaryField(name);
            Class<?> clazz = ClassUtility.primitiveToWrapper(field.getType());
            SecondaryIndex secondaryIndex;
            if (metadata.getOrmClass() == metadata.getStoreClass()) {
                secondaryIndex = store.getSecondaryIndex(primaryIndex, clazz, name);
            } else {
                secondaryIndex = store.getSubclassIndex(getPrimaryIndex(), metadata.getOrmClass(), clazz, name);
            }
            secondaryIndexes.put(name, secondaryIndex);
        }
    }

    public BerkeleyMetadata getMetadata() {
        return metadata;
    }

    PrimaryIndex getPrimaryIndex() {
        return primaryIndex;
    }

    SecondaryIndex getSecondaryIndex(String name) {
        return secondaryIndexes.get(name);
    }

    public boolean hasInstance(BerkeleyTransactor transactor, K id) {
        LockMode lockMode = transactor == null ? null : transactor.getIsolation().getLockMode();
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        return primaryIndex.contains(transaction, id, lockMode);
    }

    public boolean createInstance(BerkeleyTransactor transactor, T instance) {
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        if (primaryIndex.putNoOverwrite(transaction, instance)) {
            return true;
        } else {
            String message = StringUtility.format("创建的实例[{}:{}]已存在", metadata.getOrmName(), instance.getId());
            if (logger.isDebugEnabled()) {
                logger.debug(message);
            }
            return false;
        }
    }

    public boolean deleteInstance(BerkeleyTransactor transactor, K id) {
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        if (primaryIndex.delete(transaction, id)) {
            return true;
        } else {
            String message = StringUtility.format("删除的实例[{}:{}]不存在", metadata.getOrmName(), id);
            if (logger.isDebugEnabled()) {
                logger.debug(message);
            }
            return false;
        }
    }

    public boolean updateInstance(BerkeleyTransactor transactor, T instance) {
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        if (primaryIndex.put(transaction, instance) != null) {
            return true;
        } else {
            // TODO 删除保存的实例
            deleteInstance(transactor, instance.getId());
            String message = StringUtility.format("修改的实例[{}:{}]不存在", metadata.getOrmName(), instance.getId());
            if (logger.isDebugEnabled()) {
                logger.debug(message);
            }
            return false;
        }
    }

    public T getInstance(BerkeleyTransactor transactor, K id) {
        LockMode lockMode = transactor == null ? null : transactor.getIsolation().getLockMode();
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        return (T) primaryIndex.get(transaction, id, lockMode);
    }

    public K maximumIdentity(BerkeleyTransactor transactor, K from, K to) {
        CursorConfig cursorModel = transactor == null ? null : transactor.getIsolation().getCursorModel();
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        try (EntityCursor<K> cursor = primaryIndex.keys(transaction, from, true, to, false, cursorModel)) {
            return cursor.last();
        }
    }

    public K minimumIdentity(BerkeleyTransactor transactor, K from, K to) {
        CursorConfig cursorModel = transactor == null ? null : transactor.getIsolation().getCursorModel();
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        try (EntityCursor<K> cursor = primaryIndex.keys(transaction, from, true, to, false, cursorModel)) {
            return cursor.first();
        }
    }

    public <I> Map<K, I> queryIdentities(BerkeleyTransactor transactor, StorageCondition condition, String name, I... values) {
        SecondaryIndex secondaryIndex = secondaryIndexes.get(name);
        StoredSortedMap storeMap = (StoredSortedMap) secondaryIndex.keysIndex().sortedMap();
        HashMap<K, I> identities = new HashMap<>();
        switch (condition) {
        case All: {
            for (Object value : storeMap.keySet()) {
                for (Object identity : storeMap.duplicates(value)) {
                    identities.put((K) identity, (I) value);
                }
            }
            break;
        }
        case Between: {
            storeMap = (StoredSortedMap) storeMap.subMap(values[0], true, values[1], true);
            for (Object value : storeMap.keySet()) {
                for (Object identity : storeMap.duplicates(value)) {
                    identities.put((K) identity, (I) value);
                }
            }
            break;
        }
        case Equal: {
            I value = values[0];
            for (Object identity : storeMap.duplicates(value)) {
                identities.put((K) identity, value);
            }
            break;
        }
        case Higher: {
            storeMap = (StoredSortedMap) storeMap.subMap(values[0], false, null, true);
            for (Object value : storeMap.keySet()) {
                for (Object identity : storeMap.duplicates(value)) {
                    identities.put((K) identity, (I) value);
                }
            }
            break;
        }
        case In: {
            for (I value : values) {
                for (Object identity : storeMap.duplicates(value)) {
                    identities.put((K) identity, value);
                }
            }
            break;
        }
        case Lower: {
            storeMap = (StoredSortedMap) storeMap.subMap(null, true, values[0], false);
            for (Object value : storeMap.keySet()) {
                for (Object identity : storeMap.duplicates(value)) {
                    identities.put((K) identity, (I) value);
                }
            }
            break;
        }
        case Unequal: {
            for (Object value : storeMap.keySet()) {
                if (!values[0].equals(value)) {
                    for (Object identity : storeMap.duplicates(value)) {
                        identities.put((K) identity, (I) value);
                    }
                }
            }
            break;
        }
        }
        return identities;
    }

    public <I> List<T> queryInstances(BerkeleyTransactor transactor, StorageCondition condition, String name, I... values) {
        SecondaryIndex secondaryIndex = secondaryIndexes.get(name);
        StoredSortedMap storeMap = (StoredSortedMap) secondaryIndex.sortedMap();
        ArrayList<T> instances = new ArrayList<>();
        switch (condition) {
        case All: {
            for (Object value : storeMap.keySet()) {
                for (Object instance : storeMap.duplicates(value)) {
                    instances.add((T) instance);
                }
            }
            break;
        }
        case Between: {
            storeMap = (StoredSortedMap) storeMap.subMap(values[0], true, values[1], true);
            for (Object value : storeMap.keySet()) {
                for (Object instance : storeMap.duplicates(value)) {
                    instances.add((T) instance);
                }
            }
            break;
        }
        case Equal: {
            I value = values[0];
            for (Object instance : storeMap.duplicates(value)) {
                instances.add((T) instance);
            }
            break;
        }
        case Higher: {
            storeMap = (StoredSortedMap) storeMap.subMap(values[0], false, null, true);
            for (Object value : storeMap.keySet()) {
                for (Object instance : storeMap.duplicates(value)) {
                    instances.add((T) instance);
                }
            }
            break;
        }
        case In: {
            for (I value : values) {
                for (Object instance : storeMap.duplicates(value)) {
                    instances.add((T) instance);
                }
            }
            break;
        }
        case Lower: {
            storeMap = (StoredSortedMap) storeMap.subMap(null, true, values[0], false);
            for (Object value : storeMap.keySet()) {
                for (Object instance : storeMap.duplicates(value)) {
                    instances.add((T) instance);
                }
            }
            break;
        }
        case Unequal: {
            for (Object value : storeMap.keySet()) {
                if (!values[0].equals(value)) {
                    for (Object instance : storeMap.duplicates(value)) {
                        instances.add((T) instance);
                    }
                }
            }
            break;
        }
        }
        return instances;
    }

    private <E> long ignore(ForwardCursor<E> cursor, long size) {
        long count = 0;
        E element;
        while (count < size && (element = cursor.next()) != null) {
            count++;
        }
        return count;
    }

    private <E> void collect(Collection<E> collection, ForwardCursor<E> cursor, long size) {
        E element;
        while (size > 0 && (element = cursor.next()) != null) {
            collection.add(element);
            size--;
        }
    }

    private <E> long count(ForwardCursor<E> cursor) {
        long count = 0;
        E element;
        while ((element = cursor.next()) != null) {
            count++;
        }
        return count;
    }

    public List<T> queryInstances(BerkeleyTransactor transactor, StoragePagination pagination) {
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        ArrayList<T> instances = new ArrayList<>();
        long ignore = pagination.getFirst();
        long size = pagination.getSize();
        try (EntityCursor<T> cursor = primaryIndex.entities(transaction, transactor.getIsolation().getCursorModel())) {
            ignore -= ignore(cursor, ignore);
            if (ignore == 0) {
                collect(instances, cursor, size);
            }
            return instances;
        }
    }

    public List<T> queryIntersection(BerkeleyTransactor transactor, Map<String, Object> condition, StoragePagination pagination) {
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        EntityJoin<K, T> join = new EntityJoin<K, T>(primaryIndex);
        for (Entry<String, Object> keyValue : condition.entrySet()) {
            SecondaryIndex<Object, K, T> secondaryIndex = secondaryIndexes.get(keyValue.getKey());
            join.addCondition(secondaryIndex, keyValue.getValue());
        }
        ArrayList<T> instances = new ArrayList<>();
        long ignore = pagination.getFirst();
        long size = pagination.getSize();
        try (ForwardCursor<T> cursor = join.entities(transaction, transactor.getIsolation().getCursorModel())) {
            if (ignore == 0) {
                collect(instances, cursor, size);
            }
            return instances;
        }
    }

    public List<T> queryUnion(BerkeleyTransactor transactor, Map<String, Object> condition, StoragePagination pagination) {
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        long ignore = pagination.getFirst();
        long size = pagination.getSize();
        ArrayList<T> instances = new ArrayList<>();
        // TODO 应该考虑实例重复计算的情况.
        for (Entry<String, Object> keyValue : condition.entrySet()) {
            SecondaryIndex<Object, K, T> secondaryIndex = secondaryIndexes.get(keyValue.getKey());
            try (ForwardCursor<T> cursor = secondaryIndex.subIndex(keyValue.getValue()).entities(transaction, transactor.getIsolation().getCursorModel())) {
                if (ignore > 0) {
                    ignore -= ignore(cursor, ignore);
                }
                if (ignore == 0) {
                    if (size > instances.size()) {
                        collect(instances, cursor, size - instances.size());
                    }
                    if (size == instances.size()) {
                        break;
                    }
                }
            }
        }
        return instances;
    }

    public long countInstances(BerkeleyTransactor transactor) {
        return primaryIndex.count();
    }

    public long countIntersection(BerkeleyTransactor transactor, Map<String, Object> condition) {
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        EntityJoin<K, T> join = new EntityJoin<K, T>(primaryIndex);
        for (Entry<String, Object> keyValue : condition.entrySet()) {
            SecondaryIndex<Object, K, T> secondaryIndex = secondaryIndexes.get(keyValue.getKey());
            join.addCondition(secondaryIndex, keyValue.getValue());
        }
        try (ForwardCursor<K> cursor = join.keys(transaction, transactor.getIsolation().getCursorModel())) {
            return count(cursor);
        }
    }

    public long countUnion(BerkeleyTransactor transactor, Map<String, Object> condition) {
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        long count = 0;
        // TODO 应该考虑实例重复计算的情况.
        for (Entry<String, Object> keyValue : condition.entrySet()) {
            SecondaryIndex<Object, K, T> secondaryIndex = secondaryIndexes.get(keyValue.getKey());
            try (ForwardCursor<K> cursor = secondaryIndex.subIndex(keyValue.getValue()).keys(transaction, transactor.getIsolation().getCursorModel())) {
                count += count(cursor);
            }
        }
        return count;
    }

    public void iterateInstances(StorageIterator<T> iterator, BerkeleyTransactor transactor, StoragePagination pagination) {
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        long first = pagination.getFirst();
        long last = pagination.getLast();
        long count = 0;
        try (ForwardCursor<T> cursor = primaryIndex.entities(transaction, transactor.getIsolation().getCursorModel())) {
            T element;
            while ((element = cursor.next()) != null) {
                if (count >= first && count < last) {
                    iterator.iterate(element);
                }
                count++;
                if (count == last) {
                    break;
                }
            }
        }
    }

    public void iterateIntersection(StorageIterator<T> iterator, BerkeleyTransactor transactor, Map<String, Object> condition, StoragePagination pagination) {
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        EntityJoin<K, T> join = new EntityJoin<K, T>(primaryIndex);
        for (Entry<String, Object> keyValue : condition.entrySet()) {
            SecondaryIndex<Object, K, T> secondaryIndex = secondaryIndexes.get(keyValue.getKey());
            join.addCondition(secondaryIndex, keyValue.getValue());
        }
        long first = pagination.getFirst();
        long last = pagination.getLast();
        long count = 0;
        try (ForwardCursor<T> cursor = join.entities(transaction, transactor.getIsolation().getCursorModel())) {
            T element;
            while ((element = cursor.next()) != null) {
                if (count >= first && count < last) {
                    iterator.iterate(element);
                }
                count++;
                if (count == last) {
                    break;
                }
            }
        }
    }

    public void iterateUnion(StorageIterator<T> iterator, BerkeleyTransactor transactor, Map<String, Object> condition, StoragePagination pagination) {
        Transaction transaction = transactor == null ? null : transactor.getTransaction();
        long first = pagination.getFirst();
        long last = pagination.getLast();
        long count = 0;
        // TODO 应该考虑实例重复计算的情况.
        for (Entry<String, Object> keyValue : condition.entrySet()) {
            SecondaryIndex<Object, K, T> secondaryIndex = secondaryIndexes.get(keyValue.getKey());
            try (ForwardCursor<T> cursor = secondaryIndex.subIndex(keyValue.getValue()).entities(transaction, transactor.getIsolation().getCursorModel())) {
                T element;
                while ((element = cursor.next()) != null) {
                    if (count >= first && count < last) {
                        iterator.iterate(element);
                    }
                    count++;
                    if (count == last) {
                        break;
                    }
                }
            }
            if (count == last) {
                break;
            }
        }
    }

}