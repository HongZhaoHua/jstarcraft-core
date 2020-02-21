package com.jstarcraft.core.storage.berkeley.migration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.storage.berkeley.exception.BerkeleyMigrationException;
import com.jstarcraft.core.utility.StringUtility;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.SecondaryKey;
import com.sleepycat.persist.raw.RawObject;
import com.sleepycat.persist.raw.RawStore;

/**
 * 迁移读任务
 * 
 * @author Birdy
 *
 */
public class MigrationReadTask implements Callable<MigrationReadTask> {

    private static final Logger logger = LoggerFactory.getLogger(MigrationReadTask.class);

    private final int batchSize;

    private final MigrationCounter counter;

    private final MigrationContext context;

    private final String oldEntityName, newEntityName;

    public MigrationReadTask(String oldEntityName, String newEntityName, MigrationContext context, MigrationCounter counter, int batchSize) {
        this.oldEntityName = oldEntityName;
        this.newEntityName = newEntityName;
        this.context = context;
        this.counter = counter;
        this.batchSize = batchSize;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @Override
    public MigrationReadTask call() {
        if (this.context.getEntityConverterMap().get(this.newEntityName) instanceof IgnoreConverter) {
            // 忽略转换器,所以无需执行迁移过程
            return this;
        }

        try {
            // 实体的依赖
            final Class<?> entityClass = Class.forName(this.newEntityName);

            // 迁移任务所依赖的迁移任务的集合
            final Collection<Future<?>> dependencyTaskSet = new LinkedList<Future<?>>();
            ReflectionUtility.doWithLocalFields(entityClass, (field) -> {
                SecondaryKey annotation = field.getAnnotation(SecondaryKey.class);
                if (annotation != null && annotation.relatedEntity() != void.class && annotation.relatedEntity() != entityClass) {
                    final Future<?> future = context.getMigrationReadTask(annotation.relatedEntity().getName());
                    dependencyTaskSet.add(future);
                }
            });

            // 实体的双亲
            final Class<?> parentClass = AnnotationUtils.findAnnotationDeclaringClass(Entity.class, entityClass);
            if (parentClass != null && parentClass != entityClass) {
                final Future<?> future = this.context.getMigrationReadTask(parentClass.getName());
                if (future != null) {
                    dependencyTaskSet.add(future);
                }
            }

            // 指定的依赖
            if (context.getEntityDependencyMap().get(this.newEntityName) != null) {
                for (String dependencyClassName : context.getEntityDependencyMap().get(this.newEntityName)) {
                    final Future<?> future = this.context.getMigrationReadTask(dependencyClassName);
                    dependencyTaskSet.add(future);
                }
            }

            // 等待依赖的任务完成
            for (Future<?> dependencyTask : dependencyTaskSet) {
                dependencyTask.get();
            }

            logger.debug("迁移实体{}-{}", new Object[] { this.oldEntityName, this.newEntityName });

            // 开始迁移时间
            final long startTime = System.currentTimeMillis();

            final RawStore oldRawStore = context.getOldEntityStoreMap().get(this.oldEntityName);
            final RawStore newRawStore = context.getNewEntityStoreMap().get(this.newEntityName);

            final PrimaryIndex<Object, RawObject> oldIndex = oldRawStore.getPrimaryIndex(this.oldEntityName);
            final EntityCursor<RawObject> cursor = oldIndex.entities();

            while (true) {
                final Collection<RawObject> dataCollection = new ArrayList<RawObject>(this.batchSize);
                while (true) {
                    final RawObject oldData = cursor.next();
                    if ((oldData != null)) {
                        dataCollection.add(oldData);
                        counter.getReadRecordNumber().incrementAndGet();
                    } else {
                        break;
                    }
                    if (dataCollection.size() == this.batchSize) {
                        break;
                    }
                }

                this.counter.getCurrentTaskNumber().incrementAndGet();
                this.context.getMigrationWriteTask(newEntityName, dataCollection);

                if (counter.getReadRecordNumber().get() % 10000 == 0) {
                    final Object[] parameters = new Object[] { newEntityName, this.counter.getReadRecordNumber().get(), context.getWriteExecutor().getQueue().size(), context.getWriteExecutor().getActiveCount() };
                    logger.debug("迁移实体[{}],数量[{}],队列[{}],线程[{}]", parameters);
                }

                if (dataCollection.size() < this.batchSize) {
                    break;
                }
            }

            try {
                while (this.counter.getCurrentTaskNumber().get() != 0) {
                    Thread.yield();
                    if (logger.isDebugEnabled()) {
                        Thread.sleep(TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS));
                        logger.debug("迁移实体[{}],剩余任务[{}],线程[{}]", new Object[] { newEntityName, this.counter.getCurrentTaskNumber().get(), context.getWriteExecutor().getActiveCount() });
                    }
                }
                // 结束迁移时间
                long endTime = System.currentTimeMillis();
                logger.debug("迁移实体:旧实体名称[{}],新实体名称[{}],旧数量[{}],新数量[{}],消耗时间[{}]", new Object[] { oldEntityName, newEntityName, this.counter.getReadRecordNumber().get(), this.counter.getWriteRecordNumber().get(), (endTime - startTime) });

                // 迁移序列
                final String oldSequenceName = oldRawStore.getModel().getEntityMetadata(oldEntityName).getPrimaryKey().getSequenceName();
                final String newSequenceName = newRawStore.getModel().getEntityMetadata(newEntityName).getPrimaryKey().getSequenceName();

                if (oldSequenceName != null && newSequenceName != null) {
                    final DatabaseConfig databaseCofiguration = new DatabaseConfig();
                    final DatabaseEntry sequenceValue = new DatabaseEntry();

                    final String oldDatabaseName = Migrator.PERSIST_PREFIX + "#" + oldRawStore.getStoreName() + "#" + "com.sleepycat.persist.sequences";
                    final Database oldSequenceDatabase = oldRawStore.getEnvironment().openDatabase(null, oldDatabaseName, databaseCofiguration);
                    final String newDatabaseName = Migrator.PERSIST_PREFIX + "#" + newRawStore.getStoreName() + "#" + "com.sleepycat.persist.sequences";
                    final Database newSequenceDatabase = newRawStore.getEnvironment().openDatabase(null, newDatabaseName, databaseCofiguration);

                    final DatabaseEntry oldSequenceKey = new DatabaseEntry();
                    StringBinding.stringToEntry(oldSequenceName, oldSequenceKey);
                    oldSequenceDatabase.get(null, oldSequenceKey, sequenceValue, LockMode.DEFAULT);
                    final DatabaseEntry newSequenceKey = new DatabaseEntry();
                    StringBinding.stringToEntry(newSequenceName, newSequenceKey);
                    newSequenceDatabase.put(null, newSequenceKey, sequenceValue);

                    logger.debug("迁移序列:旧序列名称[{}],新序列名称[{}]", new Object[] { oldSequenceName, newSequenceName });

                    oldSequenceDatabase.close();
                    newSequenceDatabase.close();
                }

            } catch (InterruptedException exception) {
                String message = StringUtility.format("警告!等待被中断");
                logger.error(message, exception);
            } finally {
                cursor.close();
            }
        } catch (Exception exception) {
            throw new BerkeleyMigrationException(exception);
        }
        return this;
    }

}
