package com.jstarcraft.core.orm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.orm.berkeley.BerkeleyTestSuite;
import com.jstarcraft.core.orm.hibernate.HibernateTestSuite;
import com.jstarcraft.core.orm.mongo.MongoTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ BerkeleyTestSuite.class, HibernateTestSuite.class, MongoTestSuite.class })
public class OrmTestSuite {

}
