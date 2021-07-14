package com.jstarcraft.core.common.identification;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;

public class ZooKeeperAtomicIdentityFactoryTestCase extends IdentityFactoryTestCase {

    private TestingServer zookeeper;

    private CuratorFramework curator;

    @Before
    public void testBefore() throws Exception {
        zookeeper = new TestingServer();
        curator = CuratorFrameworkFactory.builder()

                .namespace("ZooKeeperAtomicIdentityFactoryTestCase")

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
    protected IdentityFactory getIdentityFactory() {
        RetryPolicy policy = new RetryForever(1000);
        DistributedAtomicLong middleware = new DistributedAtomicLong(curator, "/zookeeper", policy);
        IdentityDefinition definition = new IdentityDefinition(5, 58);
        return new ZooKeeperAtomicIdentityFactory(middleware, definition, 0, 1000);
    }

}
