package com.jstarcraft.core.distribution.identity;

import org.junit.After;
import org.junit.Before;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.config.Config;

import com.jstarcraft.core.distribution.identity.IdentityFactory;
import com.jstarcraft.core.distribution.identity.RedisIdentityFactory;

public class RedisIdentityFactoryTestCase extends IdentityFactoryTestCase {

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
	protected IdentityFactory getIdentityFactory() {
		RedisIdentityFactory identityFactory = new RedisIdentityFactory(redisson.getAtomicLong("atomic"), 1000L, 0, 58);
		return identityFactory;
	}

}
