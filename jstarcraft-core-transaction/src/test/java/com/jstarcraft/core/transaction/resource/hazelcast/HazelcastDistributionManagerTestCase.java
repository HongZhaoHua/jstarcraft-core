package com.jstarcraft.core.transaction.resource.hazelcast;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.resource.ResourceDefinition;
import com.jstarcraft.core.transaction.resource.ResourceManager;
import com.jstarcraft.core.transaction.resource.ResourceManagerTestCase;
import com.jstarcraft.core.transaction.resource.hazelcast.HazelcastResourceManager;

public class HazelcastDistributionManagerTestCase extends ResourceManagerTestCase {

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
	protected ResourceManager getDistributionManager() {
		return new HazelcastResourceManager(hazelcastInstance);
	}

	@Test
	public void testCluster() throws Exception {
		// 测试Hazelcast集群的分布式锁
		HazelcastResourceManager thisManager = new HazelcastResourceManager(Hazelcast.newHazelcastInstance());
		HazelcastResourceManager thatManager = new HazelcastResourceManager(Hazelcast.newHazelcastInstance());
		Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
		ResourceDefinition definition = new ResourceDefinition(name, most);

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
		thisManager = new HazelcastResourceManager(Hazelcast.newHazelcastInstance());
		try {
			thisManager.lock(definition);
			Assert.fail();
		} catch (TransactionLockException exception) {
		}
		thatManager.unlock(definition);
		thisManager.lock(definition);
	}

}
