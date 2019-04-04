package com.jstarcraft.core.distribution.identity;

public class RedisAtomicIdentityFactoryTestCase extends RedisIdentityFactoryTestCase {

	@Override
	protected IdentityFactory getIdentityFactory() {
		RedisIdentityFactory identityFactory = new RedisAtomicIdentityFactory(redisson.getAtomicLong("redis"), 1000L, 0, 58);
		return identityFactory;
	}

}
