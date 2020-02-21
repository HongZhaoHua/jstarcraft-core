package com.jstarcraft.core.orm.hibernate;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class HibernateMetadataTestCase {

    @Test
    public void test() {
        HibernateMetadata metadata = new HibernateMetadata(MockObject.class);
        Assert.assertThat(metadata.getPrimaryName(), CoreMatchers.equalTo("id"));
        Assert.assertThat(metadata.getIndexNames().size(), CoreMatchers.equalTo(1));
        Assert.assertTrue(metadata.getIndexNames().contains("name"));
        Assert.assertThat(metadata.getVersionName(), CoreMatchers.equalTo("version"));
    }

}
