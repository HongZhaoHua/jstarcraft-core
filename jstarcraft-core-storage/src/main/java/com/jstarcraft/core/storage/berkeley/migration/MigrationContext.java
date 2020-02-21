package com.jstarcraft.core.storage.berkeley.migration;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Environment;
import com.sleepycat.persist.raw.RawObject;
import com.sleepycat.persist.raw.RawStore;

/**
 * 迁移上下文对象
 * 
 * @author Birdy
 *
 */
public class MigrationContext {

    private static final Logger logger = LoggerFactory.getLogger(MigrationContext.class);

    public static final String FORMAT_SUFFIX = "formats";

    public static final String SEQUENCE_SUFFIX = "sequences";

    public static final String PERSIST_PREFIX = "persist";

    /** 设置任务队列大小(TODO 考虑配置项) */
    public static final int QUEUE_SIZE = 1000;

    /** 设置批量数据大小(TODO 考虑配置项) */
    public static final int BATCH_SIZE = 1000;

    /** 警戒数量(当出现拒绝任务异常以后,会停止任务提交,直到任务队列低于警戒数量)(TODO 考虑配置项) */
    public static final int ALERT_SIZE = MigrationContext.QUEUE_SIZE / 2;

    private final ThreadPoolExecutor readeExecutor;

    private final ThreadPoolExecutor writeExecutor;

    /** 所有迁移任务的映射集合 */
    private final ConcurrentMap<String, Future<MigrationReadTask>> migrationTaskMap = new ConcurrentHashMap<String, Future<MigrationReadTask>>();

    /** 所有迁移计数器的映射集合 */
    private final ConcurrentMap<String, MigrationCounter> counterMap = new ConcurrentHashMap<String, MigrationCounter>();

    /** 新实体名称与旧实体名称的映射 */
    private final Map<String, String> classNameMap;

    /** 实体名称与转换器的映射 */
    private final Map<String, MigrationConverter> entityConverterMap;

    /** 实体与依赖的映射 */
    private final Map<String, Collection<String>> entityDependencyMap;

    /** 新旧数据库环境 */
    private final Environment oldDatabaseEnvironment, newDatabaseEnvironment;

    /** 新旧实体与仓储的映射 */
    private final Map<String, RawStore> oldEntityStoreMap, newEntityStoreMap;

    public MigrationContext(Map<String, String> classNameMap, Map<String, MigrationConverter> entityConverterMap, Map<String, Collection<String>> entityDependencyMap, Environment oldDatabaseEnvironment, Environment newDatabaseEnvironment, Map<String, RawStore> oldEntityStoreMap, Map<String, RawStore> newEntityStoreMap) {
        this.classNameMap = classNameMap;
        for (String entityName : this.classNameMap.keySet()) {
            this.counterMap.put(entityName, new MigrationCounter());
        }

        this.readeExecutor = new ThreadPoolExecutor(0, classNameMap.size(), 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1));
        this.writeExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(QUEUE_SIZE));
        this.entityConverterMap = entityConverterMap;
        this.entityDependencyMap = entityDependencyMap;
        this.oldDatabaseEnvironment = oldDatabaseEnvironment;
        this.newDatabaseEnvironment = newDatabaseEnvironment;
        this.oldEntityStoreMap = oldEntityStoreMap;
        this.newEntityStoreMap = newEntityStoreMap;
    }

    public ThreadPoolExecutor getReadeExecutor() {
        return readeExecutor;
    }

    public ThreadPoolExecutor getWriteExecutor() {
        return writeExecutor;
    }

    public Map<String, String> getClassNameMap() {
        return classNameMap;
    }

    public Map<String, MigrationConverter> getEntityConverterMap() {
        return entityConverterMap;
    }

    public Environment getOldDatabaseEnvironment() {
        return oldDatabaseEnvironment;
    }

    public Environment getNewDatabaseEnvironment() {
        return newDatabaseEnvironment;
    }

    public Map<String, RawStore> getOldEntityStoreMap() {
        return oldEntityStoreMap;
    }

    public Map<String, RawStore> getNewEntityStoreMap() {
        return newEntityStoreMap;
    }

    public Future<MigrationReadTask> getMigrationReadTask(String entityName) {
        if (!this.classNameMap.containsKey(entityName)) {
            return null;
        }
        final String oldEntityName = this.getClassNameMap().get(entityName);
        final String newEntityName = entityName;
        synchronized (migrationTaskMap) {
            if (!migrationTaskMap.containsKey(entityName)) {
                final MigrationReadTask task = new MigrationReadTask(oldEntityName, newEntityName, this, this.counterMap.get(entityName), BATCH_SIZE);
                final Future<MigrationReadTask> future = this.readeExecutor.submit(task);
                migrationTaskMap.put(entityName, future);
            }
        }
        return migrationTaskMap.get(entityName);
    }

    public Map<String, Collection<String>> getEntityDependencyMap() {
        return entityDependencyMap;
    }

    public Future<MigrationWriteTask> getMigrationWriteTask(String entityName, Collection<RawObject> dataCollection) {
        final String oldEntityName = this.classNameMap.get(entityName);
        final String newEntityName = entityName;
        final MigrationConverter converter = this.entityConverterMap.get(entityName);
        while (true) {
            try {
                return this.writeExecutor.submit(new MigrationWriteTask(oldEntityName, newEntityName, this, this.counterMap.get(entityName), converter, dataCollection));
            } catch (RejectedExecutionException exception) {
                logger.debug("迁移实体[{}]拒绝任务异常", newEntityName);
                // 任务队列超过警戒数量,停止提交任务,防止RejectedExecutionException
                while (this.writeExecutor.getQueue().size() >= ALERT_SIZE) {
                    // TODO 比较yield与sleep的区别
                    Thread.yield();
                    // Thread.sleep(TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS));
                }
                logger.debug("迁移实体[{}]任务恢复,队列[{}]", newEntityName, this.writeExecutor.getQueue().size());
            }
        }
    }

}
