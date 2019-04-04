package com.jstarcraft.core.distribution.identity;

import org.junit.After;
import org.junit.Before;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.config.Config;

public abstract class RedisIdentityFactoryTestCase extends IdentityFactoryTestCase {

	protected Redisson redisson;

	protected RKeys keys;

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

}
