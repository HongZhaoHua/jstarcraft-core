package com.jstarcraft.core.distribution.resource.hazelcast;

import java.time.Instant;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jstarcraft.core.distribution.exception.DistributionLockException;
import com.jstarcraft.core.distribution.exception.DistributionUnlockException;
import com.jstarcraft.core.distribution.resource.ResourceDefinition;
import com.jstarcraft.core.distribution.resource.ResourceManager;

/**
 * Hazelcast分布式管理器
 * 
 * @author Birdy
 *
 */
public class HazelcastResourceManager extends ResourceManager {

	private static final String DEFAULT_STORE = "jstarcraft";

	private final String store;

	private final HazelcastInstance hazelcastInstance;

	public HazelcastResourceManager(HazelcastInstance hazelcastInstance) {
		this(hazelcastInstance, DEFAULT_STORE);
	}

	public HazelcastResourceManager(HazelcastInstance hazelcastInstance, String store) {
		this.hazelcastInstance = hazelcastInstance;
		this.store = store;
	}

	private IMap<String, HazelcastResourceDefinition> getStore() {
		return hazelcastInstance.getMap(store);
	}

	@Override
	protected void lock(ResourceDefinition definition) {
		Instant now = Instant.now();
		String name = definition.getName();
		final IMap<String, HazelcastResourceDefinition> store = getStore();
		try {
			store.lock(name);
			HazelcastResourceDefinition current = store.get(name);
			if (current == null) {
				store.put(name, new HazelcastResourceDefinition(definition));
			} else if (now.isAfter(current.getMost())) {
				store.put(name, new HazelcastResourceDefinition(definition));
			} else {
				throw new DistributionLockException();
			}
		} finally {
			store.unlock(name);
		}
	}

	@Override
	protected void unlock(ResourceDefinition definition) {
		Instant now = Instant.now();
		String name = definition.getName();
		final IMap<String, HazelcastResourceDefinition> store = getStore();
		try {
			store.lock(name);
			HazelcastResourceDefinition current = store.get(name);
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
