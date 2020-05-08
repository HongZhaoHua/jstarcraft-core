package com.jstarcraft.core.transaction.cassandra;

import org.cassandraunit.utils.CqlOperations;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.TransactionManagerTestCase;

// TODO Cassandra目前仍然不兼容Java 11,执行单元测试需要Java 8环境.
public class CassandraTransactionManagerTestCase extends TransactionManagerTestCase {

    private static CqlSession cqlSession;

    @BeforeClass
    public static void startCassandra() {
        try {
            EmbeddedCassandraServerHelper.startEmbeddedCassandra();
            cqlSession = EmbeddedCassandraServerHelper.getSession();
            CqlOperations.createKeyspace(cqlSession).accept(CassandraTransactionManager.KEYSPACE);
            cqlSession.execute(CassandraTransactionManager.CREATE_TABLE);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    @AfterClass
    public static void stopCassandra() {
        // TODO 执行删除操作总是异常,暂未排查
//        cqlSession.execute(CassandraTransactionManager.DELETE_TABLE);
//        CqlOperations.dropKeyspace(cqlSession).accept(CassandraTransactionManager.KEYSPACE);
        cqlSession.close();
//        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    private CassandraTransactionManager manager;

    @Before
    public void testBefore() {
        manager = new CassandraTransactionManager(cqlSession, ConsistencyLevel.QUORUM);
        manager.create(name);
    }

    @After
    public void testAfter() {
        manager.delete(name);
    }

    @Override
    protected TransactionManager getDistributionManager() {
        return manager;
    }

}
