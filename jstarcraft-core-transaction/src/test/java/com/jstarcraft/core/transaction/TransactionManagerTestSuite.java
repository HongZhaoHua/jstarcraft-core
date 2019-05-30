package com.jstarcraft.core.transaction;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.transaction.hazelcast.HazelcastTransactionManagerTestCase;
import com.jstarcraft.core.transaction.hibernate.HibernateTransactionManagerTestCase;
import com.jstarcraft.core.transaction.mongo.MongoTransactionManagerTestCase;
import com.jstarcraft.core.transaction.redis.RedisTransactionManagerTestCase;
import com.jstarcraft.core.transaction.zookeeper.ZooKeeperTransactionManagerTestCase;

@RunWith(Suite.class)
@SuiteClasses({ HazelcastTransactionManagerTestCase.class, HibernateTransactionManagerTestCase.class, MongoTransactionManagerTestCase.class, RedisTransactionManagerTestCase.class, ZooKeeperTransactionManagerTestCase.class })
public class TransactionManagerTestSuite {

}
