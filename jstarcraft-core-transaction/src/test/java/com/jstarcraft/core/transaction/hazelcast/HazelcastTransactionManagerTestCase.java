package com.jstarcraft.core.transaction.hazelcast;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jstarcraft.core.transaction.TransactionDefinition;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.TransactionManagerTestCase;
import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.hazelcast.HazelcastTransactionManager;

public class HazelcastTransactionManagerTestCase extends TransactionManagerTestCase {

    private HazelcastInstance hazelcastInstance;

    @Before
    public void testBefore() throws Exception {
        hazelcastInstance = Hazelcast.newHazelcastInstance();
    }

    @After
    public void testAfter() throws Exception {
        hazelcastInstance.shutdown();
    }

    @Override
    protected TransactionManager getDistributionManager() {
        return new HazelcastTransactionManager(hazelcastInstance);
    }

    @Test
    public void testCluster() throws Exception {
        // 测试Hazelcast集群的分布式锁
        HazelcastTransactionManager thisManager = new HazelcastTransactionManager(Hazelcast.newHazelcastInstance());
        HazelcastTransactionManager thatManager = new HazelcastTransactionManager(Hazelcast.newHazelcastInstance());
        Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
        TransactionDefinition definition = new TransactionDefinition(name, most);

        thisManager.lock(definition);
        try {
            thatManager.lock(definition);
            Assert.fail();
        } catch (TransactionLockException exception) {
        }
        thisManager.unlock(definition);
        thatManager.lock(definition);
        try {
            thisManager.lock(definition);
            Assert.fail();
        } catch (TransactionLockException exception) {
        }
        thisManager = new HazelcastTransactionManager(Hazelcast.newHazelcastInstance());
        try {
            thisManager.lock(definition);
            Assert.fail();
        } catch (TransactionLockException exception) {
        }
        thatManager.unlock(definition);
        thisManager.lock(definition);
    }

}
