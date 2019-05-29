package com.jstarcraft.core.orm.identification;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CacheIdentityFactoryTestCase.class, InstantIdentityFactoryTestCase.class, RedisAtomicIdentityFactoryTestCase.class, RedisMapIdentityFactoryTestCase.class })
public class IdentityFactoryTestSuite {

}
