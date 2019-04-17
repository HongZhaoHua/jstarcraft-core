package com.jstarcraft.core.distribution;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.distribution.balance.HashCycleTestCase;
import com.jstarcraft.core.distribution.identity.IdentityTestSuite;
import com.jstarcraft.core.distribution.lock.DistributionManagerTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ HashCycleTestCase.class, IdentityTestSuite.class, DistributionManagerTestSuite.class })
public class DistributionTestSuite {

}
