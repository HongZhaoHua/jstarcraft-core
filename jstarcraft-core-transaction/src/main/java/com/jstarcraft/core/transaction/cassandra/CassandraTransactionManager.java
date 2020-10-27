package com.jstarcraft.core.transaction.cassandra;

import java.time.Instant;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.jstarcraft.core.transaction.TransactionDefinition;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.exception.TransactionLockException;
import com.jstarcraft.core.transaction.exception.TransactionUnlockException;

/**
 * Cassandra事务管理器
 * 
 * @author Birdy
 *
 */
public class CassandraTransactionManager extends TransactionManager {

    public static final String KEYSPACE = "jstarcraft";

    private static final String TABLE = "CassandraTransactionDefinition";

    private static final String NAME = "name";

    private static final String MOST = "most";

    public static final String CREATE_TABLE = "CREATE TABLE " + KEYSPACE + "." + TABLE + " (name text PRIMARY KEY, most timestamp);";

    public static final String DELETE_TABLE = "DROP TABLE " + KEYSPACE + "." + TABLE + ";";

    private final CqlSession cqlSession;

    private final ConsistencyLevel consistencyLevel;

    public CassandraTransactionManager(CqlSession cqlSession, ConsistencyLevel consistencyLevel) {
        this.cqlSession = cqlSession;
        this.consistencyLevel = consistencyLevel;
    }

    private boolean execute(SimpleStatement statement) {
        return cqlSession.execute(statement.setConsistencyLevel(consistencyLevel)).wasApplied();
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
                execute(QueryBuilder.insertInto(KEYSPACE, TABLE)

                        .value(NAME, QueryBuilder.literal(name))

                        .value(MOST, QueryBuilder.literal(now))

                        .ifNotExists()

                        .build());
                count++;
            } catch (Exception exception) {
                // TODO 记录日志,锁已存在
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
                execute(QueryBuilder.deleteFrom(KEYSPACE, TABLE)

                        .whereColumn(NAME).isEqualTo(QueryBuilder.literal(name))

                        .ifExists()

                        .build());
                count++;
            } catch (Exception exception) {
                // TODO 记录日志,锁不存在
            }
        }
        return count;
    }

    @Override
    public void lock(TransactionDefinition definition) {
        // 尝试加锁
        String name = definition.getName();
        Instant instant = definition.getMost();
        Instant now = Instant.now();
        boolean lock = execute(QueryBuilder.update(KEYSPACE, TABLE)

                .setColumn(MOST, QueryBuilder.literal(instant))

                .whereColumn(NAME).isEqualTo(QueryBuilder.literal(name))

                .ifColumn(MOST).isLessThanOrEqualTo(QueryBuilder.literal(now))

                .build());
        if (!lock) {
            throw new TransactionLockException();
        }
    }

    @Override
    public void unlock(TransactionDefinition definition) {
        // 尝试解锁
        String name = definition.getName();
        Instant instant = definition.getMost();
        Instant now = Instant.now();
        boolean unlock = execute(QueryBuilder.update(KEYSPACE, TABLE)

                .setColumn(MOST, QueryBuilder.literal(now))

                .whereColumn(NAME).isEqualTo(QueryBuilder.literal(name))

                .ifColumn(MOST).isEqualTo(QueryBuilder.literal(instant))

                .ifColumn(MOST).isGreaterThan(QueryBuilder.literal(now))

                .build());
        if (!unlock) {
            throw new TransactionUnlockException();
        }
    }

}
