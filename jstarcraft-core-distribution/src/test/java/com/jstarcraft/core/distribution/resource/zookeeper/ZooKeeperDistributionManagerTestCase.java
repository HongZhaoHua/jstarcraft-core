package com.jstarcraft.core.distribution.resource.zookeeper;

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

import com.jstarcraft.core.distribution.exception.DistributionUnlockException;
import com.jstarcraft.core.distribution.resource.ResourceManagerTestCase;
import com.jstarcraft.core.distribution.resource.ResourceDefinition;
import com.jstarcraft.core.distribution.resource.ResourceManager;
import com.jstarcraft.core.distribution.resource.zookeeper.ZooKeeperResourceManager;

public class ZooKeeperDistributionManagerTestCase extends ResourceManagerTestCase {

	private TestingServer testZooKeeper;

	private CuratorFramework curator;

	@Before
	public void testBefore() throws Exception {
		testZooKeeper = new TestingServer();
		curator = CuratorFrameworkFactory.builder().namespace("ZooKeeperDistributionManagerTestCase").retryPolicy(new RetryOneTime(2000)).connectString(testZooKeeper.getConnectString()).build();
		curator.start();
	}

	@After
	public void testAfter() throws Exception {
		curator.close();
		testZooKeeper.stop();
	}

	@Override
	protected ResourceManager getDistributionManager() {
		return new ZooKeeperResourceManager(curator);
	}

	@Test
	public void testCuratorStop() throws Exception {
		{
			ZooKeeperResourceManager manager = new ZooKeeperResourceManager(curator);
			Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
			ResourceDefinition definition = new ResourceDefinition(name, most);
			manager.lock(definition);
			Assert.assertNotNull(curator.checkExists().forPath(manager.getNodePath(definition)));
			curator.close();
			try {
				manager.unlock(definition);
				Assert.fail();
			} catch (DistributionUnlockException exception) {
			}
		}

		{
			curator = CuratorFrameworkFactory.builder().namespace("ZooKeeperDistributionManagerTestCase").retryPolicy(new RetryOneTime(2000)).connectString(testZooKeeper.getConnectString()).build();
			curator.start();
			ZooKeeperResourceManager manager = new ZooKeeperResourceManager(curator);
			Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
			ResourceDefinition definition = new ResourceDefinition(name, most);
			Assert.assertNull(curator.checkExists().forPath(manager.getNodePath(definition)));
			manager.lock(definition);
		}
	}

}
