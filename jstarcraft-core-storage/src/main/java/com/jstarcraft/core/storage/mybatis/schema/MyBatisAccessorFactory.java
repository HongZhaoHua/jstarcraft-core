package com.jstarcraft.core.storage.mybatis.schema;

import java.util.Collection;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.jstarcraft.core.storage.mybatis.MyBatisAccessor;

/**
 * MyBatis访问器工厂
 * 
 * @author Birdy
 *
 */
public class MyBatisAccessorFactory implements FactoryBean<MyBatisAccessor> {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisAccessorFactory.class);

    public static final String CLASSES = "classes";

    private MyBatisAccessor accessor;
    private Collection<Class<?>> classes;

    private SqlSessionTemplate template;

    @Override
    public MyBatisAccessor getObject() {
        if (accessor == null) {
            accessor = new MyBatisAccessor(classes, template);
        }
        return accessor;
    }

    @Override
    public Class<?> getObjectType() {
        return MyBatisAccessor.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setClasses(Collection<Class<?>> classes) {
        this.classes = classes;
    }

    public void setTemplate(SqlSessionTemplate template) {
        this.template = template;
    }

}
