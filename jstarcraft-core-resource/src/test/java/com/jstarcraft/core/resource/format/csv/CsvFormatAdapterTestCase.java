package com.jstarcraft.core.resource.format.csv;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.resource.ResourceManager;
import com.jstarcraft.core.resource.ResourceStorage;
import com.jstarcraft.core.resource.annotation.ResourceAccessor;
import com.jstarcraft.core.utility.KeyValue;

/**
 * 仓储注解测试
 * 
 * @author Birdy
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Component
public class CsvFormatAdapterTestCase {

    @Autowired
    private ResourceStorage storage;
    @ResourceAccessor
    private ResourceManager<Integer, Person> manager;
    @ResourceAccessor("2")
    private Person person;
    @ResourceAccessor(value = "2", clazz = Person.class, property = "sex")
    private boolean sex;
    @ResourceAccessor(value = "2", clazz = Person.class, property = "description")
    private String description;

    /**
     * 测试仓储访问器
     */
    @Test
    public void testAssemblage() {
        // 保证@StorageAccessor注解的接口与类型能被自动装配
        Assert.assertThat(manager, CoreMatchers.notNullValue());
        Assert.assertThat(person, CoreMatchers.notNullValue());

        // 检查仓储访问
        Assert.assertThat(manager.getAll().size(), CoreMatchers.equalTo(3));
        Assert.assertThat(manager.getInstance(2, false), CoreMatchers.sameInstance(person));

        // 检查实例访问
        Assert.assertThat(person.isSex(), CoreMatchers.equalTo(sex));
        KeyValue<?, ?> keyValue = new KeyValue<>("key", "value");
        Assert.assertThat(person.getObject(), CoreMatchers.equalTo(keyValue));
        keyValue = new KeyValue<>(1, "1");
        Assert.assertThat(person.getArray()[1], CoreMatchers.equalTo(keyValue));
        Assert.assertThat(person.getMap().get("1"), CoreMatchers.equalTo(keyValue));
        Assert.assertThat(person.getList().get(1), CoreMatchers.equalTo(keyValue));

        // 检查属性访问
        Assert.assertTrue(sex);
        Assert.assertThat(description, CoreMatchers.notNullValue());
    }

    /**
     * 测试仓储索引
     */
    @Test
    public void testIndex() {
        List<Person> ageIndex = manager.getMultiple(Person.INDEX_AGE, 32);
        Assert.assertThat(ageIndex.size(), CoreMatchers.equalTo(2));

        Person birdy = manager.getSingle(Person.INDEX_NAME, "Birdy");
        Assert.assertThat(birdy, CoreMatchers.equalTo(manager.getInstance(1, false)));
    }

}
