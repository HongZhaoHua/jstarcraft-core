package com.jstarcraft.core.storage.identification;

import org.redisson.api.RMap;

public class RedisMapIdentityFactory extends RedisIdentityFactory {

    private RMap<Integer, Number> redisson;

    public RedisMapIdentityFactory(RMap<Integer, Number> redisson, IdentityDefinition definition, int partition, long step) {
        super(definition, partition, step);
        this.redisson = redisson;
    }

    @Override
    protected long getLimit(long step) {
        return redisson.addAndGet(partition, step).longValue();
    }

}
