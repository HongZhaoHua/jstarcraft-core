package com.jstarcraft.core.distribution.identity;

import org.redisson.api.RAtomicLong;

public class RedisAtomicIdentityFactory extends RedisIdentityFactory {

	private RAtomicLong redisson;

	protected RedisAtomicIdentityFactory(RAtomicLong redisson, long step, int partition, int sequenceBit) {
		super(step, partition, sequenceBit);
		this.redisson = redisson;
	}

	@Override
	protected long getLimit(long step) {
		return redisson.addAndGet(step);
	}

}
