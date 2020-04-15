package com.jstarcraft.core.storage.neo4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.storage.StorageCondition;
import com.jstarcraft.core.storage.StoragePagination;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class Neo4jAccessorTestCase {

    // 清理所有节点与关系
    private static final String CLEAR = "MATCH (node) OPTIONAL MATCH (node)-[relation]-() DELETE node, relation";

    @Autowired
    private SessionFactory factory;

    @Autowired
    private Neo4jAccessor accessor;

    @Test
    public void testGraph() {
        MockNode from = new MockNode(-1000, "from", -1000, MockEnumeration.RANDOM);
        MockNode to = new MockNode(1000, "to", 1000, MockEnumeration.RANDOM);
        MockRelation relation = new MockRelation("relation", from, to);

        accessor.createInstance(MockNode.class, from);
        accessor.createInstance(MockNode.class, to);
        accessor.createInstance(MockRelation.class, relation);
        relation = accessor.getInstance(MockRelation.class, relation.getId());
        Assert.assertNotNull(relation);

        // 无法删除节点(受关系限制)
        Assert.assertFalse(accessor.deleteInstance(MockNode.class, from.getId()));

        // 可以删除节点(将关系删除)
        Assert.assertTrue(accessor.deleteInstance(MockNode.class, to));
        relation = accessor.getInstance(MockRelation.class, relation.getId());
        Assert.assertNull(relation);

        from = accessor.getInstance(MockNode.class, from.getId());
        Assert.assertNotNull(from);
    }

    @Test
    public void testCRUD() {
        Session template = factory.openSession();
        int size = 100;
        template.query(CLEAR, Collections.EMPTY_MAP);
        template.clear();

        for (int index = 0; index < size; index++) {
            // 创建对象并保存
            MockNode object = new MockNode(index, "birdy", index, MockEnumeration.values()[index % MockEnumeration.values().length]);
            accessor.createInstance(MockNode.class, object);
            int id = object.getId();
            Assert.assertThat(id, CoreMatchers.equalTo(index));

            // 获取对象并比较
            MockNode instance = accessor.getInstance(MockNode.class, id);
            Assert.assertThat(instance, CoreMatchers.equalTo(object));

            // 修改对象并保存
            object.setName("mickey");
            accessor.updateInstance(MockNode.class, object);
            instance = accessor.getInstance(MockNode.class, id);
            Assert.assertThat(instance, CoreMatchers.equalTo(object));
        }

        // 查询对象的最大标识
        int maximum = accessor.maximumIdentity(MockNode.class, -size, size);
        Assert.assertThat(maximum, CoreMatchers.equalTo(size - 1));
        // 查询对象的最小标识
        int minimum = accessor.minimumIdentity(MockNode.class, -size, size);
        Assert.assertThat(minimum, CoreMatchers.equalTo(0));

        // 查询指定范围的主键与对象
        Map<Integer, Object> id2Moneys = accessor.queryIdentities(MockNode.class, StorageCondition.All, "money");
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(size));
        List<MockNode> objects = accessor.queryInstances(MockNode.class, StorageCondition.All, "money");
        for (MockNode object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        id2Moneys = accessor.queryIdentities(MockNode.class, StorageCondition.Equal, "money", 0);
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(1));
        objects = accessor.queryInstances(MockNode.class, StorageCondition.Equal, "money", 0);
        for (MockNode object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        id2Moneys = accessor.queryIdentities(MockNode.class, StorageCondition.Between, "money", 1, 50);
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(50));
        objects = accessor.queryInstances(MockNode.class, StorageCondition.Between, "money", 1, 50);
        for (MockNode object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        id2Moneys = accessor.queryIdentities(MockNode.class, StorageCondition.In, "money", 25, 50, 75);
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(3));
        objects = accessor.queryInstances(MockNode.class, StorageCondition.In, "money", 25, 50, 75);
        for (MockNode object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        Map<String, Object> condition = new HashMap<>();
        condition.put("race", MockEnumeration.RANDOM);
        condition.put("id", 3);

        // 查询分页
        StoragePagination pagination = new StoragePagination(1, 15);
        objects = accessor.queryInstances(MockNode.class, pagination);
        Assert.assertTrue(objects.size() == 15);
        AtomicInteger times = new AtomicInteger();
        accessor.iterate((object) -> {
            times.incrementAndGet();
        }, MockNode.class, pagination);
        Assert.assertTrue(times.get() == 15);

        pagination = new StoragePagination(7, 15);
        objects = accessor.queryInstances(MockNode.class, pagination);
        Assert.assertTrue(objects.size() == 10);
        times.set(0);
        accessor.iterate((object) -> {
            times.incrementAndGet();
        }, MockNode.class, pagination);
        Assert.assertTrue(times.get() == 10);

        // 测试总数
        long count = accessor.countInstances(MockNode.class);
        Assert.assertTrue(count == size);
        objects = accessor.queryInstances(MockNode.class, null);
        Assert.assertTrue(objects.size() == count);

        count = accessor.countIntersection(MockNode.class, condition);
        Assert.assertTrue(count == 1);
        objects = accessor.queryIntersection(MockNode.class, condition, null);
        Assert.assertTrue(objects.size() == count);

        count = accessor.countUnion(MockNode.class, condition);
        Assert.assertTrue(count == 25);
        objects = accessor.queryUnion(MockNode.class, condition, null);
        Assert.assertTrue(objects.size() == 25);

        // 删除对象
        for (MockNode object : accessor.queryIntersection(MockNode.class, condition, null)) {
            accessor.deleteInstance(MockNode.class, object);
            object = accessor.getInstance(MockNode.class, object.getId());
            Assert.assertNull(object);
        }
        for (MockNode object : accessor.queryUnion(MockNode.class, condition, null)) {
            accessor.deleteInstance(MockNode.class, object.getId());
            object = accessor.getInstance(MockNode.class, object.getId());
            Assert.assertNull(object);
        }

        template.query(CLEAR, Collections.EMPTY_MAP);
        template.clear();
    }

}
