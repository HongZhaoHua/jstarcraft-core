package com.jstarcraft.core.resource.format.excel;

import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.resource.ResourceManager;
import com.jstarcraft.core.resource.annotation.ResourceAccessor;

/**
 * XLS适配器测试
 * 
 * @author Birdy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ExcelFormatAdapterTestCase {

    @ResourceAccessor
    private ResourceManager<Integer, Biology> manager;

    @ResourceAccessor("1")
    private Biology cat;
    @ResourceAccessor("2")
    private Biology dog;
    @ResourceAccessor("3")
    private Biology tree;
    @ResourceAccessor("4")
    private Biology shrub;

    @Test
    public void testExcel() {
        Assert.assertThat(manager.getAll().size(), CoreMatchers.equalTo(4));

        assertThat(cat, CoreMatchers.notNullValue());
        assertThat(cat.getId(), CoreMatchers.is(1));
        assertThat(cat.getName(), CoreMatchers.is("Cat"));
        assertThat(cat.getDescription()[1], CoreMatchers.notNullValue());

        assertThat(dog, CoreMatchers.notNullValue());
        assertThat(dog.getId(), CoreMatchers.is(2));
        assertThat(dog.getName(), CoreMatchers.is("Dog"));
        assertThat(dog.getDescription()[1], CoreMatchers.nullValue());

        assertThat(tree, CoreMatchers.notNullValue());
        assertThat(tree.getId(), CoreMatchers.is(3));
        assertThat(tree.getName(), CoreMatchers.is("Tree"));
        assertThat(tree.getDescription(), CoreMatchers.nullValue());

        assertThat(shrub, CoreMatchers.notNullValue());
        assertThat(shrub.getId(), CoreMatchers.is(4));
        assertThat(shrub.getName(), CoreMatchers.is("Shrub"));
        assertThat(shrub.getDescription(), CoreMatchers.nullValue());
    }
}
