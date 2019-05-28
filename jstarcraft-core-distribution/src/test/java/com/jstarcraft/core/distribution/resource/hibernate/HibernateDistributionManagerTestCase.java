package com.jstarcraft.core.distribution.resource.hibernate;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.distribution.resource.ResourceManagerTestCase;
import com.jstarcraft.core.distribution.resource.ResourceManager;
import com.jstarcraft.core.distribution.resource.hibernate.HibernateResourceManager;
import com.jstarcraft.core.orm.hibernate.HibernateAccessor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class HibernateDistributionManagerTestCase extends ResourceManagerTestCase {

	@Autowired
	private HibernateAccessor accessor;

	private HibernateResourceManager manager;

	@Before
	public void testBefore() {
		manager = new HibernateResourceManager(accessor);
		manager.create(name);
	}

	@After
	public void testAfter() {
		manager.delete(name);
	}

	@Override
	protected ResourceManager getDistributionManager() {
		return manager;
	}

}
