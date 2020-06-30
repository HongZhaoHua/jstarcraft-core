package com.jstarcraft.core.storage.elasticsearch;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        // Elasticsearch访问器测试
        ElasticsearchAccessorTestCase.class, ElasticsearchMetadataTestCase.class })
public class ElasticsearchTestSuite {

}
