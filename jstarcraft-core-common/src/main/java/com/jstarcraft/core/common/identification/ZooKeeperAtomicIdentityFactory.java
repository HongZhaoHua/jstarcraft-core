package com.jstarcraft.core.common.identification;

import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;

public class ZooKeeperAtomicIdentityFactory extends MiddlewareIdentityFactory {

    private DistributedAtomicLong middleware;

    public ZooKeeperAtomicIdentityFactory(DistributedAtomicLong middleware, IdentityDefinition definition, int partition, long step) {
        super(definition, partition, step);
        this.middleware = middleware;
    }

    @Override
    protected long getLimit(long step) {
        try {
            return middleware.add(step).postValue();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
