package com.jstarcraft.core.storage.identification;

import com.jstarcraft.core.storage.identification.IdentityDefinition;
import com.jstarcraft.core.storage.identification.IdentityFactory;
import com.jstarcraft.core.storage.identification.RedisIdentityFactory;
import com.jstarcraft.core.storage.identification.RedisMapIdentityFactory;

public class RedisMapIdentityFactoryTestCase extends RedisIdentityFactoryTestCase {

    @Override
    protected IdentityFactory getIdentityFactory() {
        IdentityDefinition definition = new IdentityDefinition(5, 58);
        RedisIdentityFactory identityFactory = new RedisMapIdentityFactory(redisson.getMap("redis"), definition, 0, 1000L);
        return identityFactory;
    }

}
