package com.jstarcraft.core.distribution.database;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.orm.OrmAccessor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RouteDataSourceTestCase {
	
	@Autowired
	private MockRouteStrategy strategy;

	@Autowired
	private OrmAccessor accessor;

	/**
	 * 测试切换数据源
	 */
	@Test
	public void testSwitch() {
		strategy.setName("leftDataSource");
		MockObject left = MockObject.instanceOf("left");
		accessor.create(MockObject.class, left);
		Assert.assertEquals(1L, accessor.count(MockObject.class));
		
		strategy.setName("rightDataSource");
		MockObject right = MockObject.instanceOf("right");
		accessor.create(MockObject.class, right);
		Assert.assertEquals(1L, accessor.count(MockObject.class));
	}

}
