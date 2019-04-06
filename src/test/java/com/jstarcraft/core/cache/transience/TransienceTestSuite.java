package com.jstarcraft.core.cache.transience;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ WeakElementManagerTestCase.class, DelayedTransienceStrategyTestCase.class, LeastRecentlyUsedTransienceStrategyTestCase.class })
public class TransienceTestSuite {

}
