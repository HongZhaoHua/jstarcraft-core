package com.jstarcraft.core.resource.annotation;

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

/**
 * 仓储注解测试
 * 
 * @author Birdy
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Component
public class ResourceAccessorTestCase {

    @Autowired
    private ResourceStorage chineseStorage;
    @ResourceAccessor(storage = "chineseStorage")
    private ResourceManager<Integer, Person> chineseManager;
    @ResourceAccessor(storage = "chineseStorage", value = "0")
    private Person chinesePerson;

    @Autowired
    private ResourceStorage englishStorage;
    @ResourceAccessor(storage = "englishStorage")
    private ResourceManager<Integer, Person> englishManager;
    @ResourceAccessor(storage = "englishStorage", value = "0")
    private Person englishPerson;

    /**
     * 测试仓储访问器
     */
    @Test
    public void testAssemblage() {
        // 保证@StorageAccessor注解的接口与类型能被自动装配
        Assert.assertThat(chineseStorage, CoreMatchers.notNullValue());
        Assert.assertThat(englishStorage, CoreMatchers.notNullValue());
        Assert.assertThat(chineseManager, CoreMatchers.notNullValue());
        Assert.assertThat(englishManager, CoreMatchers.notNullValue());
        Assert.assertThat(chinesePerson, CoreMatchers.notNullValue());
        Assert.assertThat(englishPerson, CoreMatchers.notNullValue());

        // 检查仓储访问
        Assert.assertThat(chineseManager.getAll().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(englishManager.getAll().size(), CoreMatchers.equalTo(1));

        // 检查实例访问
        Assert.assertThat(chinesePerson.getId(), CoreMatchers.equalTo(0));
        Assert.assertThat(englishPerson.getId(), CoreMatchers.equalTo(0));
        Assert.assertThat(chinesePerson.getName(), CoreMatchers.equalTo("Chinese"));
        Assert.assertThat(englishPerson.getName(), CoreMatchers.equalTo("English"));
    }

}
