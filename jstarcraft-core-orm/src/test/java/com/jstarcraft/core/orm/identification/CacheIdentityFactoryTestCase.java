package com.jstarcraft.core.orm.identification;

import com.jstarcraft.core.orm.identification.CacheIdentityFactory;
import com.jstarcraft.core.orm.identification.IdentityDefinition;
import com.jstarcraft.core.orm.identification.IdentityFactory;

public class CacheIdentityFactoryTestCase extends IdentityFactoryTestCase {

	@Override
	protected IdentityFactory getIdentityFactory() {
		IdentityDefinition definition = new IdentityDefinition(5, 58);
		CacheIdentityFactory identityFactory = new CacheIdentityFactory(definition, 0, 10L);
		return identityFactory;
	}

}
