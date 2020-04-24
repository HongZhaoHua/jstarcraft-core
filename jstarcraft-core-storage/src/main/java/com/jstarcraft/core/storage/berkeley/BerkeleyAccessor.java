package com.jstarcraft.core.storage.berkeley;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.storage.StorageAccessor;
import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.storage.StorageIterator;
import com.jstarcraft.core.storage.StorageMetadata;
import com.jstarcraft.core.storage.StoragePagination;
import com.jstarcraft.core.storage.berkeley.exception.BerkeleyOperationException;
import com.jstarcraft.core.storage.berkeley.exception.BerkeleyStateException;
import com.jstarcraft.core.storage.berkeley.exception.BerkeleyVersionException;
import com.jstarcraft.core.storage.exception.StorageQueryException;
import com.jstarcraft.core.utility.DelayElement;
import com.jstarcraft.core.utility.SensitivityQueue;
import com.jstarcraft.core.utility.StringUtility;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.AnnotationModel;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.EntityModel;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

/**
 * Berkeley访问器
 * 
 * @author Birdy
 *
 */
public class BerkeleyAccessor implements StorageAccessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BerkeleyAccessor.class);

    /** 修复时间间隔 */
    private static final long FIX_TIME = 1000;

    /** 目录 */
    private final File directory;
    /** 配置 */
    private final Properties properties;
    /** 是否为只读 */
    private final boolean readOnly;
    /** 是否为延迟写 */
    private final boolean writeDelay;
    /** 是否为临时 */
    private final boolean temporary;
    /** 版本保持时间(单位:毫秒) */
    private final long versionKeep;

    private final HashSet<Class<?>> entityClasses;
    private final HashSet<Class<?>> persistentClasses;
    private final HashSet<Class<?>> proxyClasses;

    /** 环境 */
    private Environment environment;
    /** 管理器映射 */
    private Map<Class<?>, BerkeleyManager> managers = new HashMap<>();
    /** 元数据集合 */
    private HashSet<BerkeleyMetadata> metadatas = new HashSet<>();
    /** 监控器集合 */
    private HashSet<BerkeleyMonitor> monitors = new HashSet<>();
    /** 贮存器映射 */
    private Map<String, EntityStore> stores = new HashMap<>();
    /** 事务集合 */
    private ThreadLocal<BerkeleyTransactor> transactors = new ThreadLocal<>();

    /** 状态 */
    private AtomicReference<BerkeleyState> state = new AtomicReference<>(BerkeleyState.STOPPED);

    /** 版本缓存 */
    private HashMap<BerkeleyIdentification, BerkeleyVersion> versionCache = new HashMap<>();
    /** 版本锁 */
    private ReentrantReadWriteLock versionLock = new ReentrantReadWriteLock();
    /** 版本队列 */
    private SensitivityQueue<DelayElement<BerkeleyVersion>> versionQueue = new SensitivityQueue<>(FIX_TIME);
    /** 版本线程 */
    private Thread versionThread;
    /** 版本任务 */
    private final Runnable versionTask = new Runnable() {
        public void run() {
            try {
                while (true) {
                    DelayElement<BerkeleyVersion> element = versionQueue.take();
                    BerkeleyVersion content = element.getContent();
                    versionCache.remove(content.getIdentification(), content);
                }
            } catch (InterruptedException exception) {
            }
        }
    };

    public BerkeleyAccessor(Collection<Class<?>> classes, File directory, Properties properties, boolean readOnly, boolean writeDelay, boolean temporary, long versionKeep) {
        this.entityClasses = new HashSet<>();
        this.persistentClasses = new HashSet<>();
        this.proxyClasses = new HashSet<>();

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Entity.class)) {
                entityClasses.add(clazz);
            } else if (clazz.isAnnotationPresent(Persistent.class)) {
                if (AnnotationUtils.findAnnotation(clazz, Entity.class) != null) {
                    entityClasses.add(clazz);
                } else if (PersistentProxy.class.isAssignableFrom(clazz)) {
                    proxyClasses.add(clazz);
                } else {
                    persistentClasses.add(clazz);
                }
            } else {
                throw new IllegalArgumentException();
            }
        }

        this.directory = directory;
        // 检查目录是否存在
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IllegalArgumentException("数据环境目录异常");
            }
        }
        this.properties = properties;
        this.readOnly = readOnly;
        this.temporary = temporary;
        this.versionKeep = versionKeep;
        this.writeDelay = writeDelay;
    }

    public BerkeleyState getState() {
        return state.get();
    }

    public void start() {
        if (!state.compareAndSet(BerkeleyState.STOPPED, BerkeleyState.STARTED)) {
            throw new BerkeleyStateException();
        }

        // 设置线程
        versionThread = new Thread(versionTask);
        versionThread.setDaemon(true);
        versionThread.start();

        EnvironmentConfig environmentConfiguration = new EnvironmentConfig(properties);
        environmentConfiguration.setAllowCreate(true);
        environmentConfiguration.setTransactional(true);
        environment = new Environment(directory, environmentConfiguration);

        // 分类贮存与排序实体
        HashMap<String, TreeSet<BerkeleyMetadata>> store2Matadatas = new HashMap<>();
        for (Class<?> ormClass : entityClasses) {
            BerkeleyMetadata metadata = BerkeleyMetadata.instanceOf(ormClass);
            TreeSet<BerkeleyMetadata> metadatas = store2Matadatas.get(metadata.getStoreName());
            if (metadatas == null) {
                metadatas = new TreeSet<>();
                store2Matadatas.put(metadata.getStoreName(), metadatas);
            }
            metadatas.add(metadata);
        }

        for (Entry<String, TreeSet<BerkeleyMetadata>> keyValue : store2Matadatas.entrySet()) {
            StoreConfig storeConfiguration = new StoreConfig();
            storeConfiguration.setAllowCreate(true);
            storeConfiguration.setSecondaryBulkLoad(true);
            // TODO 注意temporary配置与事务/延迟写配置之间的冲突
            storeConfiguration.setTransactional(!temporary);
            storeConfiguration.setReadOnly(readOnly);
            storeConfiguration.setDeferredWrite(!temporary && writeDelay);
            storeConfiguration.setTemporary(temporary);

            EntityModel model = new AnnotationModel();
            // 注册实体类型
            for (BerkeleyMetadata metadata : keyValue.getValue()) {
                model.registerClass(metadata.getStoreClass());
                metadatas.add(metadata);
            }
            // 注册持久类型
            for (Class<?> clazz : persistentClasses) {
                model.registerClass(clazz);
            }
            // 注册代理类型
            for (Class<?> clazz : proxyClasses) {
                model.registerClass(clazz);
            }
            storeConfiguration.setModel(model);

            // 构建贮存
            EntityStore store = new EntityStore(environment, keyValue.getKey(), storeConfiguration);
            stores.put(keyValue.getKey(), store);

            // 构建索引
            for (BerkeleyMetadata metadata : keyValue.getValue()) {
                BerkeleyManager manager = new BerkeleyManager(metadata, store);
                managers.put(metadata.getOrmClass(), manager);
                metadatas.add(metadata);
            }
        }
    }

    public void stop() {
        if (!state.compareAndSet(BerkeleyState.STARTED, BerkeleyState.STOPPED)) {
            throw new BerkeleyStateException();
        }
        // 中断线程
        versionCache.clear();
        versionQueue.clear();
        versionThread.interrupt();

        // 停止贮存
        for (EntityStore store : stores.values()) {
            try {
                store.sync();
                store.close();
            } catch (Exception exception) {
                String message = StringUtility.format("停止Berkeley贮存[{}]时异常", store.getStoreName());
                LOGGER.error(message, exception);
            }
        }
        try {
            // 停止环境
            environment.sync();
            environment.close();
        } catch (Exception exception) {
            String message = StringUtility.format("停止Berkeley环境时异常");
            LOGGER.error(message, exception);
        }
    }

    @Override
    public Collection<? extends StorageMetadata> getAllMetadata() {
        return metadatas;
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> T getInstance(Class<T> clazz, K id) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return (T) manager.getInstance(transactor, id);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean createInstance(Class<T> clazz, T object) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.createInstance(transactor, object);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, K id) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.deleteInstance(transactor, id);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean deleteInstance(Class<T> clazz, T object) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        K id = (K) manager.getMetadata().getPrimaryValue(object);
        return manager.deleteInstance(transactor, id);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> boolean updateInstance(Class<T> clazz, T object) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        BerkeleyMetadata metadata = manager.getMetadata();
        // 判断是否检查版本
        if (metadata.getVersionName() != null) {
            BerkeleyVersion version = new BerkeleyVersion(metadata, object);
            checkVersions(transactor == null, transactor, version);
        }
        return manager.updateInstance(transactor, object);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K maximumIdentity(Class<T> clazz, K from, K to) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.maximumIdentity(transactor, from, to);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> K minimumIdentity(Class<T> clazz, K from, K to) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.minimumIdentity(transactor, from, to);
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> Map<K, I> queryIdentities(Class<T> clazz, StorageCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new StorageQueryException();
        }
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.queryIdentities(transactor, condition, name, values);
    }

    @Override
    public <K extends Comparable, I, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StorageCondition condition, String name, I... values) {
        if (!condition.checkValues(values)) {
            throw new StorageQueryException();
        }
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.queryInstances(transactor, condition, name, values);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryInstances(Class<T> clazz, StoragePagination pagination) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.queryInstances(transactor, pagination);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryIntersection(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.queryIntersection(transactor, condition, pagination);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> List<T> queryUnion(Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.queryUnion(transactor, condition, pagination);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countInstances(Class<T> clazz) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.countInstances(transactor);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countIntersection(Class<T> clazz, Map<String, Object> condition) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.countIntersection(transactor, condition);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> long countUnion(Class<T> clazz, Map<String, Object> condition) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        return manager.countUnion(transactor, condition);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterate(StorageIterator<T> iterator, Class<T> clazz, StoragePagination pagination) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        manager.iterateInstances(iterator, transactor, pagination);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateIntersection(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        manager.iterateIntersection(iterator, transactor, condition, pagination);
    }

    @Override
    public <K extends Comparable, T extends IdentityObject<K>> void iterateUnion(StorageIterator<T> iterator, Class<T> clazz, Map<String, Object> condition, StoragePagination pagination) {
        BerkeleyManager<K, T> manager = managers.get(clazz);
        BerkeleyTransactor transactor = transactors.get();
        manager.iterateUnion(iterator, transactor, condition, pagination);
    }

    /**
     * 获取事务
     * 
     * @return
     */
    public BerkeleyTransactor getTransactor() {
        return transactors.get();
    }

    /**
     * 打开事务
     * 
     * @param isolation
     * @return
     */
    public BerkeleyTransactor openTransactor(BerkeleyIsolation isolation) {
        Transaction transaction = environment.beginTransaction(null, isolation.getTransactionModel());
        BerkeleyTransactor transactor = new BerkeleyTransactor(isolation, transaction);
        transactors.set(transactor);
        for (BerkeleyMonitor monitor : monitors) {
            monitor.notifyOpen(transactor);
        }
        return transactor;
    }

    /**
     * 关闭事务
     */
    public void closeTransactor(boolean interrupt) {
        BerkeleyTransactor transactor = transactors.get();
        if (interrupt) {
            transactor.abort();
            // 事务回滚
            for (BerkeleyMonitor monitor : monitors) {
                monitor.notifyFailure(transactor);
            }
        } else {
            BerkeleyVersion[] versions = transactor.getVersions();
            checkVersions(true, transactor, versions);
        }

        transactors.remove();
        for (BerkeleyMonitor monitor : monitors) {
            monitor.notifyClose(transactor);
        }
    }

    private void checkVersions(boolean modify, BerkeleyTransactor transactor, BerkeleyVersion... versions) {
        Lock lock = modify ? versionLock.writeLock() : versionLock.readLock();
        try {
            lock.lock();
            for (BerkeleyVersion version : versions) {
                BerkeleyVersion value = versionCache.get(version.getIdentification());
                if (value != null && !value.equals(version)) {
                    if (modify && transactor != null) {
                        transactor.abort();
                        // 事务回滚
                        for (BerkeleyMonitor monitor : monitors) {
                            monitor.notifyFailure(transactor);
                        }
                    }
                    throw new BerkeleyVersionException("版本冲突");
                }
            }
            if (modify) {
                if (transactor != null) {
                    transactor.commit();
                    // 事务提交
                    for (BerkeleyMonitor monitor : monitors) {
                        monitor.notifySuccess(transactor);
                    }
                }
                Instant instant = Instant.now();
                instant = instant.plus(versionKeep, ChronoUnit.MILLIS);
                for (BerkeleyVersion version : versions) {
                    // 修改与设置版本信息
                    version.modify();
                    DelayElement element = new DelayElement(version, instant);
                    versionQueue.put(element);
                    versionCache.put(version.getIdentification(), version);
                }
            } else if (transactor != null) {
                // 缓存版本信息
                transactor.setVersions(versions);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 添加事务监控器
     * 
     * @param monitor
     */
    public synchronized void plugInTransactionMonitor(BerkeleyMonitor monitor) {
        monitors.add(monitor);
    }

    /**
     * 移除事务监控器
     * 
     * @param monitor
     */
    public synchronized void plugOutTransactionMonitor(BerkeleyMonitor monitor) {
        monitors.remove(monitor);
    }

    public synchronized HashSet<BerkeleyMonitor> getMonitors() {
        return new HashSet<>(monitors);
    }

    public Environment getEnvironment() {
        return environment;
    }

}
