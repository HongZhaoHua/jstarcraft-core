package com.jstarcraft.core.common.identification;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class InstantIdentityFactoryTestCase extends IdentityFactoryTestCase {

    @Override
    protected IdentityFactory getIdentityFactory() {
        IdentityDefinition definition = new IdentityDefinition(5, 40, 18);
        LocalDateTime dateTime = LocalDateTime.of(2017, 1, 1, 0, 0, 0);
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);
        InstantIdentityFactory identityFactory = new InstantIdentityFactory(definition, 0, instant);
        return identityFactory;
    }

}
