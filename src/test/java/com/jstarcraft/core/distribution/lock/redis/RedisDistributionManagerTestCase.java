package com.jstarcraft.core.distribution.lock.redis;

import org.junit.After;
import org.junit.Before;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.config.Config;

import com.jstarcraft.core.distribution.lock.DistributionManager;
import com.jstarcraft.core.distribution.lock.DistributionManagerTestCase;

public class RedisDistributionManagerTestCase extends DistributionManagerTestCase {

	private Redisson redisson;
	private RKeys keys;

	@Before
	public void testBefore() {
		Config configuration = new Config();
		configuration.useSingleServer().setAddress("redis://127.0.0.1:6379");
		redisson = (Redisson) Redisson.create(configuration);
		keys = redisson.getKeys();
		keys.flushdb();
	}

	@After
	public void testAfter() {
		keys.flushdb();
		redisson.shutdown();
	}

	@Override
	protected DistributionManager getDistributionManager() {
		return new RedisDistributionManager(redisson.getScript());
	}

}
