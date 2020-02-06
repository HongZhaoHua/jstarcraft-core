package com.jstarcraft.core.orm.lucene;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.orm.lucene.converter.LuceneContext;

public class LuceneMetadataTestCase {

    @Test
    public void test() {
        LuceneContext context = new LuceneContext(CodecDefinition.instanceOf(MockSimpleObject.class, MockComplexObject.class));
        try {
            new LuceneMetadata(MockSimpleObject.class, context);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }
        LuceneMetadata metadata = new LuceneMetadata(MockComplexObject.class, context);
        Assert.assertEquals(MockComplexObject.class.getName(), metadata.getOrmName());
        Assert.assertEquals("id", metadata.getPrimaryName());
        Assert.assertEquals(11, metadata.getIndexNames().size());
        Assert.assertTrue(metadata.getIndexNames().contains("firstName"));
        Assert.assertTrue(metadata.getIndexNames().contains("lastName"));
        Assert.assertEquals(LuceneMetadata.LUCENE_VERSION, metadata.getVersionName());
    }

}
