package com.jstarcraft.core.storage.elasticsearch;

import org.junit.Assert;
import org.junit.Test;

public class ElasticsearchMetadataTestCase {

    @Test
    public void test() {
        ElasticsearchMetadata metadata = new ElasticsearchMetadata(MockObject.class);
        try {
            new ElasticsearchMetadata(NestObject.class);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }
        Assert.assertEquals("elasticsearch", metadata.getOrmName());
        Assert.assertEquals("id", metadata.getPrimaryName());
        Assert.assertEquals(3, metadata.getIndexNames().size());
        Assert.assertTrue(metadata.getIndexNames().contains("name"));
        Assert.assertEquals("version", metadata.getVersionName());
    }

}
