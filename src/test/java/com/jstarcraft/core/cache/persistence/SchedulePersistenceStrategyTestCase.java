package com.jstarcraft.core.cache.persistence;

import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.cache.CacheInformation;
import com.jstarcraft.core.cache.MockEntityObject;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy.PersistenceType;
import com.jstarcraft.core.orm.OrmAccessor;
import com.jstarcraft.core.utility.StringUtility;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SchedulePersistenceStrategyTestCase extends PersistenceStrategyTestCase {

	@Override
	protected PersistenceConfiguration getPersistenceConfiguration() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put(SchedulePersistenceStrategy.PARAMETER_CRON, "0/10 * * ? * *");
		PersistenceConfiguration configuration = new PersistenceConfiguration("schedulePersistenceStrategy", PersistenceType.SCHEDULE, parameters);
		return configuration;
	}

	@Override
	protected PersistenceStrategy getPersistenceStrategy() {
		SchedulePersistenceStrategy strategy = new SchedulePersistenceStrategy();
		return strategy;
	}

	@Test
	public void testMerge() throws Exception {
		PersistenceStrategy strategy = getPersistenceStrategy();
		strategy.start(accessor, cacheInformations, getPersistenceConfiguration());
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
		Assert.assertThat(manager.getExceptionCount(), is(3L));

		// DELETE, UPDATE(会导致异常)
		manager.updateInstance(MockEntityObject.instanceOf(id, "wolfy" + id, "xiao", id * id, id * id));
		Assert.assertThat(manager.getWaitSize(), is(1));
		Assert.assertThat(manager.getExceptionCount(), is(4L));

		// DELETE, CREATE
		manager.createInstance(MockEntityObject.instanceOf(id, "mickey" + id, "hong", id, id));
		Assert.assertThat(manager.getWaitSize(), is(1));
		Assert.assertThat(manager.getExceptionCount(), is(4L));

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
