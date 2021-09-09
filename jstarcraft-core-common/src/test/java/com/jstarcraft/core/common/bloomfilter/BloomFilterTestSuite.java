package com.jstarcraft.core.common.bloomfilter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.common.bit.BitMapTestCase;

@RunWith(Suite.class)
@SuiteClasses({

        BitMapTestCase.class,

        RedissonBloomFilterTestCase.class,

        BitMapBloomLocalFilterTestCase.class,

})
public class BloomFilterTestSuite {

}
