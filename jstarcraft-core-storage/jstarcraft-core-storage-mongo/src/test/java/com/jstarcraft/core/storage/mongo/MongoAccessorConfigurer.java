package com.jstarcraft.core.storage.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;

@Configuration
public class MongoAccessorConfigurer {

    @Bean(name = "factory", destroyMethod = "shutdown")
    public MongodForTestsFactory getFactory() throws Exception {
        MongodForTestsFactory factory = new MongodForTestsFactory(Version.Main.V3_5);
        return factory;
    }

    @Bean(name = "template")
    public MongoTemplate getTemplate(MongodForTestsFactory factory) throws Exception {
        MongoClient mongo = factory.newMongo();
        MongoTemplate template = new MongoTemplate(mongo, "test");
        return template;
    }

}
