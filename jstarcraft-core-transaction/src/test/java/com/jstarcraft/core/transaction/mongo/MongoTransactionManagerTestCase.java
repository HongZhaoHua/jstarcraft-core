package com.jstarcraft.core.transaction.mongo;

import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.jstarcraft.core.storage.mongo.MongoAccessor;
import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.TransactionManagerTestCase;
import com.jstarcraft.core.transaction.mongo.MongoTransactionDefinition;
import com.jstarcraft.core.transaction.mongo.MongoTransactionManager;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;

public class MongoTransactionManagerTestCase extends TransactionManagerTestCase {

    private static MongodForTestsFactory factory;

    private static MongoAccessor accessor;

    @BeforeClass
    public static void startMongo() throws IOException {
        factory = new MongodForTestsFactory(Version.Main.V3_5);
        MongoClient mongo = factory.newMongo();
        MongoTemplate template = new MongoTemplate(mongo, "test");
        accessor = new MongoAccessor(Arrays.asList(MongoTransactionDefinition.class), template);
    }

    @AfterClass
    public static void stopMongo() throws IOException {
        factory.shutdown();
    }

    private MongoTransactionManager manager;

    @Before
    public void testBefore() {
        manager = new MongoTransactionManager(accessor);
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
