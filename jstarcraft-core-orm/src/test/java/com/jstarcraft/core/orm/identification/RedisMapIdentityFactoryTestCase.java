package com.jstarcraft.core.orm.identification;

import com.jstarcraft.core.orm.identification.IdentityDefinition;
import com.jstarcraft.core.orm.identification.IdentityFactory;
import com.jstarcraft.core.orm.identification.RedisIdentityFactory;
import com.jstarcraft.core.orm.identification.RedisMapIdentityFactory;

public class RedisMapIdentityFactoryTestCase extends RedisIdentityFactoryTestCase {

    @Override
    protected IdentityFactory getIdentityFactory() {
        IdentityDefinition definition = new IdentityDefinition(5, 58);
        RedisIdentityFactory identityFactory = new RedisMapIdentityFactory(redisson.getMap("redis"), definition, 0, 1000L);
        return identityFactory;
    }

}
