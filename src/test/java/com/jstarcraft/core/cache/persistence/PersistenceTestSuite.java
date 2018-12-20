package com.jstarcraft.core.cache.persistence;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ PersistenceElementTestCase.class, PromptPersistenceStrategyTestCase.class, QueuePersistenceStrategyTestCase.class, SchedulePersistenceStrategyTestCase.class })
public class PersistenceTestSuite {

}
