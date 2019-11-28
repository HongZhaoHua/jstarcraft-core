package com.jstarcraft.core.orm.neo4j;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jAccessorConfigurer {

    @Bean(name = "factory", destroyMethod = "close")
    public SessionFactory getFactory() throws Exception {
        File file = new File("neo4j");
        FileUtils.deleteQuietly(file);
        GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase(file);
        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder().build();
        EmbeddedDriver driver = new EmbeddedDriver(database, configuration);
        SessionFactory factory = new SessionFactory(driver, "com.jstarcraft.core.orm.neo4j");
        return factory;
    }

    @Bean(name = "template")
    public Session getTemplate(SessionFactory factory) throws Exception {
        Session template = factory.openSession();
        return template;
    }

    @Bean(name = "accessor")
    public Neo4jAccessor getAccessor(SessionFactory factory) {
        Neo4jAccessor accessor = new Neo4jAccessor(factory);
        return accessor;
    }

}
