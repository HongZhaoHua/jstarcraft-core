package com.jstarcraft.core.distribution.identity;

public class RedisMapIdentityFactoryTestCase extends RedisIdentityFactoryTestCase {

	@Override
	protected IdentityFactory getIdentityFactory() {
		RedisIdentityFactory identityFactory = new RedisMapIdentityFactory(redisson.getMap("redis"), 1000L, 0, 58);
		return identityFactory;
	}

}
