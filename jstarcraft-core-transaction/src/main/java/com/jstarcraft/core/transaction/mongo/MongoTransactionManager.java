package com.jstarcraft.core.transaction.mongo;

import java.time.Instant;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.jstarcraft.core.storage.mongo.MongoAccessor;
import com.jstarcraft.core.storage.mongo.MongoMetadata;
import com.jstarcraft.core.transaction.TransactionDefinition;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.exception.TransactionUnlockException;

/**
 * Mongo事务管理器
 * 
 * @author Birdy
 *
 */
public class MongoTransactionManager extends TransactionManager {

    private MongoAccessor accessor;

    public MongoTransactionManager(MongoAccessor accessor) {
        this.accessor = accessor;
    }

    /**
     * 尝试创建指定的锁
     * 
     * @param names
     * @return
     */
    public int create(String... names) {
        int count = 0;
        Instant now = Instant.now();
        for (String name : names) {
            try {
                MongoTransactionDefinition definition = new MongoTransactionDefinition(name, now);
                accessor.createInstance(MongoTransactionDefinition.class, definition);
                count++;
            } catch (Exception exception) {
            }
        }
        return count;
    }

    /**
     * 尝试删除指定的锁
     * 
     * @param names
     * @return
     */
    public int delete(String... names) {
        int count = 0;
        for (String name : names) {
            try {
                accessor.deleteInstance(MongoTransactionDefinition.class, name);
                count++;
            } catch (Exception exception) {
            }
        }
        return count;
    }

    @Override
    protected void lock(TransactionDefinition definition) {
        Instant now = Instant.now();
        Criteria criteria = Criteria.where(MongoMetadata.mongoId).is(definition.getName());
        Criteria[] andCriterias = new Criteria[1];
        andCriterias[0] = Criteria.where("most").lte(now.toEpochMilli());
        Query query = Query.query(criteria.andOperator(andCriterias));
        Update update = new Update();
        update.set("most", definition.getMost().toEpochMilli());
        long count = accessor.update(MongoTransactionDefinition.class, query, update);
        if (count != 1) {
            throw new TransactionLockException();
        }
    }

    @Override
    protected void unlock(TransactionDefinition definition) {
        Instant now = Instant.now();
        Criteria criteria = Criteria.where(MongoMetadata.mongoId).is(definition.getName());
        Criteria[] andCriterias = new Criteria[2];
        andCriterias[0] = Criteria.where("most").is(definition.getMost().toEpochMilli());
        andCriterias[1] = Criteria.where("most").gt(now.toEpochMilli());
        Query query = Query.query(criteria.andOperator(andCriterias));
        Update update = new Update();
        update.set("most", now.toEpochMilli());
        long count = accessor.update(MongoTransactionDefinition.class, query, update);
        if (count != 1) {
            throw new TransactionUnlockException();
        }
    }

}
