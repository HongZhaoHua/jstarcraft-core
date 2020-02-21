package com.jstarcraft.core.storage.mongo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        // Mongo访问器测试
        MongoAccessorTestCase.class, MongoMetadataTestCase.class })
public class MongoTestSuite {

}
