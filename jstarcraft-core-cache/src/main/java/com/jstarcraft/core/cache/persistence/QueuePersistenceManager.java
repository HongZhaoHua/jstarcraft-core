package com.jstarcraft.core.cache.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.CacheInformation;
import com.jstarcraft.core.cache.CacheState;
import com.jstarcraft.core.cache.exception.CacheException;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy.PersistenceOperation;
import com.jstarcraft.core.cache.proxy.ProxyObject;
import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.storage.StorageAccessor;
import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 队列持久策略
 * 
 * @author Birdy
 *
 */
public class QueuePersistenceManager<K extends Comparable, T extends IdentityObject<K>> extends Thread implements PersistenceManager<K, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueuePersistenceManager.class);

    /** 名称 */
    private String name;
    /** 类型 */
    private Class cacheClass;
    
    protected ThreadLocal<T> copyInstances = new ThreadLocal<T>() {

        @Override
        protected T initialValue() {
            T instance = (T) information.getCacheInstance();
            return instance;
        }

    };

    /** 更新队列 */
    private BlockingQueue<PersistenceElement> elementQueue;

    /** 此读写锁用于配合elementMap,保证在查询过程中不存在增删改 */
    private ReentrantReadWriteLock waitForLock = new ReentrantReadWriteLock();
    /** 等待的缓存元素实例 */
    private ConcurrentHashMap<Object, PersistenceElement> elements = new ConcurrentHashMap<>();

    /** ORM访问器 */
    private StorageAccessor accessor;
    /** 缓存类型信息 */
    private CacheInformation information;
    /** 状态 */
    private AtomicReference<CacheState> state = new AtomicReference<>(null);
    /** 监听器 */
    private PersistenceMonitor monitor;
    /** 创建统计 */
    private final AtomicLong createdCount = new AtomicLong();
    /** 更新统计 */
    private final AtomicLong updatedCount = new AtomicLong();
    /** 删除统计 */
    private final AtomicLong deletedCount = new AtomicLong();
    /** 异常统计 */
    private final AtomicLong exceptionCount = new AtomicLong();

    QueuePersistenceManager(String name, Class cacheClass, StorageAccessor accessor, CacheInformation information, AtomicReference<CacheState> state, int size) {
        this.name = name;
        this.cacheClass = cacheClass;
        this.accessor = accessor;
        this.information = information;
        this.state = state;
        if (size > 0) {
            elementQueue = new ArrayBlockingQueue<PersistenceElement>(size);
        } else {
            elementQueue = new LinkedBlockingQueue<PersistenceElement>();
        }
    }

    @Override
    public T getInstance(K cacheId) {
        Lock readLock = waitForLock.readLock();
        try {
            readLock.lock();
            PersistenceElement element = elements.get(cacheId);
            if (element != null) {
                if (element.getOperation().equals(PersistenceOperation.DELETE)) {
                    return null;
                } else {
                    return (T) element.getCacheObject();
                }
            }
            T value = (T) accessor.getInstance(cacheClass, cacheId);
            return value;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Map<K, Object> getIdentities(String indexName, Comparable indexValue) {
        Lock readLock = waitForLock.readLock();
        try {
            readLock.lock();
            Map<K, Object> values = accessor.queryIdentities(cacheClass, StorageCondition.Equal, indexName, indexValue);
            for (PersistenceElement element : elements.values()) {
                if (element.getOperation().equals(PersistenceOperation.CREATE)) {
                    Object value = information.getIndexValue(element.getCacheObject(), indexName);
                    if (indexValue.equals(value)) {
                        values.put((K) element.getCacheId(), value);
                    }
                }
                if (element.getOperation().equals(PersistenceOperation.UPDATE)) {
                    Object value = information.getIndexValue(element.getCacheObject(), indexName);
                    if (indexValue.equals(value)) {
                        values.put((K) element.getCacheId(), value);
                    }
                }
                if (element.getOperation().equals(PersistenceOperation.DELETE)) {
                    values.remove(element.getCacheId());
                }
            }
            return values;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<T> getInstances(String indexName, Comparable indexValue) {
        Lock readLock = waitForLock.readLock();
        try {
            readLock.lock();
            List<T> values = accessor.queryInstances(cacheClass, StorageCondition.Equal, indexName, indexValue);

            Map<K, T> instances = new HashMap<>();
            for (T value : values) {
                instances.put(value.getId(), value);
            }

            for (PersistenceElement element : elements.values()) {
                if (element.getOperation().equals(PersistenceOperation.CREATE)) {
                    Object value = information.getIndexValue(element.getCacheObject(), indexName);
                    if (indexValue.equals(value)) {
                        instances.put((K) element.getCacheId(), (T) element.getCacheObject());
                    }
                }
                if (element.getOperation().equals(PersistenceOperation.UPDATE)) {
                    Object value = information.getIndexValue(element.getCacheObject(), indexName);
                    if (indexValue.equals(value)) {
                        instances.put((K) element.getCacheId(), (T) element.getCacheObject());
                    }
                }
                if (element.getOperation().equals(PersistenceOperation.DELETE)) {
                    instances.remove(element.getCacheId());
                }
            }
            return new ArrayList<>(instances.values());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public PersistenceElement createInstance(IdentityObject<?> cacheObject) {
//		if (cacheObject instanceof ProxyObject) {
//			cacheObject = ((ProxyObject) cacheObject).getInstance();
//		}
        PersistenceElement element = new PersistenceElement(PersistenceOperation.CREATE, cacheObject.getId(), cacheObject);
        persist(element);
        return element;
    }

    @Override
    public PersistenceElement deleteInstance(Comparable cacheId) {
        PersistenceElement element = new PersistenceElement(PersistenceOperation.DELETE, cacheId, null);
        persist(element);
        return element;
    }

    @Override
    public PersistenceElement updateInstance(IdentityObject<?> cacheObject) {
//		if (cacheObject instanceof ProxyObject) {
//			cacheObject = ((ProxyObject) cacheObject).getInstance();
//		}
        PersistenceElement element = new PersistenceElement(PersistenceOperation.UPDATE, cacheObject.getId(), cacheObject);
        persist(element);
        return element;
    }

    @Override
    public void setMonitor(PersistenceMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public PersistenceMonitor getMonitor() {
        return monitor;
    }

    @Override
    public int getWaitSize() {
        int size = elementQueue.size();
        return size;
    }

    @Override
    public long getCreatedCount() {
        return createdCount.get();
    }

    @Override
    public long getUpdatedCount() {
        return updatedCount.get();
    }

    @Override
    public long getDeletedCount() {
        return deletedCount.get();
    }

    @Override
    public long getExceptionCount() {
        return exceptionCount.get();
    }

    private void persist(PersistenceElement element) {
        if (element == null) {
            return;
        }
        if (!state.get().equals(CacheState.STARTED)) {
            String message = StringUtility.format("持久策略[{}]已经停止,拒绝接收元素[{}]", name, element);
            LOGGER.error(message);
            throw new CacheException(message);
        }
        Object cacheId = element.getCacheId();
        Lock writeLock = waitForLock.writeLock();
        try {
            writeLock.lock();
            PersistenceElement current = elements.get(cacheId);
            if (current == null) {
                current = element;
                elements.put(cacheId, current);
                elementQueue.put(current);
            } else {
                current.modify(element);
                if (current.isIgnore()) {
                    // 忽略只清理elementMap,不清理elementQueue
                    elements.remove(cacheId);
                }
            }
        } catch (InterruptedException exception) {
            LOGGER.error("不应该出现的情况,等待将元素[{}]放到队列时被中断", new Object[] { element, exception });
            // TODO 应该记录日志,防止丢失数据
            // if (element.getOperation().equals(PersistenceOperation.UPDATE)) {
            // ConcurrentHashSet<Object> waitForUpdate = getWaitForUpdate(cacheClass);
            // waitForUpdate.remove(cacheId);
            // }
            // if (element.getOperation().equals(PersistenceOperation.DELETE)) {
            // ReentrantReadWriteLock waitForLock = getLock(cacheClass);
            // Lock writeLock = waitForLock.writeLock();
            // try {
            // ConcurrentHashSet<Object> waitForDelete = getWaitForDelete(cacheClass);
            // waitForDelete.remove(cacheId);
            // } finally {
            // writeLock.unlock();
            // }
            // }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            PersistenceElement element = null;
            Object cacheId = null;
            try {
                if (state.get().equals(CacheState.STOPPED) && elementQueue.isEmpty()) {
                    break;
                }
                element = elementQueue.take();
                synchronized (accessor) {
                    // TODO 此处保证单元测试
                }
                cacheId = element.getCacheId();
                Object instance = element.getCacheObject();
                T copyInstance = copyInstances.get();
                synchronized (instance == null ? Thread.currentThread() : instance) {
                    Lock writeLock = waitForLock.writeLock();
                    try {
                        writeLock.lock();
                        if (element.isIgnore()) {
                            // 忽略不做任何处理
                            continue;
                        }

                        elements.remove(cacheId);

                        switch (element.getOperation()) {
                        case CREATE:
                            ReflectionUtility.copyInstance(element.getCacheObject(), copyInstance);
                            accessor.createInstance(cacheClass, copyInstance);
                            createdCount.incrementAndGet();
                            break;
                        case DELETE:
                            accessor.deleteInstance(cacheClass, element.getCacheId());
                            deletedCount.incrementAndGet();
                            break;
                        case UPDATE:
                            ReflectionUtility.copyInstance(element.getCacheObject(), copyInstance);
                            accessor.updateInstance(cacheClass, copyInstance);
                            updatedCount.incrementAndGet();
                            break;
                        default:
                            LOGGER.error("未支持的元素类型[{}]", element);
                            break;
                        }
                    } finally {
                        writeLock.unlock();
                    }
                }
                if (monitor != null) {
                    monitor.notifyOperate(element.getOperation(), element.getCacheId(), element.getCacheObject(), null);
                }
            } catch (InterruptedException exception) {
                // TODO 考虑中断策略不需要处理? 现在是由state维护.
            } catch (Exception exception) {
                // TODO 考虑是否再次把元素提交到队列?以及影响.
                if (monitor != null && element != null) {
                    monitor.notifyOperate(element.getOperation(), element.getCacheId(), element.getCacheObject(), exception);
                }
                exceptionCount.incrementAndGet();
                String message = StringUtility.format("队列策略[{}]处理元素[{}]时异常", new Object[] { name, element });
                LOGGER.error(message, exception);
            }
        }
    }

}
