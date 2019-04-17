package com.jstarcraft.core.distribution.lock;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.distribution.lock.hazelcast.HazelcastDistributionManagerTestCase;
import com.jstarcraft.core.distribution.lock.hibernate.HibernateDistributionManagerTestCase;
import com.jstarcraft.core.distribution.lock.mongo.MongoDistributionManagerTestCase;
import com.jstarcraft.core.distribution.lock.redis.RedisDistributionManagerTestCase;
import com.jstarcraft.core.distribution.lock.zookeeper.ZooKeeperDistributionManagerTestCase;

@RunWith(Suite.class)
@SuiteClasses({ HazelcastDistributionManagerTestCase.class, HibernateDistributionManagerTestCase.class, MongoDistributionManagerTestCase.class, RedisDistributionManagerTestCase.class, ZooKeeperDistributionManagerTestCase.class })
public class DistributionManagerTestSuite {

}
