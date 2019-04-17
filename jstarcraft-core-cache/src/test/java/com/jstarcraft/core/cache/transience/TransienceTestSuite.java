package com.jstarcraft.core.cache.transience;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ WeakElementManagerTestCase.class, DelayedTransienceStrategyTestCase.class, LeastRecentlyUsedTransienceStrategyTestCase.class })
public class TransienceTestSuite {

}
