package com.jstarcraft.core.transaction;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.transaction.balance.HashCycleTestCase;
import com.jstarcraft.core.transaction.resource.ResourceManagerTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ HashCycleTestCase.class, ResourceManagerTestSuite.class })
public class ResourceTestSuite {

}
