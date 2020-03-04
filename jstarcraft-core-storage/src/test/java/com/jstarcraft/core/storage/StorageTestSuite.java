package com.jstarcraft.core.storage;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.storage.berkeley.BerkeleyTestSuite;
import com.jstarcraft.core.storage.hibernate.HibernateTestSuite;
import com.jstarcraft.core.storage.identification.IdentityTestSuite;
import com.jstarcraft.core.storage.lucene.LuceneTestSuite;
import com.jstarcraft.core.storage.mongo.MongoTestSuite;
import com.jstarcraft.core.storage.mybatis.MyBatisAccessorTestCase;
import com.jstarcraft.core.storage.neo4j.Neo4jTestSuite;

@RunWith(Suite.class)
@SuiteClasses({

        IdentityTestSuite.class,

        BerkeleyTestSuite.class,

        HibernateTestSuite.class,

        LuceneTestSuite.class,

        MongoTestSuite.class,

        MyBatisAccessorTestCase.class,

        Neo4jTestSuite.class })
public class StorageTestSuite {

}
