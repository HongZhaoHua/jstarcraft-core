package com.jstarcraft.core.cache.persistence;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.cache.MockEntityObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PromptPersistenceStrategyTestCase extends PersistenceStrategyTestCase {

    @Override
    protected Map<String, String> getPersistenceConfiguration() {
        Map<String, String> configuration = new HashMap<>();
        return configuration;
    }

    @Override
    protected PersistenceStrategy getPersistenceStrategy(String name, Map<String, String> configuration) {
        PromptPersistenceStrategy strategy = new PromptPersistenceStrategy(name, configuration);
        return strategy;
    }

    @Test
    public void testQuery() throws Exception {
        int size = 10000;
        PersistenceStrategy strategy = getPersistenceStrategy("strategy", getPersistenceConfiguration());
        strategy.start(accessor, cacheInformations);
        PersistenceManager<Integer, MockEntityObject> manager = strategy.getPersistenceManager(MockEntityObject.class);

        // 创建数据
        for (int index = 0; index < size; index++) {
            manager.createInstance(MockEntityObject.instanceOf(index, "birdy" + index, "hong", index, index));
        }
        Assert.assertTrue(manager.getCreatedCount() == size);
        for (int index = 0; index < size; index++) {
            MockEntityObject instance = manager.getInstance(index);
            Assert.assertNotNull(instance);
        }
        Assert.assertTrue(manager.getCreatedCount() == size);

        // 修改数据
        for (int index = 0; index < size; index++) {
            manager.updateInstance(MockEntityObject.instanceOf(index, "xiao" + index, "xiao", index * index, index * index));
        }
        Assert.assertTrue(manager.getUpdatedCount() == size);
        for (int index = 0; index < size; index++) {
            MockEntityObject instance = manager.getInstance(index);
            Assert.assertThat(instance.getLastName(), CoreMatchers.equalTo("xiao"));
        }
        Assert.assertTrue(manager.getUpdatedCount() == size);

        // 删除数据
        for (int index = 0; index < size; index++) {
            manager.deleteInstance(index);
        }
        Assert.assertTrue(manager.getDeletedCount() == size);
        for (int index = 0; index < size; index++) {
            MockEntityObject instance = manager.getInstance(index);
            Assert.assertNull(instance);
        }
        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }
        Assert.assertTrue(manager.getDeletedCount() == size);

        strategy.stop();
    }

}
