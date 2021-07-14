package com.jstarcraft.core.common.identification;

import org.redisson.api.RMap;

public class RedisMapIdentityFactory extends MiddlewareIdentityFactory {

    private RMap<Integer, Number> middleware;

    public RedisMapIdentityFactory(RMap<Integer, Number> middleware, IdentityDefinition definition, int partition, long step) {
        super(definition, partition, step);
        this.middleware = middleware;
    }

    @Override
    protected long getLimit(long step) {
        return middleware.addAndGet(partition, step).longValue();
    }

}
