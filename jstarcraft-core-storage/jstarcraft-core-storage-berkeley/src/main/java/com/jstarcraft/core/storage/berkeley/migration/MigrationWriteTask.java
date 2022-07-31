package com.jstarcraft.core.storage.berkeley.migration;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.storage.berkeley.exception.BerkeleyMigrationException;
import com.jstarcraft.core.utility.StringUtility;
import com.sleepycat.je.LockConflictException;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.raw.RawObject;
import com.sleepycat.persist.raw.RawStore;
import com.sleepycat.persist.raw.RawType;

/**
 * 迁移写任务
 * 
 * @author Birdy
 *
 */
public class MigrationWriteTask implements Callable<MigrationWriteTask> {

    private static final Logger logger = LoggerFactory.getLogger(MigrationWriteTask.class);

    private final MigrationCounter counter;

    private final MigrationConverter converter;

    private final MigrationContext context;

    private final String oldEntityName;

    private final String newEntityName;

    private final Collection<RawObject> dataCollection;

    private RawObject buildRawObject(RawType rawType) {
        final RawType superType = rawType.getSuperType();
        final RawObject superObject = superType != null ? this.buildRawObject(superType) : null;
        return new RawObject(rawType, new HashMap<String, Object>(), superObject);
    }

    public MigrationWriteTask(String oldEntityName, String newEntityName, MigrationContext context, MigrationCounter counter, MigrationConverter converter, Collection<RawObject> dataCollection) {
        this.oldEntityName = oldEntityName;
        this.newEntityName = newEntityName;
        this.context = context;
        this.counter = counter;
        this.converter = converter;
        this.dataCollection = dataCollection;
    }

    @Override
    public MigrationWriteTask call() throws Exception {
        try {
            final RawStore oldRawStore = context.getOldEntityStoreMap().get(oldEntityName);
            final RawStore newRawStore = context.getNewEntityStoreMap().get(newEntityName);
            final PrimaryIndex<Object, RawObject> newIndex = newRawStore.getPrimaryIndex(newEntityName);
            final RawType rawType = newRawStore.getModel().getRawType(newEntityName);
            for (RawObject oldData : dataCollection) {
                final AtomicInteger tryTimes = new AtomicInteger(10);
                final RawObject newData = this.buildRawObject(rawType);
                try {
                    boolean migrate = converter.convert(context, oldRawStore, newRawStore, oldData, newData);
                    if (migrate) {
                        do {
                            try {
                                newIndex.putNoReturn(newData);
                                counter.getWriteRecordNumber().incrementAndGet();
                                break;
                            } catch (LockConflictException exception) {
                                // TODO 可能存在次级键原因导致锁冲突,等待100毫秒进行重试
                                Thread.sleep(100);
                                if (tryTimes.decrementAndGet() < 0) {
                                    String message = StringUtility.format("迁移数据[{}]尝试次数到达限制", oldData);
                                    throw new BerkeleyMigrationException(message);
                                }
                            }
                        } while (true);
                    } else {
                        logger.debug("忽略数据[{}]", new Object[] { oldData });
                    }
                } catch (Throwable exception) {
                    String message = StringUtility.format("迁移异常:旧数据[{}],新数据[{}]", oldData, newData);
                    logger.error(message, exception);
                }
            }
        } finally {
            this.counter.getCurrentTaskNumber().decrementAndGet();
        }
        return this;
    }

}
