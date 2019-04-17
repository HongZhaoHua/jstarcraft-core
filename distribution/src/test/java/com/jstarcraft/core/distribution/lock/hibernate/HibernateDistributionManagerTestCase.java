package com.jstarcraft.core.distribution.lock.hibernate;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.distribution.lock.DistributionManager;
import com.jstarcraft.core.distribution.lock.DistributionManagerTestCase;
import com.jstarcraft.core.orm.hibernate.HibernateAccessor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class HibernateDistributionManagerTestCase extends DistributionManagerTestCase {

	@Autowired
	private HibernateAccessor accessor;

	private HibernateDistributionManager manager;

	@Before
	public void testBefore() {
		manager = new HibernateDistributionManager(accessor);
		manager.create(name);
	}

	@After
	public void testAfter() {
		manager.delete(name);
	}

	@Override
	protected DistributionManager getDistributionManager() {
		return manager;
	}

}
