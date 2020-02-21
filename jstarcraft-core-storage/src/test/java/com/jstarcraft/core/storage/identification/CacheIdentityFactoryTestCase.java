package com.jstarcraft.core.storage.identification;

import com.jstarcraft.core.storage.identification.CacheIdentityFactory;
import com.jstarcraft.core.storage.identification.IdentityDefinition;
import com.jstarcraft.core.storage.identification.IdentityFactory;

public class CacheIdentityFactoryTestCase extends IdentityFactoryTestCase {

    @Override
    protected IdentityFactory getIdentityFactory() {
        IdentityDefinition definition = new IdentityDefinition(5, 58);
        CacheIdentityFactory identityFactory = new CacheIdentityFactory(definition, 0, 10L);
        return identityFactory;
    }

}
