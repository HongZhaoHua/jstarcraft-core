package com.jstarcraft.core.storage.berkeley.migration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.storage.berkeley.BerkeleyAccessor;
import com.jstarcraft.core.storage.berkeley.exception.BerkeleyMigrationException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.raw.RawStore;

/**
 * 迁移器
 * 
 * <pre>
 * 迁移流程:
 * 1.拷贝旧环境,通常是文件复制过程,目的在于为迁移做基础准备;
 * 2.构建新环境,通常是在旧环境基础,以仓储(Stroe)为单位重构建,重构建的仓储所包含的全部实体都要进行迁移;
 * 3.确定迁移实体以后,从旧环境获取数据,通过(Converter)转换器转换以后,将数据填充到新环境;
 * 
 * 注意:在迁移过程将缓存设置到最低是为了提高迁移的效率
 * 原因在于缓存的数据,在缓存填充满以后,要通过LRU算法交换出去,缓存的数据越多,开销越大,反而导致插入的性能下降.
 * 在迁移过程主要操作是插入,所以要将缓存设置到最小.
 * </pre>
 * 
 * @author Birdy
 */
public class Migrator {

    private static final Logger logger = LoggerFactory.getLogger(Migrator.class);

    public static final String FORMAT_SUFFIX = "formats";

    public static final String SEQUENCE_SUFFIX = "sequences";

    public static final String PERSIST_PREFIX = "persist";

    /** 环境配置文件 */
    private final Properties environmentProperties = new Properties();

    /** 新数据库路径 */
    private final File newDatabaseDirectory;

    /** 旧数据库路径 */
    private final File oldDatabaseDirectory;

    /** 迁移仓储名称 */
    private final Collection<String> migrateStroreNames;

    /** 新实体名称与旧实体名称的映射 */
    private final Map<String, String> classNameMap;

    /** 实体名称与转换器名称的映射 */
    private final Map<String, String> entityConverterMap;

    /** 实体与依赖的映射 */
    private final Map<String, Collection<String>> entityDependencyMap;

    private final String berkeleyConverter;

    /**
     * 获取环境所有的实体名称
     * 
     * @param environment
     * @return 实体名称的集合
     */
    private Collection<String> getAllEntityNames(Environment environment) {
        final Collection<String> entityStoreSet = new HashSet<String>();
        for (String databaseName : environment.getDatabaseNames()) {
            // 检测数据库名称是否符合EntityStore的名称规范(persist#STORE_NAME#ENTITY_CLASS)
            if (databaseName.startsWith(PERSIST_PREFIX) && !databaseName.endsWith(FORMAT_SUFFIX) && !databaseName.endsWith(SEQUENCE_SUFFIX)) {
                final String[] databasePartNames = databaseName.split("#");
                entityStoreSet.add(databasePartNames[2]);
            }
        }
        return entityStoreSet;
    }

    /**
     * 迁移器
     * 
     * @param configurationFile 配置文件名
     * @param newDatabasePath   新数据库路径
     * @param oldDatabasePath   旧数据库路径
     */
    public Migrator(Collection<String> migrateStroreNames, Map<String, String> classNameMap, Map<String, String> entityConverterMap, Map<String, Collection<String>> entityDependencyMap, String configurationFile, String oldDatabaseDirectory, String newDatabaseDirectory, String berkeleyConverter) {
        try (BufferedInputStream in = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(configurationFile))) {
            environmentProperties.load(in);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.berkeleyConverter = berkeleyConverter;

        this.migrateStroreNames = migrateStroreNames;

        this.newDatabaseDirectory = new File(newDatabaseDirectory);
        this.oldDatabaseDirectory = new File(oldDatabaseDirectory);

        this.classNameMap = classNameMap;
        this.entityConverterMap = entityConverterMap;
        this.entityDependencyMap = entityDependencyMap;
    }

    /**
     * 拷贝旧数据库环境
     * 
     * @param sourceDirectory
     * @param targetDirectory
     */
    public void copyOldEnvironment(File sourceDirectory, File targetDirectory) {
        final int cacheSize = 2 * 1024 * 1024;
        if (!sourceDirectory.exists()) {
            throw new RuntimeException("来源目录[" + sourceDirectory + "]不存在");
        }
        if (targetDirectory.exists()) {
            throw new RuntimeException("目标目录[" + targetDirectory + "]已存在");
        }
        targetDirectory.mkdirs();

        for (File from : sourceDirectory.listFiles()) {
            if (from.isFile()) {
                final File to = new File(targetDirectory, from.getName());
                try {
                    FileUtils.copyFile(from, to);
                    logger.debug("拷贝文件从[{}]到[{}]", from.getAbsolutePath(), to.getAbsolutePath());
                } catch (IOException exception) {
                    throw new BerkeleyMigrationException("拷贝文件失败", exception);
                }
            }
        }
    }

