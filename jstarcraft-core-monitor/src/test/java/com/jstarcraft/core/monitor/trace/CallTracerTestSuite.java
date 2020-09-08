package com.jstarcraft.core.monitor.trace;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

        SecurityManagerTracerTestCase.class,

        ThreadStackTracerTestCase.class,

        ThrowableStackTracerTestCase.class })
public class CallTracerTestSuite {

}
