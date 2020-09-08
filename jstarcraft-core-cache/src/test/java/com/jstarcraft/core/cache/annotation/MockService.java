package com.jstarcraft.core.cache.annotation;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.Assert;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.jstarcraft.core.cache.CacheService;
import com.jstarcraft.core.common.lifecycle.LifecycleState;

public class MockService implements InitializingBean, DisposableBean {

    @Autowired
    private CacheService cacheService;

    @PostConstruct
    public void postConstruct() throws Exception {
        if (!cacheService.getState().equals(LifecycleState.STARTED)) {
            Assert.fail();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!cacheService.getState().equals(LifecycleState.STARTED)) {
            Assert.fail();
        }
    }

    @PreDestroy
    public void preDestory() {
        if (!cacheService.getState().equals(LifecycleState.STOPPED)) {
            Assert.fail();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (!cacheService.getState().equals(LifecycleState.STOPPED)) {
            Assert.fail();
        }
    }

}
