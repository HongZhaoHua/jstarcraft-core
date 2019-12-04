package com.jstarcraft.core.resource.schema;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jstarcraft.core.resource.ResourceStorage;
import com.jstarcraft.core.resource.definition.FormatDefinition;

/**
 * 仓储管理器工厂
 * 
 * @author Birdy
 */
public class StorageManagerFactory extends DefaultListableBeanFactory implements ApplicationContextAware, FactoryBean<ResourceStorage> {

    public static final String DEFINITIONS = "definitions";

    private ApplicationContext applicationContext;

    /** 仓储定义列表 */
    private Map<Class<?>, FormatDefinition> definitions;

    private ResourceStorage storageManager;

    public void setDefinitions(Map<Class<?>, FormatDefinition> definitions) {
        this.definitions = definitions;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        // TODO 将applicationContext设置为StorageManagerFactory的工厂.
        this.setParentBeanFactory(this.applicationContext);
        // 支持属性文件(*.properties)
        Map<String, PropertyPlaceholderConfigurer> propertyConfigurers = this.applicationContext.getBeansOfType(PropertyPlaceholderConfigurer.class);
        for (PropertyPlaceholderConfigurer ropertyConfigurer : propertyConfigurers.values()) {
            ropertyConfigurer.postProcessBeanFactory(this);
        }
    }

    @Override
    public synchronized ResourceStorage getObject() throws Exception {
        if (storageManager == null) {
            storageManager = ResourceStorage.instanceOf(this.definitions, this);
        }
        return storageManager;
    }

    @Override
    public Class<ResourceStorage> getObjectType() {
        return ResourceStorage.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
