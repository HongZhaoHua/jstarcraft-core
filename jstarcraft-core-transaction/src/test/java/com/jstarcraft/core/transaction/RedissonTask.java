package com.jstarcraft.core.transaction;

import java.io.Serializable;

import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;

public class RedissonTask implements Runnable, Serializable {

    private String counterName;

    @RInject
    private RedissonClient redisson;

    public RedissonTask() {
    }

    public RedissonTask(String counterName) {
        super();
        this.counterName = counterName;
    }

    @Override
    public void run() {
        redisson.getAtomicLong(counterName).incrementAndGet();
    }

}
