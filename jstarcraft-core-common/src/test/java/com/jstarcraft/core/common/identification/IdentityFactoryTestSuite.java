package com.jstarcraft.core.common.identification;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CacheIdentityFactoryTestCase.class, InstantIdentityFactoryTestCase.class, RedisAtomicIdentityFactoryTestCase.class, RedisMapIdentityFactoryTestCase.class, ZooKeeperAtomicIdentityFactoryTestCase.class })
public class IdentityFactoryTestSuite {

}
