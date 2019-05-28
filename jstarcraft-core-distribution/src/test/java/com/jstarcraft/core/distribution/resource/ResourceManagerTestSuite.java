package com.jstarcraft.core.distribution.resource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.distribution.resource.hazelcast.HazelcastDistributionManagerTestCase;
import com.jstarcraft.core.distribution.resource.hibernate.HibernateDistributionManagerTestCase;
import com.jstarcraft.core.distribution.resource.mongo.MongoDistributionManagerTestCase;
import com.jstarcraft.core.distribution.resource.redis.RedisDistributionManagerTestCase;
import com.jstarcraft.core.distribution.resource.zookeeper.ZooKeeperDistributionManagerTestCase;

@RunWith(Suite.class)
@SuiteClasses({ HazelcastDistributionManagerTestCase.class, HibernateDistributionManagerTestCase.class, MongoDistributionManagerTestCase.class, RedisDistributionManagerTestCase.class, ZooKeeperDistributionManagerTestCase.class })
public class ResourceManagerTestSuite {

}
