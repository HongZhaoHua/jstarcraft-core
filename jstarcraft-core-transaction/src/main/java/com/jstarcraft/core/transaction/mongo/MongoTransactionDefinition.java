package com.jstarcraft.core.transaction.mongo;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jstarcraft.core.common.identification.IdentityObject;
import com.jstarcraft.core.transaction.TransactionDefinition;

/**
 * Mongo事务定义
 * 
 * @author Birdy
 *
 */
@Document
public class MongoTransactionDefinition implements IdentityObject<String> {

    /** 锁名称 */
    @Id
    private String name;

    /** 最多锁定到指定的时间(必选) */
    private long most;

    MongoTransactionDefinition() {
    }

    public MongoTransactionDefinition(TransactionDefinition definition) {
        this(definition.getName(), definition.getMost());
    }

    public MongoTransactionDefinition(String name, Instant most) {
        this.name = name;
        this.most = most.toEpochMilli();
    }

    @Override
    public String getId() {
        return name;
    }

    public String getName() {
        return name;
    }

    public long getMost() {
        return most;
    }

}
