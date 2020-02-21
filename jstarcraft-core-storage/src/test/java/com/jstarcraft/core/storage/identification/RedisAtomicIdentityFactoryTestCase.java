package com.jstarcraft.core.storage.identification;

import com.jstarcraft.core.storage.identification.IdentityDefinition;
import com.jstarcraft.core.storage.identification.IdentityFactory;
import com.jstarcraft.core.storage.identification.RedisAtomicIdentityFactory;
import com.jstarcraft.core.storage.identification.RedisIdentityFactory;

public class RedisAtomicIdentityFactoryTestCase extends RedisIdentityFactoryTestCase {

    @Override
    protected IdentityFactory getIdentityFactory() {
        IdentityDefinition definition = new IdentityDefinition(5, 58);
        RedisIdentityFactory identityFactory = new RedisAtomicIdentityFactory(redisson.getAtomicLong("redis"), definition, 0, 1000L);
        return identityFactory;
    }

}
