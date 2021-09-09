package com.jstarcraft.core.common.bloomfilter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.common.bit.BitMapTestCase;

@RunWith(Suite.class)
@SuiteClasses({

        BitMapTestCase.class,

        BitSetLocalBloomFilterTestCase.class,

        RedissonGlobalBloomFilterTestCase.class,
        
        ByteMapBloomLocalFilterTestCase.class,

        IntegerMapLocalBloomFilterTestCase.class,

        LongMapLocalBloomFilterTestCase.class,

})
public class BloomFilterTestSuite {

}