    /**
     * 构建新数据库环境
     * 
     * @param databaseConfiguration
     */
    public void buildNewEnvironment(BerkeleyAccessor accessorFactory) {
        // 删除准备迁移的仓储
        final EnvironmentConfig environmentConfiguration = new EnvironmentConfig(environmentProperties);
        final Environment environment = new Environment(this.newDatabaseDirectory, environmentConfiguration);

        for (String databaseName : environment.getDatabaseNames()) {
            // 检测数据库名称是否符合EntityStore的名称规范(persist#STORE_NAME#ENTITY_CLASS)
            if (databaseName.startsWith(PERSIST_PREFIX)) {
                final String[] databasePartNames = databaseName.split("#");
                logger.debug("扫描数据库[{}]", databaseName);
                if (this.migrateStroreNames.contains(databasePartNames[1])) {
                    environment.removeDatabase(null, databaseName);
                    logger.debug("删除数据库[{}]", databaseName);
                }
            }
        }

        environment.close();

        // 让环境自动构建迁移的实体数据表
        // final ClassPathXmlApplicationContext applicationContext = new
        // ClassPathXmlApplicationContext(new String[] { databaseConfiguration });
        // BerkeleyAccessor accessorFactory =
        // applicationContext.getBean(BerkeleyAccessor.class);
        // applicationContext.close();
        // while (accessorFactory.getState() != BerkeleyState.STOPPED) {
        // Thread.yield();
        // }
        accessorFactory.start();
        accessorFactory.stop();
    }

    /**
     * 扫描指定环境中的实体名称与仓储名称的映射
     * 
     * @param environment
     * @return 实体名称与仓储名称的映射
     */
    private Map<String, String> scanEntityStoreNameMap(Environment environment) {
        final Map<String, String> entityStoreNameMap = new HashMap<String, String>();
        for (String databaseName : environment.getDatabaseNames()) {
            // 检测数据库名称是否符合EntityStore的名称规范(persist#STORE_NAME#ENTITY_CLASS)
            if (databaseName.startsWith(PERSIST_PREFIX) && !databaseName.endsWith(FORMAT_SUFFIX) && !databaseName.endsWith(SEQUENCE_SUFFIX)) {
                final String[] databasePartNames = databaseName.split("#");
                // 防止重复构建RawStore
                if (databasePartNames.length == 3) {
                    if (entityStoreNameMap.containsKey(databasePartNames[2])) {
                        throw new RuntimeException("实体名称冲突");
                    } else {
                        entityStoreNameMap.put(databasePartNames[2], databasePartNames[1]);
                    }
                }
            }
        }
        return entityStoreNameMap;
    }

    /**
     * 构建仓储的映射
     * 
     * @param environment
     * @param entityStoreNameMap
     * @return 仓储名称与仓储实例的映射
     */
    private Map<String, RawStore> bulidRawStoreMap(Environment environment, Map<String, String> entityStoreNameMap) {
        final StoreConfig storeConfiguration = new StoreConfig();
        final Map<String, RawStore> rawStoreMap = new HashMap<String, RawStore>();
        for (String storeName : entityStoreNameMap.values()) {
            if (!rawStoreMap.containsKey(storeName)) {
                rawStoreMap.put(storeName, new RawStore(environment, storeName, storeConfiguration));
            }
        }
        return rawStoreMap;
    }

    /**
     * 迁移流程
     * 
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void migrate() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final EnvironmentConfig environmentConfiguration = new EnvironmentConfig(environmentProperties);

        final Environment newDatabaseEnvironment = new Environment(this.newDatabaseDirectory, environmentConfiguration);
        final Environment oldDatabaseEnvironment = new Environment(this.oldDatabaseDirectory, environmentConfiguration);

        final Map<String, MigrationConverter> entityConverterMap = new HashMap<String, MigrationConverter>();

        /** 新旧实体的名称与仓储名称的映射 */
        final Map<String, String> oldEntityStoreNameMap = this.scanEntityStoreNameMap(oldDatabaseEnvironment);
        final Map<String, String> newEntityStoreNameMap = this.scanEntityStoreNameMap(newDatabaseEnvironment);

