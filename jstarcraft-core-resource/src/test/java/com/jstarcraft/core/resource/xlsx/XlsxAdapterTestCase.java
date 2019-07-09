package com.jstarcraft.core.resource.xlsx;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.resource.Storage;
import com.jstarcraft.core.resource.annotation.StorageAccessor;

/**
 * XLSX适配器测试
 * 
 * @author Birdy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class XlsxAdapterTestCase {

	@StorageAccessor
	private Storage<Integer, Biology> storage;

	@StorageAccessor("1")
	private Biology cat;
	@StorageAccessor("2")
	private Biology dog;
	@StorageAccessor("3")
	private Biology tree;
	@StorageAccessor("4")
	private Biology shrub;

	@StorageAccessor(value = "0", necessary = false)
	private Biology ignore;
	@StorageAccessor(value = "5", necessary = false)
	private Biology unknow;

	@Test
	public void testXlsx() {
		Assert.assertThat(storage.getAll().size(), CoreMatchers.equalTo(4));

		assertThat(cat, CoreMatchers.notNullValue());
		assertThat(cat.getId(), CoreMatchers.is(1));
		assertThat(cat.getName(), CoreMatchers.is("Cat"));

		assertThat(dog, CoreMatchers.notNullValue());
		assertThat(dog.getId(), CoreMatchers.is(2));
		assertThat(dog.getName(), CoreMatchers.is("Dog"));

		assertThat(tree, CoreMatchers.notNullValue());
		assertThat(tree.getId(), CoreMatchers.is(3));
		assertThat(tree.getName(), CoreMatchers.is("Tree"));

		assertThat(shrub, CoreMatchers.notNullValue());
		assertThat(shrub.getId(), CoreMatchers.is(4));
		assertThat(shrub.getName(), CoreMatchers.is("Shrub"));

		assertThat(ignore, CoreMatchers.nullValue());
		assertThat(unknow, CoreMatchers.nullValue());
	}
}
