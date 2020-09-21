package com.jstarcraft.core.common.instant;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

        CronExpressionTestCase.class,

        IntervalExpressionTestCase.class,

        LunarExpressionTestCase.class,

        LunisolarExpressionTestCase.class,

        SolarExpressionTestCase.class,

        TermExpressionTestCase.class })
public class DateTimeExpressionTestSuite {

}
