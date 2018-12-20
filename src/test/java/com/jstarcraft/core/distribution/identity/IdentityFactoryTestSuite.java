package com.jstarcraft.core.distribution.identity;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CacheIdentityFactoryTestCase.class, InstantIdentityFactoryTestCase.class, RedisIdentityFactoryTestCase.class })
public class IdentityFactoryTestSuite {

}