        /** 仓储的名称与仓储的映射 */
        final Map<String, RawStore> oldRawStoreMap = this.bulidRawStoreMap(oldDatabaseEnvironment, oldEntityStoreNameMap);
        final Map<String, RawStore> newRawStoreMap = this.bulidRawStoreMap(newDatabaseEnvironment, newEntityStoreNameMap);

        /** 新旧实体与仓储的映射 */
        final Map<String, RawStore> oldEntityStoreMap = new HashMap<String, RawStore>();
        final Map<String, RawStore> newEntityStoreMap = new HashMap<String, RawStore>();

        for (Entry<String, String> keyValue : oldEntityStoreNameMap.entrySet()) {
            oldEntityStoreMap.put(keyValue.getKey(), oldRawStoreMap.get(keyValue.getValue()));
        }
        for (Entry<String, String> keyValue : newEntityStoreNameMap.entrySet()) {
            newEntityStoreMap.put(keyValue.getKey(), newRawStoreMap.get(keyValue.getValue()));
        }

        final Map<String, MigrationConverter> converterMap = new HashMap<String, MigrationConverter>();

        // 数据库环境的实体名称集合
        final Collection<String> oldEntityNameCollection = this.getAllEntityNames(oldDatabaseEnvironment);
        final Collection<String> newEntityNameCollection = this.getAllEntityNames(newDatabaseEnvironment);

        logger.debug("旧数据库实体列表[{}]", oldEntityNameCollection);
        logger.debug("新数据库实体列表[{}]", newEntityNameCollection);

        final Collection<String> migrateEntityNames = new HashSet<String>();
        for (final Entry<String, String> keyValue : newEntityStoreNameMap.entrySet()) {
            if (this.migrateStroreNames.contains(keyValue.getValue())) {
                migrateEntityNames.add(keyValue.getKey());
            }
        }

        for (final String newEntityName : migrateEntityNames) {
            final String oldEntityName;
            // 获取与新实体名称对应的旧实体名称
            if (this.classNameMap.containsKey(newEntityName)) {
                oldEntityName = this.classNameMap.get(newEntityName);
            } else {
                oldEntityName = newEntityName;
            }

            if (!oldEntityNameCollection.contains(oldEntityName)) {
                // 新实体在旧数据库中没有对应的旧实体,所以无需执行迁移过程
                continue;
            }

            this.classNameMap.put(newEntityName, oldEntityName);

            // 获取与新实体名称对应的转换器名称
            final String converterName;
            if (this.entityConverterMap.containsKey(newEntityName)) {
                converterName = this.entityConverterMap.get(newEntityName);
            } else {
                converterName = this.berkeleyConverter;
            }

            if (!converterMap.containsKey(converterName)) {
                converterMap.put(converterName, MigrationConverter.class.cast(Class.forName(converterName).newInstance()));
            }

            entityConverterMap.put(newEntityName, converterMap.get(converterName));
        }

        logger.debug("迁移实体列表[{}]", this.classNameMap);

        final MigrationContext context = new MigrationContext(this.classNameMap, entityConverterMap, this.entityDependencyMap, oldDatabaseEnvironment, newDatabaseEnvironment, oldEntityStoreMap, newEntityStoreMap);
        // 迁移实体
        for (String entityName : this.classNameMap.keySet()) {
            context.getMigrationReadTask(entityName);
        }

        context.getReadeExecutor().shutdown();

        try {
            if (context.getReadeExecutor().awaitTermination(60, TimeUnit.MINUTES)) {
                logger.debug("迁移成功");
            } else {
                logger.debug("迁移失败");
            }
        } catch (InterruptedException exception) {
            logger.error("迁移异常", exception);
        } finally {
            context.getWriteExecutor().shutdown();

            for (RawStore store : oldEntityStoreMap.values()) {
                store.close();
            }

            for (RawStore store : newRawStoreMap.values()) {
                store.close();
            }

            oldDatabaseEnvironment.close();
            newDatabaseEnvironment.close();
        }

    }

    public File getNewDatabaseDirectory() {
        return newDatabaseDirectory;
    }

    public File getOldDatabaseDirectory() {
        return oldDatabaseDirectory;
    }

}
