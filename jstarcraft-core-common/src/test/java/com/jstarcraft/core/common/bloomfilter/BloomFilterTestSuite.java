package com.jstarcraft.core.common.bloomfilter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

        BitMapBloomLocalFilterTestCase.class,

        RedissonBloomFilterTestCase.class,

})
public class BloomFilterTestSuite {

}
