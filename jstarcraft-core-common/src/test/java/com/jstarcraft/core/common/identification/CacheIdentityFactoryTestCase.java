package com.jstarcraft.core.common.identification;

public class CacheIdentityFactoryTestCase extends IdentityFactoryTestCase {

    @Override
    protected IdentityFactory getIdentityFactory() {
        IdentityDefinition definition = new IdentityDefinition(5, 58);
        CacheIdentityFactory identityFactory = new CacheIdentityFactory(definition, 0, 10L);
        return identityFactory;
    }

}
