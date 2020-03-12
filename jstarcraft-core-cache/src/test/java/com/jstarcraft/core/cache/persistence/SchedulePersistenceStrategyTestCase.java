package com.jstarcraft.core.cache.persistence;

import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.cache.MockEntityObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SchedulePersistenceStrategyTestCase extends PersistenceStrategyTestCase {

    @Override
    protected Map<String, String> getPersistenceConfiguration() {
        Map<String, String> configuration = new HashMap<>();
        configuration.put(SchedulePersistenceStrategy.PARAMETER_CRON, "0/10 * * ? * *");
        return configuration;
    }

    @Override
    protected PersistenceStrategy getPersistenceStrategy(String name, Map<String, String> configuration) {
        SchedulePersistenceStrategy strategy = new SchedulePersistenceStrategy(name, configuration);
        return strategy;
    }

    @Test
    public void testMerge() throws Exception {
        PersistenceStrategy strategy = getPersistenceStrategy("strategy", getPersistenceConfiguration());
        strategy.start(accessor, cacheInformations);
        PersistenceManager<Integer, MockEntityObject> manager = strategy.getPersistenceManager(MockEntityObject.class);

        int id = -1;

        // CREATE, CREATE(会导致异常)
        manager.createInstance(MockEntityObject.instanceOf(id, "birdy" + id, "hong", id, id));
        Assert.assertThat(manager.getWaitSize(), is(1));
        manager.createInstance(MockEntityObject.instanceOf(id, "birdy" + id, "hong", id, id));
        Assert.assertThat(manager.getWaitSize(), is(1));
        Assert.assertThat(manager.getExceptionCount(), is(1L));

        // CREATE, UPDATE
        manager.updateInstance(MockEntityObject.instanceOf(id, "wolfy" + id, "xiao", id * id, id * id));
        Assert.assertThat(manager.getWaitSize(), is(1));
        Assert.assertThat(manager.getExceptionCount(), is(1L));

        // CREATE, DELETE
        manager.deleteInstance(id);
        Assert.assertThat(manager.getWaitSize(), is(0));
        Assert.assertThat(manager.getExceptionCount(), is(1L));

        // 操作统计校验
        Assert.assertThat(manager.getCreatedCount(), is(0L));
        Assert.assertThat(manager.getUpdatedCount(), is(0L));
        Assert.assertThat(manager.getDeletedCount(), is(0L));

        // UPDATE, CREATE(会导致异常)
        manager.createInstance(MockEntityObject.instanceOf(id, "birdy" + id, "hong", id, id));
        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }
        manager.updateInstance(MockEntityObject.instanceOf(id, "wolfy" + id, "xiao", id * id, id * id));
        Assert.assertThat(manager.getWaitSize(), is(1));
        manager.createInstance(MockEntityObject.instanceOf(id, "birdy" + id, "hong", id, id));
        Assert.assertThat(manager.getWaitSize(), is(1));
        Assert.assertThat(manager.getExceptionCount(), is(2L));

        // UPDATE, UPDATE
        manager.updateInstance(MockEntityObject.instanceOf(id, "mickey" + id, "hong", id, id));
        Assert.assertThat(manager.getWaitSize(), is(1));
        Assert.assertThat(manager.getExceptionCount(), is(2L));

        // UPDATE, DELETE
        manager.deleteInstance(id);
        Assert.assertThat(manager.getWaitSize(), is(1));
        Assert.assertThat(manager.getExceptionCount(), is(2L));

        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }
        // 操作统计校验
        Assert.assertThat(manager.getCreatedCount(), is(1L));
        Assert.assertThat(manager.getUpdatedCount(), is(0L));
        Assert.assertThat(manager.getDeletedCount(), is(1L));

        // DELETE, DELETE(会导致异常)
        manager.createInstance(MockEntityObject.instanceOf(id, "birdy" + id, "hong", id, id));
        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }
        manager.deleteInstance(id);
        Assert.assertThat(manager.getWaitSize(), is(1));
        manager.deleteInstance(id);
        Assert.assertThat(manager.getWaitSize(), is(1));
        Assert.assertThat(manager.getExceptionCount(), is(2L));

        // DELETE, UPDATE(会导致异常)
        manager.updateInstance(MockEntityObject.instanceOf(id, "wolfy" + id, "xiao", id * id, id * id));
        Assert.assertThat(manager.getWaitSize(), is(1));
        Assert.assertThat(manager.getExceptionCount(), is(3L));

        // DELETE, CREATE
        manager.createInstance(MockEntityObject.instanceOf(id, "mickey" + id, "hong", id, id));
        Assert.assertThat(manager.getWaitSize(), is(1));
        Assert.assertThat(manager.getExceptionCount(), is(3L));

        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }
        // 操作统计校验
        Assert.assertThat(manager.getCreatedCount(), is(2L));
        Assert.assertThat(manager.getUpdatedCount(), is(1L));
        Assert.assertThat(manager.getDeletedCount(), is(1L));

        manager.deleteInstance(id);
        while (true) {
            if (manager.getWaitSize() == 0) {
                break;
            }
            Thread.sleep(1000);
        }
        strategy.stop();
    }

}
