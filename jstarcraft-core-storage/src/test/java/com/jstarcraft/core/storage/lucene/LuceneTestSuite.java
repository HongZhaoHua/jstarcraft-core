package com.jstarcraft.core.storage.lucene;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

        // Lucene引擎测试
        LuceneEngineTestCase.class,

        // Lucene访问器测试
        LuceneAccessorTestCase.class, LuceneMetadataTestCase.class, 
        
        // Lucene索引与检索测试
        LuceneIndexTestCase.class, LuceneQueryTestCase.class })
public class LuceneTestSuite {

}
