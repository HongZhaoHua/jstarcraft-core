package com.jstarcraft.core.distribution.identity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.jstarcraft.core.distribution.identity.IdentityFactory;
import com.jstarcraft.core.distribution.identity.InstantIdentityFactory;

public class InstantIdentityFactoryTestCase extends IdentityFactoryTestCase {

	@Override
	protected IdentityFactory getIdentityFactory() {
		LocalDateTime dateTime = LocalDateTime.of(2017, 1, 1, 0, 0, 0);
		Instant instant = dateTime.toInstant(ZoneOffset.UTC);
		InstantIdentityFactory identityFactory = new InstantIdentityFactory(5, 0, 40, instant);
		return identityFactory;
	}

}
