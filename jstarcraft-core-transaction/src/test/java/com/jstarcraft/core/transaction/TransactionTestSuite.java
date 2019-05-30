package com.jstarcraft.core.transaction;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.transaction.balance.HashCycleTestCase;

@RunWith(Suite.class)
@SuiteClasses({ HashCycleTestCase.class, TransactionManagerTestSuite.class })
public class TransactionTestSuite {

}
