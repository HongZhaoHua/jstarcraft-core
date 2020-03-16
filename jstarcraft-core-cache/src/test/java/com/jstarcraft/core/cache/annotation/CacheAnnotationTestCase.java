package com.jstarcraft.core.cache.annotation;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.cache.EntityManager;
import com.jstarcraft.core.cache.MockEntityObject;
import com.jstarcraft.core.cache.MockRegionObject;
import com.jstarcraft.core.cache.RegionManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CacheAnnotationTestCase {

    /** 用于测试{@link CacheConfiguration} */
    @CacheAccessor
    private EntityManager<Integer, MockEntityObject> entityManager;
    @CacheAccessor
    private RegionManager<Integer, MockRegionObject> regionManager;

    @Autowired
    private MockService springService;

    @Test
    public void test() {
        // 保证@CacheAccessor注解的实体管理器与区域管理能被自动装配
        Assert.assertThat(entityManager, CoreMatchers.notNullValue());
        Assert.assertThat(regionManager, CoreMatchers.notNullValue());
    }

}
