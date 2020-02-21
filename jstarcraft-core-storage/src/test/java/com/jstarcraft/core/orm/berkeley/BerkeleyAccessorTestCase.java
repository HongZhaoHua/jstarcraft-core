package com.jstarcraft.core.orm.berkeley;

import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.orm.OrmCondition;
import com.jstarcraft.core.orm.berkeley.entity.Pack;
import com.jstarcraft.core.orm.berkeley.entity.Person;
import com.jstarcraft.core.orm.berkeley.exception.BerkeleyOperationException;
import com.jstarcraft.core.orm.berkeley.exception.BerkeleyVersionException;
import com.jstarcraft.core.orm.berkeley.persistent.Item;
import com.sleepycat.je.UniqueConstraintException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BerkeleyAccessorTestCase {

    @Autowired
    private BerkeleyAccessor accessor;

    /**
     * 测试增删查改
     */
    @Test
    public void testCRUD() {
        Person birdy = new Person(1L, "Birdy");
        Assert.assertTrue(accessor.create(Person.class, birdy));

        // 标识冲突
        birdy = new Person(1L, "Mickey");
        Assert.assertFalse(accessor.create(Person.class, birdy));

        try {
            // 索引冲突
            birdy = new Person(2L, "Birdy");
            accessor.create(Person.class, birdy);
            Assert.fail();
        } catch (UniqueConstraintException exception) {
        }

        birdy = accessor.get(Person.class, 1L);
        Assert.assertThat(birdy.getName(), CoreMatchers.equalTo("Birdy"));

        int size = 10;
        Item item = new Item(size, size);
        for (long index = 0; index < size; index++) {
            Pack pack = new Pack(index, size, birdy.getId());
            accessor.create(Pack.class, pack);
            Pack newPack = accessor.get(Pack.class, index);
            Pack oldPack = accessor.get(Pack.class, index);
            // 检查版本
            Assert.assertThat(newPack.getVersion(), CoreMatchers.equalTo(0));
            newPack.push(item);
            accessor.update(Pack.class, newPack);
            Assert.assertThat(newPack.getVersion(), CoreMatchers.equalTo(1));

            oldPack.push(item);
            try {
                // 版本冲突
                accessor.update(Pack.class, oldPack);
                Assert.fail();
            } catch (BerkeleyVersionException exception) {
            }
        }

        Collection<Pack> packs = accessor.queryInstances(Pack.class, OrmCondition.Equal, "personId", birdy.getId());
        Assert.assertThat(packs.size(), CoreMatchers.equalTo(size));

        accessor.delete(Person.class, 1L);

        // 由于级联操作,所有Pack的personId会被重置为null
        packs = accessor.queryInstances(Pack.class, OrmCondition.Equal, "personId", birdy.getId());
        Assert.assertTrue(packs.isEmpty());

        Assert.assertTrue(accessor.count(Pack.class) == size);
        for (long index = 0; index < size; index++) {
            accessor.delete(Pack.class, index);
        }

        Assert.assertFalse(accessor.delete(Person.class, 1L));
    }

    private void testAbortTransactor(Pack pack, BerkeleyIsolation isolation) {
        accessor.openTransactor(isolation);
        Assert.assertNotNull(accessor.getTransactor());
        accessor.create(Pack.class, pack);
        pack = accessor.get(Pack.class, 1L);
        Assert.assertThat(pack.getSize(), CoreMatchers.equalTo(10));

        accessor.closeTransactor(true);
        Assert.assertNull(accessor.getTransactor());

        pack = accessor.get(Pack.class, 1L);
        Assert.assertNull(pack);
    }

    private void testCommitTransactor(Pack pack, BerkeleyIsolation isolation) {
        accessor.openTransactor(isolation);
        Assert.assertNotNull(accessor.getTransactor());
        accessor.create(Pack.class, pack);
        pack = accessor.get(Pack.class, 1L);
        Assert.assertThat(pack.getSize(), CoreMatchers.equalTo(10));

        accessor.closeTransactor(false);
        Assert.assertNull(accessor.getTransactor());

        pack = accessor.get(Pack.class, 1L);
        Assert.assertNotNull(pack);
        accessor.delete(Pack.class, 1L);
    }

    /**
     * 测试事务
     */
    @Test
    public void testTransactor() {
        Pack pack = new Pack(1L, 10, null);
        testAbortTransactor(pack, BerkeleyIsolation.READ_UNCOMMITTED);
        testAbortTransactor(pack, BerkeleyIsolation.READ_COMMITTED);
        testCommitTransactor(pack, BerkeleyIsolation.READ_UNCOMMITTED);
        testCommitTransactor(pack, BerkeleyIsolation.READ_COMMITTED);
    }

}
