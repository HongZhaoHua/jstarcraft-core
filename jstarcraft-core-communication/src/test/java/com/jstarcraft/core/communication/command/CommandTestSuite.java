package com.jstarcraft.core.communication.command;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CommandContextTestCase.class, CommandDefinitionTestCase.class, CommandDispatcherTestCase.class })
public class CommandTestSuite {

}
