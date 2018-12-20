package com.jstarcraft.core.cache;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.cache.annotation.CacheAnnotationTestCase;
import com.jstarcraft.core.cache.aspect.ChainLockTestCase;
import com.jstarcraft.core.cache.crud.CrudTestSuite;
import com.jstarcraft.core.cache.persistence.PersistenceTestSuite;
import com.jstarcraft.core.cache.transience.TransienceTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ CacheAnnotationTestCase.class, CrudTestSuite.class, TransienceTestSuite.class, PersistenceTestSuite.class, ChainLockTestCase.class })
public class CacheTestSuite {

}
