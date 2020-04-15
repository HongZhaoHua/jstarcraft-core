package com.jstarcraft.core.storage.berkeley.migration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.storage.berkeley.BerkeleyAccessor;
import com.jstarcraft.core.storage.berkeley.migration.BerkeleyConverter;
import com.jstarcraft.core.storage.berkeley.migration.Migrator;
import com.jstarcraft.core.storage.berkeley.migration.older.Information;
import com.jstarcraft.core.storage.berkeley.migration.older.Item;
import com.jstarcraft.core.storage.berkeley.migration.older.Player;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MigrationTestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("newer")
    private BerkeleyAccessor newerAccessor;

    @Autowired
    @Qualifier("older")
    private BerkeleyAccessor olderAccessor;

    @Test
    public void testMigrate() throws Exception {
        final int numberOfThread = 10;
        final int batchSize = 100;
        final Collection<Batch> threads = new ArrayList<Batch>(numberOfThread);

        final CyclicBarrier barrier = new CyclicBarrier(numberOfThread + 1);
        for (int index = 0; index < numberOfThread; index++) {
            Batch insert = new Batch(barrier, batchSize, olderAccessor);
            threads.add(insert);
            insert.start();
        }
        // 等待所有线程开始
        barrier.await();
        long totalTime = 0;
        long startTime = System.currentTimeMillis();
        // 等待所有线程结束
        barrier.await();
        long endTime = System.currentTimeMillis();
        totalTime += (endTime - startTime);
        long averageTime = 0;
        for (Batch thread : threads) {
            long time = thread.getExecuteTime(TimeUnit.MILLISECONDS);
            logger.debug("thread[" + thread.getId() + "] execute time is : " + time);
            averageTime += time;
        }
        averageTime = averageTime / threads.size();
        logger.debug("execute average time is : " + averageTime);
        logger.debug("execute total time is : " + totalTime);

        long start = System.currentTimeMillis();
        logger.debug("item count is : " + olderAccessor.countInstances(Item.class));
        logger.debug("player count is : " + olderAccessor.countInstances(Player.class));
        logger.debug("information count is : " + olderAccessor.countInstances(Information.class));
        long end = System.currentTimeMillis();
        logger.debug("count total time is : " + (end - start));

        File olderFile = olderAccessor.getEnvironment().getHome();
        File newerFile = newerAccessor.getEnvironment().getHome();
        olderAccessor.stop();
        newerAccessor.stop();
        FileUtils.forceDelete(newerFile);

        // 设置需要迁移的库
        Collection<String> migrateStroreNames = new HashSet<String>();
        migrateStroreNames.add("migration");

        // 设置新旧实体名称的映射,默认新旧实体名称是一致的
        Map<String, String> entityNameMap = new HashMap<String, String>();
        entityNameMap.put("com.jstarcraft.core.storage.berkeley.migration.newer.Player", "com.jstarcraft.core.storage.berkeley.migration.older.Player");
        entityNameMap.put("com.jstarcraft.core.storage.berkeley.migration.newer.Item", "com.jstarcraft.core.storage.berkeley.migration.older.Item");
        entityNameMap.put("com.jstarcraft.core.storage.berkeley.migration.newer.Information", "com.jstarcraft.core.storage.berkeley.migration.older.Information");
        // 设置实体与转换器的映射,默认使用BerkeleyConverter作为转换器
        Map<String, String> entityConverterMap = new HashMap<String, String>();
        entityConverterMap.put("com.jstarcraft.core.storage.berkeley.migration.newer.Player", "com.jstarcraft.core.storage.berkeley.migration.Old2NewPlayerConverter");

        Map<String, Collection<String>> entityDependencyMap = new HashMap<String, Collection<String>>();
        String configurationFile = "migration.properties";
        String oldDatabaseDirectory = "target/older";
        String newDatabaseDirectory = "target/newer";
        String berkeleyConverter = BerkeleyConverter.class.getName();
        Migrator migrator = new Migrator(migrateStroreNames, entityNameMap, entityConverterMap, entityDependencyMap, configurationFile, oldDatabaseDirectory, newDatabaseDirectory, berkeleyConverter);

        // 拷贝旧环境,通常是文件复制过程,目的在于为迁移做基础准备;
        migrator.copyOldEnvironment(migrator.getOldDatabaseDirectory(), migrator.getNewDatabaseDirectory());

        // 构建新环境,通常是在旧环境基础,以仓储(Stroe)为单位重构建,重构建的仓储所包含的全部实体都要进行迁移;
        migrator.buildNewEnvironment(newerAccessor);

        // 确定迁移实体以后,从旧环境获取数据,通过(Converter)转换器转换以后,将数据填充到新环境;
        migrator.migrate();

        FileUtils.forceDelete(olderFile);
        FileUtils.forceDelete(newerFile);
    }

}
