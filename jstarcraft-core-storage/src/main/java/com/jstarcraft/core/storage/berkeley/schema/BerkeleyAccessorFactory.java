package com.jstarcraft.core.storage.berkeley.schema;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;

import com.jstarcraft.core.storage.berkeley.BerkeleyAccessor;
import com.jstarcraft.core.storage.berkeley.BerkeleyState;

/**
 * Berkeley访问器工厂
 * 
 * @author Birdy
 *
 */
public class BerkeleyAccessorFactory implements FactoryBean<BerkeleyAccessor>, ApplicationContextAware, ApplicationListener<ApplicationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(BerkeleyAccessorFactory.class);

    public static final String CLASSES = "classes";

    private BerkeleyAccessor accessor;
    private Collection<Class<?>> classes;
    private File directory;
    private String properties;
    private boolean readOnly;
    private boolean temporary;
    private long versionKeep;
    private boolean writeDelay;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public synchronized void onApplicationEvent(ApplicationEvent event) {
        getObject();

        if (event instanceof ContextRefreshedEvent) {
            if (accessor.getState() == BerkeleyState.STOPPED) {
                accessor.start();
            }
            return;
        }

        if (event instanceof ContextClosedEvent) {
            if (accessor.getState() == BerkeleyState.STARTED) {
                accessor.stop();
            }
            return;
        }
    }

    @Override
    public BerkeleyAccessor getObject() {
        if (accessor == null) {
            Resource resource = applicationContext.getResource(properties);
            Properties properties = new Properties();
            try {
                properties.load(resource.getInputStream());
            } catch (IOException exception) {
                throw new IllegalArgumentException("无法获取配置文件[" + properties + "]");
            }
            accessor = new BerkeleyAccessor(classes, directory, properties, readOnly, writeDelay, temporary, versionKeep);
        }
        return accessor;
    }

    @Override
    public Class<?> getObjectType() {
        return BerkeleyAccessor.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setClasses(Collection<Class<?>> classes) {
        this.classes = classes;
    }

    public void setDirectory(String directory) {
        this.directory = new File(directory);
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public void setVersionKeep(long versionKeep) {
        this.versionKeep = versionKeep;
    }

    public void setWriteDelay(boolean writeDelay) {
        this.writeDelay = writeDelay;
    }

}
