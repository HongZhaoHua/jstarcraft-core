package com.jstarcraft.core.transaction.hazelcast;

import java.io.Serializable;
import java.time.Instant;

import com.jstarcraft.core.transaction.TransactionDefinition;

/**
 * Hazelcast事务定义
 * 
 * @author Birdy
 *
 */
public class HazelcastTransactionDefinition implements Serializable {

    private static final long serialVersionUID = -2615267956144491936L;

    /** 锁名称 */
    private String name;

    /** 最多锁定到指定的时间(必选) */
    private Instant most;

    HazelcastTransactionDefinition() {
    }

    public HazelcastTransactionDefinition(TransactionDefinition definition) {
        this(definition.getName(), definition.getMost());
    }

    public HazelcastTransactionDefinition(String name, Instant most) {
        this.name = name;
        this.most = most;
    }

    public String getName() {
        return name;
    }

    public Instant getMost() {
        return most;
    }

}
