package com.jstarcraft.core.transaction.resource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.transaction.resource.hazelcast.HazelcastDistributionManagerTestCase;
import com.jstarcraft.core.transaction.resource.hibernate.HibernateDistributionManagerTestCase;
import com.jstarcraft.core.transaction.resource.mongo.MongoDistributionManagerTestCase;
import com.jstarcraft.core.transaction.resource.redis.RedisDistributionManagerTestCase;
import com.jstarcraft.core.transaction.resource.zookeeper.ZooKeeperDistributionManagerTestCase;

@RunWith(Suite.class)
@SuiteClasses({ HazelcastDistributionManagerTestCase.class, HibernateDistributionManagerTestCase.class, MongoDistributionManagerTestCase.class, RedisDistributionManagerTestCase.class, ZooKeeperDistributionManagerTestCase.class })
public class ResourceManagerTestSuite {

}
