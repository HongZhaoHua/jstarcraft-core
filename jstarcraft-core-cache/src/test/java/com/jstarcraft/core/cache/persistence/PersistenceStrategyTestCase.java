package com.jstarcraft.core.cache.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jstarcraft.core.cache.CacheInformation;
import com.jstarcraft.core.cache.MockEntityObject;
import com.jstarcraft.core.storage.StorageAccessor;
import com.jstarcraft.core.utility.StringUtility;

public abstract class PersistenceStrategyTestCase {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected StorageAccessor accessor;

    protected Map<Class<?>, CacheInformation> cacheInformations = new HashMap<>();

    {
        cacheInformations.put(MockEntityObject.class, CacheInformation.instanceOf(MockEntityObject.class));
    }

    protected abstract Map<String, String> getPersistenceConfiguration();

    protected abstract PersistenceStrategy getPersistenceStrategy(String name, Map<String, String> configuration);

    @Test
    public void testPerformance() throws Exception {
        int size = 10000;
        PersistenceStrategy strategy = getPersistenceStrategy("strategy", getPersistenceConfiguration());
        strategy.start(accessor, cacheInformations);
        PersistenceManager<Integer, MockEntityObject> manager = strategy.getPersistenceManager(MockEntityObject.class);

        // 创建数据
        long begin = System.currentTimeMillis();
        synchronized (accessor) {
            for (int index = 0; index < size; index++) {
                manager.createInstance(MockEntityObject.instanceOf(index, "birdy" + index, "hong", index, index));
            }
        }
        long end = System.currentTimeMillis();
        String message = StringUtility.format("创建{}数据的时间:{}毫秒", size, end - begin);
        logger.debug(message);
        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }

        // 修改数据
        begin = System.currentTimeMillis();
        synchronized (accessor) {
            for (int index = 0; index < size; index++) {
                manager.updateInstance(MockEntityObject.instanceOf(index, "xiao" + index, "xiao", index * index, 100));
                List<MockEntityObject> objects = manager.getInstances("firstName", "xiao" + index);
                Assert.assertThat(objects.size(), CoreMatchers.equalTo(1));
                for (MockEntityObject object : objects) {
                    Assert.assertThat(object.getFirstName(), CoreMatchers.equalTo("xiao" + index));
                }
            }
        }
        end = System.currentTimeMillis();
        message = StringUtility.format("修改{}数据的时间:{}毫秒", size, end - begin);
        logger.debug(message);
        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }

        // 查询数据
        synchronized (accessor) {
            List<MockEntityObject> objects = manager.getInstances("token", 100);
            Assert.assertThat(objects.size(), CoreMatchers.equalTo(size));
            for (MockEntityObject object : objects) {
                Assert.assertThat(object.getLastName(), CoreMatchers.equalTo("xiao"));
            }
        }

        // 删除数据
        begin = System.currentTimeMillis();
        synchronized (accessor) {
            for (int index = 0; index < size; index++) {
                manager.deleteInstance(index);
            }
        }
        end = System.currentTimeMillis();
        message = StringUtility.format("删除{}数据的时间:{}毫秒", size, end - begin);
        logger.debug(message);
        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }

        strategy.stop();
    }

    @Test
    public void testQuery() throws Exception {
        int size = 10000;
        PersistenceStrategy strategy = getPersistenceStrategy("strategy", getPersistenceConfiguration());
        strategy.start(accessor, cacheInformations);
        PersistenceManager<Integer, MockEntityObject> manager = strategy.getPersistenceManager(MockEntityObject.class);

        synchronized (accessor) {
            // 创建数据
            for (int index = 0; index < size; index++) {
                manager.createInstance(MockEntityObject.instanceOf(index, "birdy" + index, "hong", index, index));
            }
            Assert.assertEquals(0, manager.getCreatedCount());
            for (int index = 0; index < size; index++) {
                MockEntityObject instance = manager.getInstance(index);
                Assert.assertNotNull(instance);
            }
        }
        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }
        Assert.assertEquals(size, manager.getCreatedCount());

        synchronized (accessor) {
            // 修改数据
            for (int index = 0; index < size; index++) {
                manager.updateInstance(MockEntityObject.instanceOf(index, "xiao" + index, "xiao", index * index, index * index));
            }
            Assert.assertEquals(0, manager.getUpdatedCount());
            for (int index = 0; index < size; index++) {
                MockEntityObject instance = manager.getInstance(index);
                Assert.assertThat(instance.getLastName(), CoreMatchers.equalTo("xiao"));
            }
        }
        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }
        Assert.assertEquals(size, manager.getUpdatedCount());

        synchronized (accessor) {
            // 删除数据
            for (int index = 0; index < size; index++) {
                manager.deleteInstance(index);
            }
            Assert.assertEquals(0, manager.getDeletedCount());
            for (int index = 0; index < size; index++) {
                MockEntityObject instance = manager.getInstance(index);
                Assert.assertNull(instance);
            }
        }
        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }
        Assert.assertEquals(size, manager.getDeletedCount());

        strategy.stop();
    }

    @Test
    public void testUpdate() throws Exception {
        PersistenceStrategy strategy = getPersistenceStrategy("strategy", getPersistenceConfiguration());
        strategy.start(accessor, cacheInformations);
        PersistenceManager<Integer, MockEntityObject> manager = strategy.getPersistenceManager(MockEntityObject.class);

        MockEntityObject object = MockEntityObject.instanceOf(0, "birdy", "hong", 1, -1);
        manager.createInstance(object);
        object.modify("洪", 0, true);
        manager.updateInstance(object);

        strategy.stop();
        object = accessor.getInstance(MockEntityObject.class, 0);
        Assert.assertThat(object.getLastName(), CoreMatchers.equalTo("洪"));
        Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(0));

        accessor.deleteInstance(MockEntityObject.class, 0);
    }

}
