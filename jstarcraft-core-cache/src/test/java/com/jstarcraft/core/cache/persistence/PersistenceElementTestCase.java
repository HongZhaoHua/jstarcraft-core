package com.jstarcraft.core.cache.persistence;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.cache.MockEntityObject;
import com.jstarcraft.core.cache.exception.CacheOperationException;
import com.jstarcraft.core.cache.persistence.PersistenceStrategy.PersistenceOperation;
import com.jstarcraft.core.common.identification.IdentityObject;

public class PersistenceElementTestCase {

    @Test
    public void testIgnore() {
        Class<? extends IdentityObject> cacheClass = MockEntityObject.class;
        Integer cacheId = 0;
        MockEntityObject cacheObject = MockEntityObject.instanceOf(cacheId, "birdy", "hong", 100, 100);
        PersistenceElement element = new PersistenceElement(PersistenceOperation.CREATE, cacheId, cacheObject);
        Assert.assertTrue(element.modify(new PersistenceElement(PersistenceOperation.DELETE, cacheId, cacheObject)));
        Assert.assertTrue(element.isIgnore());
        Assert.assertNull(element.getOperation());
        try {
            // 处于忽略状态的元素无法再使用
            element.modify(new PersistenceElement(PersistenceOperation.CREATE, cacheId, cacheObject));
            Assert.fail();
        } catch (CacheOperationException exception) {
        }
    }

    @Test
    public void testCreate() {
        Class<? extends IdentityObject> cacheClass = MockEntityObject.class;
        Integer cacheId = 0;
        MockEntityObject cacheObject = MockEntityObject.instanceOf(cacheId, "birdy", "hong", 100, 100);
        {
            PersistenceElement element = new PersistenceElement(PersistenceOperation.CREATE, cacheId, cacheObject);
            try {
                PersistenceElement create = new PersistenceElement(PersistenceOperation.CREATE, cacheId, cacheObject);
                element.modify(create);
                Assert.fail();
            } catch (CacheOperationException exception) {
            }
            Assert.assertFalse(element.isIgnore());
            Assert.assertThat(element.getOperation(), CoreMatchers.equalTo(PersistenceOperation.CREATE));
        }

        {
            PersistenceElement element = new PersistenceElement(PersistenceOperation.CREATE, cacheId, cacheObject);
            PersistenceElement update = new PersistenceElement(PersistenceOperation.UPDATE, cacheId, cacheObject);
            Assert.assertFalse(element.modify(update));
            Assert.assertFalse(element.isIgnore());
            Assert.assertThat(element.getOperation(), CoreMatchers.equalTo(PersistenceOperation.CREATE));
        }

        {
            PersistenceElement element = new PersistenceElement(PersistenceOperation.CREATE, cacheId, cacheObject);
            PersistenceElement delete = new PersistenceElement(PersistenceOperation.DELETE, cacheId, cacheObject);
            Assert.assertTrue(element.modify(delete));
            Assert.assertTrue(element.isIgnore());
            Assert.assertNull(element.getOperation());
        }
    }

    @Test
    public void testUpdate() {
        Class<? extends IdentityObject> cacheClass = MockEntityObject.class;
        Integer cacheId = 0;
        MockEntityObject cacheObject = MockEntityObject.instanceOf(cacheId, "birdy", "hong", 100, 100);
        {
            PersistenceElement element = new PersistenceElement(PersistenceOperation.UPDATE, cacheId, cacheObject);
            try {
                PersistenceElement create = new PersistenceElement(PersistenceOperation.CREATE, cacheId, cacheObject);
                element.modify(create);
                Assert.fail();
            } catch (CacheOperationException exception) {
            }
            Assert.assertFalse(element.isIgnore());
            Assert.assertThat(element.getOperation(), CoreMatchers.equalTo(PersistenceOperation.UPDATE));
        }

        {
            PersistenceElement element = new PersistenceElement(PersistenceOperation.UPDATE, cacheId, cacheObject);
            PersistenceElement update = new PersistenceElement(PersistenceOperation.UPDATE, cacheId, cacheObject);
            Assert.assertFalse(element.modify(update));
            Assert.assertFalse(element.isIgnore());
            Assert.assertThat(element.getOperation(), CoreMatchers.equalTo(PersistenceOperation.UPDATE));
        }

        {
            PersistenceElement element = new PersistenceElement(PersistenceOperation.UPDATE, cacheId, cacheObject);
            PersistenceElement delete = new PersistenceElement(PersistenceOperation.DELETE, cacheId, cacheObject);
            Assert.assertFalse(element.modify(delete));
            Assert.assertFalse(element.isIgnore());
            Assert.assertThat(element.getOperation(), CoreMatchers.equalTo(PersistenceOperation.DELETE));
        }
    }

    @Test
    public void testDelete() {
        Class<? extends IdentityObject> cacheClass = MockEntityObject.class;
        Integer cacheId = 0;
        MockEntityObject cacheObject = MockEntityObject.instanceOf(cacheId, "birdy", "hong", 100, 100);
        {
            PersistenceElement element = new PersistenceElement(PersistenceOperation.DELETE, cacheId, cacheObject);
            PersistenceElement create = new PersistenceElement(PersistenceOperation.CREATE, cacheId, cacheObject);
            Assert.assertFalse(element.modify(create));
            Assert.assertFalse(element.isIgnore());
            Assert.assertThat(element.getOperation(), CoreMatchers.equalTo(PersistenceOperation.UPDATE));
        }

        {
            PersistenceElement element = new PersistenceElement(PersistenceOperation.DELETE, cacheId, cacheObject);
            try {
                PersistenceElement update = new PersistenceElement(PersistenceOperation.UPDATE, cacheId, cacheObject);
                element.modify(update);
                Assert.fail();
            } catch (CacheOperationException exception) {
            }
            Assert.assertFalse(element.isIgnore());
            Assert.assertThat(element.getOperation(), CoreMatchers.equalTo(PersistenceOperation.DELETE));
        }

        {
            PersistenceElement element = new PersistenceElement(PersistenceOperation.DELETE, cacheId, cacheObject);
            PersistenceElement delete = new PersistenceElement(PersistenceOperation.DELETE, cacheId, cacheObject);
            element.modify(delete);
            Assert.assertFalse(element.isIgnore());
            Assert.assertThat(element.getOperation(), CoreMatchers.equalTo(PersistenceOperation.DELETE));
        }
    }

}
