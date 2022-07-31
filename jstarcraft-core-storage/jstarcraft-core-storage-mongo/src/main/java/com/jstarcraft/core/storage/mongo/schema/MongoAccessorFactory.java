package com.jstarcraft.core.storage.mongo.schema;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.jstarcraft.core.storage.mongo.MongoAccessor;

/**
 * Mongo访问器工厂
 * 
 * @author Birdy
 *
 */
public class MongoAccessorFactory implements FactoryBean<MongoAccessor> {

    private static final Logger logger = LoggerFactory.getLogger(MongoAccessorFactory.class);

    public static final String CLASSES = "classes";

    private MongoAccessor accessor;
    private Collection<Class<?>> classes;

    private MongoTemplate template;

    @Override
    public MongoAccessor getObject() {
        if (accessor == null) {
            accessor = new MongoAccessor(classes, template);
        }
        return accessor;
    }

    @Override
    public Class<?> getObjectType() {
        return MongoAccessor.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setClasses(Collection<Class<?>> classes) {
        this.classes = classes;
    }

    public void setTemplate(MongoTemplate template) {
        this.template = template;
    }

}
