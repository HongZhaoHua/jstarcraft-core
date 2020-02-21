package com.jstarcraft.core.transaction.hibernate;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.storage.hibernate.HibernateAccessor;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.TransactionManagerTestCase;
import com.jstarcraft.core.transaction.hibernate.HibernateTransactionManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class HibernateTransactionManagerTestCase extends TransactionManagerTestCase {

    @Autowired
    private HibernateAccessor accessor;

    private HibernateTransactionManager manager;

    @Before
    public void testBefore() {
        manager = new HibernateTransactionManager(accessor);
        manager.create(name);
    }

    @After
    public void testAfter() {
        manager.delete(name);
    }

    @Override
    protected TransactionManager getDistributionManager() {
        return manager;
    }

}
