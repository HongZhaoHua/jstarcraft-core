package com.jstarcraft.core.distribution.identity;

import org.redisson.api.RMap;

public class RedisMapIdentityFactory extends RedisIdentityFactory {

	private RMap<Integer, Number> redisson;

	protected RedisMapIdentityFactory(RMap<Integer, Number> redisson, long step, int partition, int sequenceBit) {
		super(step, partition, sequenceBit);
		this.redisson = redisson;
	}

	@Override
	protected long getLimit(long step) {
		return redisson.addAndGet(partition, step).longValue();
	}

}
