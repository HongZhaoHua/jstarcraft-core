package com.jstarcraft.core.distribution.lock.hazelcast;

import java.time.Instant;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jstarcraft.core.distribution.exception.DistributionLockException;
import com.jstarcraft.core.distribution.exception.DistributionUnlockException;
import com.jstarcraft.core.distribution.lock.DistributionDefinition;
import com.jstarcraft.core.distribution.lock.DistributionManager;

/**
 * Hazelcast分布式管理器
 * 
 * @author Birdy
 *
 */
public class HazelcastDistributionManager extends DistributionManager {

	private static final String DEFAULT_STORE = "jstarcraft";

	private final String store;

	private final HazelcastInstance hazelcastInstance;

	public HazelcastDistributionManager(HazelcastInstance hazelcastInstance) {
		this(hazelcastInstance, DEFAULT_STORE);
	}

	public HazelcastDistributionManager(HazelcastInstance hazelcastInstance, String store) {
		this.hazelcastInstance = hazelcastInstance;
		this.store = store;
	}

	private IMap<String, HazelcastDistributionDefinition> getStore() {
		return hazelcastInstance.getMap(store);
	}

	@Override
	protected void lock(DistributionDefinition definition) {
		Instant now = Instant.now();
		String name = definition.getName();
		final IMap<String, HazelcastDistributionDefinition> store = getStore();
		try {
			store.lock(name);
			HazelcastDistributionDefinition current = store.get(name);
			if (current == null) {
				store.put(name, new HazelcastDistributionDefinition(definition));
			} else if (now.isAfter(current.getMost())) {
				store.put(name, new HazelcastDistributionDefinition(definition));
			} else {
				throw new DistributionLockException();
			}
		} finally {
			store.unlock(name);
		}
	}

	@Override
	protected void unlock(DistributionDefinition definition) {
		Instant now = Instant.now();
		String name = definition.getName();
		final IMap<String, HazelcastDistributionDefinition> store = getStore();
		try {
			store.lock(name);
			HazelcastDistributionDefinition current = store.get(name);
			if (current == null) {
				throw new DistributionUnlockException();
			} else if (now.isAfter(current.getMost())) {
				throw new DistributionUnlockException();
			} else {
				store.remove(name);
			}
		} finally {
			store.unlock(name);
		}
	}

}
