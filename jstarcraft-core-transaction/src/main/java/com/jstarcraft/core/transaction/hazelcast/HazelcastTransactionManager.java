package com.jstarcraft.core.transaction.hazelcast;

import java.time.Instant;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jstarcraft.core.transaction.TransactionDefinition;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.exception.TransactionUnlockException;

/**
 * Hazelcast事务管理器
 * 
 * @author Birdy
 *
 */
public class HazelcastTransactionManager extends TransactionManager {

    private static final String DEFAULT_STORE = "jstarcraft";

    private final String store;

    private final HazelcastInstance hazelcastInstance;

    public HazelcastTransactionManager(HazelcastInstance hazelcastInstance) {
        this(hazelcastInstance, DEFAULT_STORE);
    }

    public HazelcastTransactionManager(HazelcastInstance hazelcastInstance, String store) {
        this.hazelcastInstance = hazelcastInstance;
        this.store = store;
    }

    private IMap<String, HazelcastTransactionDefinition> getStore() {
        return hazelcastInstance.getMap(store);
    }

    @Override
    public void lock(TransactionDefinition definition) {
        Instant now = Instant.now();
        String name = definition.getName();
        final IMap<String, HazelcastTransactionDefinition> store = getStore();
        try {
            store.lock(name);
            HazelcastTransactionDefinition current = store.get(name);
            if (current == null) {
                store.put(name, new HazelcastTransactionDefinition(definition));
            } else if (now.isAfter(current.getMost())) {
                store.put(name, new HazelcastTransactionDefinition(definition));
            } else {
                throw new TransactionLockException();
            }
        } finally {
            store.unlock(name);
        }
    }

    @Override
    public void unlock(TransactionDefinition definition) {
        Instant now = Instant.now();
        String name = definition.getName();
        final IMap<String, HazelcastTransactionDefinition> store = getStore();
        try {
            store.lock(name);
            HazelcastTransactionDefinition current = store.get(name);
            if (current == null) {
                throw new TransactionUnlockException();
            } else if (now.isAfter(current.getMost())) {
                throw new TransactionUnlockException();
            } else {
                store.remove(name);
            }
        } finally {
            store.unlock(name);
        }
    }

}
