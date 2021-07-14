package com.jstarcraft.core.common.identification;

import org.redisson.api.RAtomicLong;

public class RedisAtomicIdentityFactory extends MiddlewareIdentityFactory {

    private RAtomicLong middleware;

    public RedisAtomicIdentityFactory(RAtomicLong middleware, IdentityDefinition definition, int partition, long step) {
        super(definition, partition, step);
        this.middleware = middleware;
    }

    @Override
    protected long getLimit(long step) {
        return middleware.addAndGet(step);
    }

}
