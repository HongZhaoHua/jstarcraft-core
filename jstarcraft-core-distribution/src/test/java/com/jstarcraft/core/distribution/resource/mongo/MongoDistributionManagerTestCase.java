package com.jstarcraft.core.distribution.resource.mongo;

import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.jstarcraft.core.distribution.resource.ResourceManagerTestCase;
import com.jstarcraft.core.distribution.resource.ResourceManager;
import com.jstarcraft.core.distribution.resource.mongo.MongoResourceDefinition;
import com.jstarcraft.core.distribution.resource.mongo.MongoResourceManager;
import com.jstarcraft.core.orm.mongo.MongoAccessor;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;

public class MongoDistributionManagerTestCase extends ResourceManagerTestCase {

	private static MongodForTestsFactory factory;

	private static MongoAccessor accessor;

	@BeforeClass
	public static void startMongo() throws IOException {
		factory = new MongodForTestsFactory(Version.Main.V3_5);
		MongoClient mongo = factory.newMongo();
		MongoTemplate template = new MongoTemplate(mongo, "test");
		accessor = new MongoAccessor(Arrays.asList(MongoResourceDefinition.class), template);
	}

	@AfterClass
	public static void stopMongo() throws IOException {
		factory.shutdown();
	}

	private MongoResourceManager manager;

	@Before
	public void testBefore() {
		manager = new MongoResourceManager(accessor);
		manager.create(name);
	}

	@After
	public void testAfter() {
		manager.delete(name);
	}

	@Override
	protected ResourceManager getDistributionManager() {
		return manager;
	}

}
