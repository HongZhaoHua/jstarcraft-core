package com.jstarcraft.core.distribution.lock.zookeeper;

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
import com.jstarcraft.core.distribution.lock.DistributionDefinition;
import com.jstarcraft.core.distribution.lock.DistributionManager;
import com.jstarcraft.core.distribution.lock.DistributionManagerTestCase;

public class ZooKeeperDistributionManagerTestCase extends DistributionManagerTestCase {

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
	protected DistributionManager getDistributionManager() {
		return new ZooKeeperDistributionManager(curator);
	}

	@Test
	public void testCuratorStop() throws Exception {
		{
			ZooKeeperDistributionManager manager = new ZooKeeperDistributionManager(curator);
			Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
			DistributionDefinition definition = new DistributionDefinition(name, most);
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
			ZooKeeperDistributionManager manager = new ZooKeeperDistributionManager(curator);
			Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
			DistributionDefinition definition = new DistributionDefinition(name, most);
			Assert.assertNull(curator.checkExists().forPath(manager.getNodePath(definition)));
			manager.lock(definition);
		}
	}

}
