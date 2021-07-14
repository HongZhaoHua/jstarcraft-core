package com.jstarcraft.core.common.identification;

public class RedisMapIdentityFactoryTestCase extends RedisIdentityFactoryTestCase {

    @Override
    protected IdentityFactory getIdentityFactory() {
        IdentityDefinition definition = new IdentityDefinition(5, 58);
        MiddlewareIdentityFactory identityFactory = new RedisMapIdentityFactory(redisson.getMap("redis"), definition, 0, 1000L);
        return identityFactory;
    }

}
