package com.jstarcraft.core.cache.crud.berkeley;

import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jstarcraft.core.cache.CacheIndex;
import com.jstarcraft.core.cache.CacheObjectFactory;
import com.jstarcraft.core.cache.CacheService;
import com.jstarcraft.core.cache.EntityManager;
import com.jstarcraft.core.cache.RegionManager;
import com.jstarcraft.core.cache.annotation.CacheAccessor;
import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.storage.StorageAccessor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BerkeleyCrudTestCase {

    @Autowired
    private StorageAccessor accessor;

    @Autowired
    private CacheService cacheService;

    /** 用于测试{@link CacheConfiguration} */
    @CacheAccessor
    private EntityManager<Integer, BerkeleyEntityObject> entityManager;

    /** 用于测试{@link CacheConfiguration} */
    @CacheAccessor
    private RegionManager<Integer, BerkeleyRegionObject> regionManager;

    private static int SIZE = 5;

    @Before
    public void beforeTest() throws Exception {
        // 此部分数据最初不加载到缓存
        for (int index = 1; index <= SIZE; index++) {
            BerkeleyEntityObject entity = BerkeleyEntityObject.instanceOf(-index, "birdy:" + index, "hong", index, index);
            accessor.createInstance(BerkeleyEntityObject.class, entity);

            for (int position = 1; position <= SIZE; position++) {
                BerkeleyRegionObject region = BerkeleyRegionObject.instanceOf(-(index * SIZE + position), entity.getId());
                accessor.createInstance(BerkeleyRegionObject.class, region);
            }
        }
    }

    @After
    public void afterTest() throws Exception {
        for (int index = 1; index <= SIZE; index++) {
            accessor.deleteInstance(BerkeleyEntityObject.class, -index);
            accessor.deleteInstance(BerkeleyEntityObject.class, index);

            for (int position = 1; position <= SIZE; position++) {
                accessor.deleteInstance(BerkeleyRegionObject.class, -(index * SIZE + position));
                accessor.deleteInstance(BerkeleyRegionObject.class, (index * SIZE + position));
            }
        }
    }

    @Test
    public void testCRUD() {
        for (int index = 1; index <= SIZE; index++) {
            // 测试创建
            BerkeleyEntityObject entity = entityManager.loadInstance(index, new CacheObjectFactory<Integer, BerkeleyEntityObject>() {
                @Override
                public BerkeleyEntityObject instanceOf(Integer id) {
                    return BerkeleyEntityObject.instanceOf(id, "birdy:" + id, "hong", id, id);
                }
            });
            if (entity != entityManager.getInstance(entity.getId())) {
                Assert.fail();
            }
            // 测试索引
            CacheIndex cacheIndex = new CacheIndex("firstName", entity.getFirstName());
            if (!entityManager.getIdentities(cacheIndex).contains(entity.getId())) {
                Assert.fail();
            }
            Assert.assertThat(entityManager.getIdentities(cacheIndex).size(), CoreMatchers.equalTo(2));
            // 测试更新
            entity.modify("lastName", 1000, true);
            // 测试删除
            entityManager.deleteInstance(entity.getId());
            if (entityManager.getInstance(entity.getId()) != null) {
                Assert.fail();
            }
            entity = entityManager.loadInstance(index, new CacheObjectFactory<Integer, BerkeleyEntityObject>() {
                @Override
                public BerkeleyEntityObject instanceOf(Integer id) {
                    return BerkeleyEntityObject.instanceOf(id, "birdy:" + id, "hong", id, id);
                }
            });

            for (int position = 1; position <= SIZE; position++) {
                // 测试创建
                BerkeleyRegionObject region = BerkeleyRegionObject.instanceOf(index * SIZE + position, entity.getId());
                region = regionManager.createInstance(region);
                cacheIndex = new CacheIndex("owner", entity.getId());
                if (region != regionManager.getInstance(cacheIndex, region.getId())) {
                    Assert.fail();
                }
                // 测试删除
                regionManager.deleteInstance(region);
                cacheIndex = new CacheIndex("owner", entity.getId());
                if (regionManager.getInstance(cacheIndex, region.getId()) != null) {
                    Assert.fail();
                }
                region = BerkeleyRegionObject.instanceOf(index * SIZE + position, entity.getId());
                region = regionManager.createInstance(region);
            }
            // 测试索引
            cacheIndex = new CacheIndex("owner", index);
            Collection<BerkeleyRegionObject> regions = regionManager.getInstances(cacheIndex);
            Assert.assertThat(regions.size(), CoreMatchers.equalTo(SIZE));
        }

        // 测试查询
        Assert.assertThat(entityManager.getInstanceCount(), CoreMatchers.equalTo(SIZE));
        Assert.assertThat(regionManager.getInstanceCount(), CoreMatchers.equalTo(SIZE * SIZE));

        for (int index = 1; index <= SIZE; index++) {
            entityManager.deleteInstance(-index);
            Assert.assertNull(entityManager.getInstance(-index));
        }

        cacheService.stop();
    }

}
