package com.jstarcraft.core.resource.format.csv;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CsvFormatAdapterTestCase {

    @Autowired
    private ResourceStorage storage;
    @ResourceAccessor
    private ResourceManager<Integer, CsvPerson> manager;
    @ResourceAccessor("2")
    private CsvPerson person;
    @ResourceAccessor(value = "2", clazz = CsvPerson.class, property = "sex")
    private boolean sex;

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

        // 检查属性访问
        Assert.assertTrue(sex);
    }

    /**
     * 测试仓储索引
     */
    @Test
    public void testIndex() {
        List<CsvPerson> ageIndex = manager.getMultiple(CsvPerson.INDEX_AGE, 32);
        Assert.assertThat(ageIndex.size(), CoreMatchers.equalTo(2));

        CsvPerson birdy = manager.getSingle(CsvPerson.INDEX_NAME, "Birdy");
        Assert.assertThat(birdy, CoreMatchers.equalTo(manager.getInstance(1, false)));
    }

}
