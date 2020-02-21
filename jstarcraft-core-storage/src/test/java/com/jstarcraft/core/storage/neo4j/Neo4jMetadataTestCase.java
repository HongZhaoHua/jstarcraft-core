package com.jstarcraft.core.storage.neo4j;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.storage.neo4j.Neo4jMetadata;

public class Neo4jMetadataTestCase {

    @Test
    public void test() {
        {
            Neo4jMetadata metadata = new Neo4jMetadata(MockNode.class);
            Assert.assertEquals("MockNode", metadata.getOrmName());
            Assert.assertEquals("id", metadata.getPrimaryName());
            Assert.assertEquals(1, metadata.getIndexNames().size());
            Assert.assertTrue(metadata.getIndexNames().contains("name"));
        }
        {
            Neo4jMetadata metadata = new Neo4jMetadata(MockRelation.class);
            Assert.assertEquals("MockRelation", metadata.getOrmName());
            Assert.assertEquals("id", metadata.getPrimaryName());
            Assert.assertEquals(0, metadata.getIndexNames().size());
            Assert.assertFalse(metadata.getIndexNames().contains("name"));
        }
    }

}
