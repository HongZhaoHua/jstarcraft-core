package com.jstarcraft.core.storage.berkeley;

import static com.sleepycat.persist.model.Relationship.ONE_TO_ONE;

import java.util.HashSet;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.storage.berkeley.BerkeleyMetadata;
import com.jstarcraft.core.storage.berkeley.annotation.BerkeleyConfiguration;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

public class BerkeleyMetadataTestCase {

    @Test
    public void testIllegal() {
        try {
            // 缺少@BerkeleyConfiguration注解
            @Entity
            class Illegal {
                @PrimaryKey
                private Long id;
            }
            BerkeleyMetadata.instanceOf(Illegal.class);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }

        try {
            // 缺少@Entity注解
            @BerkeleyConfiguration(store = "illegal")
            class Illegal {
                @PrimaryKey
                private Long id;
            }
            BerkeleyMetadata.instanceOf(Illegal.class);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }

        try {
            // 缺少@PrimaryKey注解
            @BerkeleyConfiguration(store = "illegal")
            @Entity
            class Illegal {
                private Long id;
            }
            BerkeleyMetadata.instanceOf(Illegal.class);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }
        try {
            // 多余@PrimaryKey注解
            @BerkeleyConfiguration(store = "illegal")
            @Entity
            class Illegal {
                @PrimaryKey
                private Long id;
                @PrimaryKey
                private String name;
            }
            BerkeleyMetadata.instanceOf(Illegal.class);
            Assert.fail();
        } catch (IllegalArgumentException exception) {
        }

    }

    @Test
    public void testLegal() {
        @Persistent
        class SuperClass {

            @SecondaryKey(relate = ONE_TO_ONE)
            protected String name;

        }

        @BerkeleyConfiguration(store = "legal", version = "version")
        @Entity
        class SubClass extends SuperClass {

            @PrimaryKey(sequence = "ID")
            private Long id;

            private HashSet<SuperClass> list;

            private int version;

        }
        BerkeleyMetadata metadata = BerkeleyMetadata.instanceOf(SubClass.class);
        Assert.assertThat(metadata.getOrmClass(), CoreMatchers.equalTo(SubClass.class));
        Assert.assertThat(metadata.getPrimaryName(), CoreMatchers.equalTo("id"));
        Assert.assertThat(metadata.getIndexNames().size(), CoreMatchers.equalTo(1));
        Assert.assertTrue(metadata.getIndexNames().contains("name"));
        Assert.assertThat(metadata.getStoreClass(), CoreMatchers.equalTo(SubClass.class));
        Assert.assertThat(metadata.getStoreName(), CoreMatchers.equalTo("legal"));
        Assert.assertThat(metadata.getVersionName(), CoreMatchers.equalTo("version"));
    }

}
