package com.jstarcraft.core.transaction.zookeeper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jstarcraft.core.transaction.TransactionDefinition;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.TransactionManagerTestCase;
import com.jstarcraft.core.transaction.exception.TransactionUnlockException;

public class ZooKeeperTransactionManagerTestCase extends TransactionManagerTestCase {

    private TestingServer zookeeper;

    private CuratorFramework curator;

    @Before
    public void testBefore() throws Exception {
        zookeeper = new TestingServer();
        curator = CuratorFrameworkFactory.builder()

                .namespace("ZooKeeperDistributionManagerTestCase")

                .connectString(zookeeper.getConnectString())

                .retryPolicy(new RetryOneTime(2000))

                .build();
        curator.start();
    }

    @After
    public void testAfter() throws Exception {
        curator.close();
        zookeeper.stop();
    }

    @Override
    protected TransactionManager getDistributionManager() {
        return new ZooKeeperTransactionManager(curator);
    }

    @Test
    public void testCuratorStop() throws Exception {
        {
            ZooKeeperTransactionManager manager = new ZooKeeperTransactionManager(curator);
            Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
            TransactionDefinition definition = new TransactionDefinition(name, most);
            manager.lock(definition);
            Assert.assertNotNull(curator.checkExists().forPath(manager.getNodePath(definition)));
            curator.close();
            try {
                manager.unlock(definition);
                Assert.fail();
            } catch (TransactionUnlockException exception) {
            }
        }

        {
            curator = CuratorFrameworkFactory.builder().namespace("ZooKeeperDistributionManagerTestCase").retryPolicy(new RetryOneTime(2000)).connectString(zookeeper.getConnectString()).build();
            curator.start();
            ZooKeeperTransactionManager manager = new ZooKeeperTransactionManager(curator);
            Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
            TransactionDefinition definition = new TransactionDefinition(name, most);
            Assert.assertNull(curator.checkExists().forPath(manager.getNodePath(definition)));
            manager.lock(definition);
        }
    }

}
