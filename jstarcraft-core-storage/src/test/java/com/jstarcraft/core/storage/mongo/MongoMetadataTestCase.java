package com.jstarcraft.core.storage.mongo;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.storage.mongo.MongoMetadata;

public class MongoMetadataTestCase {

    @Test
    public void test() {
        MongoMetadata metadata = new MongoMetadata(MockObject.class);
        try {
            new MongoMetadata(NestObject.class);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }
        Assert.assertEquals("mockObject", metadata.getOrmName());
        Assert.assertEquals("id", metadata.getPrimaryName());
        Assert.assertEquals(1, metadata.getIndexNames().size());
        Assert.assertTrue(metadata.getIndexNames().contains("name"));
        Assert.assertEquals("version", metadata.getVersionName());
    }

}
