package com.jstarcraft.core.storage.neo4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        // Neo4j访问器测试
        Neo4jAccessorTestCase.class, Neo4jMetadataTestCase.class })
public class Neo4jTestSuite {

}
