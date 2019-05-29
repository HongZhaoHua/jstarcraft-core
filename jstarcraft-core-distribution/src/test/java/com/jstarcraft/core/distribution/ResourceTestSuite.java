package com.jstarcraft.core.distribution;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.distribution.balance.HashCycleTestCase;
import com.jstarcraft.core.distribution.resource.ResourceManagerTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ HashCycleTestCase.class, ResourceManagerTestSuite.class })
public class ResourceTestSuite {

}
