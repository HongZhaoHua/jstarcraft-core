package com.jstarcraft.core.orm.neo4j;

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

import com.jstarcraft.core.orm.OrmCondition;
import com.jstarcraft.core.orm.OrmPagination;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class Neo4jAccessorTestCase {

    // 清理所有节点与关系
    private static final String CLEAR = "MATCH (node)-[relation]-() DELETE node, relation";

    @Autowired
    private SessionFactory factory;

    @Autowired
    private Neo4jAccessor accessor;

    @Test
    public void testCRUD() {
        Session template = factory.openSession();
        int size = 100;
        template.query(CLEAR, Collections.EMPTY_MAP);

        for (int index = 0; index < size; index++) {
            // 创建对象并保存
            MockNode object = new MockNode(index, "birdy", index, MockEnumeration.values()[index % MockEnumeration.values().length]);
            int id = accessor.create(MockNode.class, object);
            Assert.assertThat(id, CoreMatchers.equalTo(index));

            // 获取对象并比较
            MockNode instance = accessor.get(MockNode.class, id);
            Assert.assertThat(instance, CoreMatchers.equalTo(object));

            // 修改对象并保存
            object.setName("mickey");
            accessor.update(MockNode.class, object);
            instance = accessor.get(MockNode.class, id);
            Assert.assertThat(instance, CoreMatchers.equalTo(object));
        }

        // 查询对象的最大标识
        int maximum = accessor.maximumIdentity(MockNode.class, -size, size);
        Assert.assertThat(maximum, CoreMatchers.equalTo(size - 1));
        // 查询对象的最小标识
        int minimum = accessor.minimumIdentity(MockNode.class, -size, size);
        Assert.assertThat(minimum, CoreMatchers.equalTo(0));

        // 查询指定范围的主键与对象
        Map<Integer, Object> id2Moneys = accessor.queryIdentities(MockNode.class, OrmCondition.All, "money");
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(size));
        List<MockNode> objects = accessor.queryInstances(MockNode.class, OrmCondition.All, "money");
        for (MockNode object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        id2Moneys = accessor.queryIdentities(MockNode.class, OrmCondition.Equal, "money", 0);
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(1));
        objects = accessor.queryInstances(MockNode.class, OrmCondition.Equal, "money", 0);
        for (MockNode object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        id2Moneys = accessor.queryIdentities(MockNode.class, OrmCondition.Between, "money", 1, 50);
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(50));
        objects = accessor.queryInstances(MockNode.class, OrmCondition.Between, "money", 1, 50);
        for (MockNode object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        id2Moneys = accessor.queryIdentities(MockNode.class, OrmCondition.In, "money", 25, 50, 75);
        Assert.assertThat(id2Moneys.size(), CoreMatchers.equalTo(3));
        objects = accessor.queryInstances(MockNode.class, OrmCondition.In, "money", 25, 50, 75);
        for (MockNode object : objects) {
            Assert.assertThat(object.getMoney(), CoreMatchers.equalTo(id2Moneys.get(object.getId())));
        }

        Map<String, Object> condition = new HashMap<>();
        condition.put("race", MockEnumeration.RANDOM);
        condition.put("id", 3);

        // 查询分页
        OrmPagination pagination = new OrmPagination(1, 15);
        objects = accessor.query(MockNode.class, pagination);
        Assert.assertTrue(objects.size() == 15);
        AtomicInteger times = new AtomicInteger();
        accessor.iterate((object) -> {
            times.incrementAndGet();
        }, MockNode.class, pagination);
        Assert.assertTrue(times.get() == 15);

        pagination = new OrmPagination(7, 15);
        objects = accessor.query(MockNode.class, pagination);
        Assert.assertTrue(objects.size() == 10);
        times.set(0);
        accessor.iterate((object) -> {
            times.incrementAndGet();
        }, MockNode.class, pagination);
        Assert.assertTrue(times.get() == 10);

        // 测试总数
        long count = accessor.count(MockNode.class);
        Assert.assertTrue(count == size);
        objects = accessor.query(MockNode.class, null);
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
            accessor.delete(MockNode.class, object);
            object = accessor.get(MockNode.class, object.getId());
            Assert.assertNull(object);
        }
        for (MockNode object : accessor.queryUnion(MockNode.class, condition, null)) {
            accessor.delete(MockNode.class, object.getId());
            object = accessor.get(MockNode.class, object.getId());
            Assert.assertNull(object);
        }

        template.query(CLEAR, Collections.EMPTY_MAP);
    }

}
