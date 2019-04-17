package com.jstarcraft.core.cache.persistence;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ PersistenceElementTestCase.class, PromptPersistenceStrategyTestCase.class, QueuePersistenceStrategyTestCase.class, SchedulePersistenceStrategyTestCase.class })
public class PersistenceTestSuite {

}
