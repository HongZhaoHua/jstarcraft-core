package com.jstarcraft.core.orm.mongo;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class MongoMetadataTestCase {

    @Test
    public void test() {
        MongoMetadata metadata = new MongoMetadata(MockObject.class);
        try {
            new MongoMetadata(NestObject.class);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }
        Assert.assertThat(metadata.getPrimaryName(), CoreMatchers.equalTo("id"));
        Assert.assertThat(metadata.getIndexNames().size(), CoreMatchers.equalTo(1));
        Assert.assertTrue(metadata.getIndexNames().contains("name"));
        Assert.assertThat(metadata.getVersionName(), CoreMatchers.equalTo("version"));
    }

}
