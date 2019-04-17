package com.jstarcraft.core.distribution.lock.hazelcast;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jstarcraft.core.distribution.exception.DistributionLockException;
import com.jstarcraft.core.distribution.lock.DistributionDefinition;
import com.jstarcraft.core.distribution.lock.DistributionManager;
import com.jstarcraft.core.distribution.lock.DistributionManagerTestCase;

public class HazelcastDistributionManagerTestCase extends DistributionManagerTestCase {

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
	protected DistributionManager getDistributionManager() {
		return new HazelcastDistributionManager(hazelcastInstance);
	}

	@Test
	public void testCluster() throws Exception {
		// 测试Hazelcast集群的分布式锁
		HazelcastDistributionManager thisManager = new HazelcastDistributionManager(Hazelcast.newHazelcastInstance());
		HazelcastDistributionManager thatManager = new HazelcastDistributionManager(Hazelcast.newHazelcastInstance());
		Instant most = Instant.now().plus(10, ChronoUnit.SECONDS);
		DistributionDefinition definition = new DistributionDefinition(name, most);

		thisManager.lock(definition);
		try {
			thatManager.lock(definition);
			Assert.fail();
		} catch (DistributionLockException exception) {
		}
		thisManager.unlock(definition);
		thatManager.lock(definition);
		try {
			thisManager.lock(definition);
			Assert.fail();
		} catch (DistributionLockException exception) {
		}
		thisManager = new HazelcastDistributionManager(Hazelcast.newHazelcastInstance());
		try {
			thisManager.lock(definition);
			Assert.fail();
		} catch (DistributionLockException exception) {
		}
		thatManager.unlock(definition);
		thisManager.lock(definition);
	}

}
