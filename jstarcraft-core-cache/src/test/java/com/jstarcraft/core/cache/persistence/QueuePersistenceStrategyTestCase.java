package com.jstarcraft.core.cache.persistence;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.cache.MockEntityObject;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy.PersistenceType;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class QueuePersistenceStrategyTestCase extends PersistenceStrategyTestCase {

	@Override
	protected PersistenceConfiguration getPersistenceConfiguration() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put(QueuePersistenceStrategy.PARAMETER_SIZE, "0");
		PersistenceConfiguration configuration = new PersistenceConfiguration("strategy", PersistenceType.QUEUE, parameters);
		return configuration;
	}

	@Override
	protected PersistenceStrategy getPersistenceStrategy() {
		QueuePersistenceStrategy strategy = new QueuePersistenceStrategy();
		return strategy;
	}

	@Test
	public void testInhibit() throws Exception {
		int size = 1000;
		PersistenceStrategy strategy = getPersistenceStrategy();
		strategy.start(accessor, cacheInformations, getPersistenceConfiguration());
		PersistenceManager<Integer, MockEntityObject> manager = strategy.getPersistenceManager(MockEntityObject.class);

		// 创建数据
		long begin = System.currentTimeMillis();
		for (int index = 0; index < size; index++) {
			manager.createInstance(MockEntityObject.instanceOf(index, "birdy" + index, "hong", index, index));
		}
		while (true) {
			if (manager.getWaitSize() == 0) {
				break;
			}
			Thread.sleep(1000);
		}
		long end = System.currentTimeMillis();
		String message = StringUtility.format("创建{}数据的时间:{}毫秒", size, end - begin);
		logger.debug(message);

		// 修改数据
		int times = 10000;
		begin = System.currentTimeMillis();
		for (int index = 0; index < times; index++) {
			int id = RandomUtility.randomInteger(0, 5);
			manager.updateInstance(MockEntityObject.instanceOf(id, "xiao" + index, "xiao", index * index, index * index));
			// 有抑制才需要检查等待数量的大小
			if (manager.getWaitSize() > 5) {
				Assert.fail();
			}
		}
		while (true) {
			if (manager.getWaitSize() == 0) {
				break;
			}
			Thread.sleep(1000);
		}
		end = System.currentTimeMillis();
		message = StringUtility.format("修改{}数据的时间:{}毫秒", times, end - begin);
		logger.debug(message);

		// 删除数据
		begin = System.currentTimeMillis();
		for (int index = 0; index < size; index++) {
			manager.deleteInstance(index);
		}
		while (true) {
			if (manager.getWaitSize() == 0) {
				break;
			}
			Thread.sleep(1000);
		}
		end = System.currentTimeMillis();
		message = StringUtility.format("删除{}数据的时间:{}毫秒", size, end - begin);
		logger.debug(message);

		strategy.stop();
	}

}
