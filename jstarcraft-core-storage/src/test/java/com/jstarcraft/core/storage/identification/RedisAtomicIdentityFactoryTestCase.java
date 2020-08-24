package com.jstarcraft.core.storage.identification;

public class RedisAtomicIdentityFactoryTestCase extends RedisIdentityFactoryTestCase {

    @Override
    protected IdentityFactory getIdentityFactory() {
        IdentityDefinition definition = new IdentityDefinition(5, 58);
        RedisIdentityFactory identityFactory = new RedisAtomicIdentityFactory(redisson.getAtomicLong("redis"), definition, 0, 1000L);
        return identityFactory;
    }

}
