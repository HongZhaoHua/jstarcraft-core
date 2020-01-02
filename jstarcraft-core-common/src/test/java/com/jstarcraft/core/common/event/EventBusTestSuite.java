package com.jstarcraft.core.common.event;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AsynchronousEventBusTestCase.class, SynchronousEventBusTestCase.class, GlobalEventBusTestCase.class })
public class EventBusTestSuite {

}
