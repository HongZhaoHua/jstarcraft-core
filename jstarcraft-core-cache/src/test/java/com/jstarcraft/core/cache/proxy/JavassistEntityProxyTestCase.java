package com.jstarcraft.core.cache.proxy;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jstarcraft.core.cache.CacheInformation;
import com.jstarcraft.core.cache.MockEntityObject;

public class JavassistEntityProxyTestCase {

    /** 伪装的ProxyManager,便于测试 */
    private static MockProxyManager mockProxyManager = new MockProxyManager();

    private static JavassistEntityProxy entityProxy;

    @BeforeClass
    public static void before() {
        CacheInformation entityInformation = CacheInformation.instanceOf(MockEntityObject.class);
        entityProxy = new JavassistEntityProxy(mockProxyManager, entityInformation);
    }

    @Test
    public void testDataChange() {
        MockEntityObject mockEntity = MockEntityObject.instanceOf(0, "birdy", "hong", 0, 0);
        MockEntityObject proxyEntity = entityProxy.transform(mockEntity);
        Assert.assertTrue(proxyEntity.equals(mockEntity));
        Assert.assertTrue(mockEntity.equals(proxyEntity));

        // 数据不冲突
        // int oldModifyIndexesTimes = mockProxyManager.getModifyIndexes();
        int oldModifyDatasTimes = mockProxyManager.getModifyDatas();
        proxyEntity.modify("xiao", 10, true);
        // int newModifyIndexesTimes = mockProxyManager.getModifyIndexes();
        int newModifyDatasTimes = mockProxyManager.getModifyDatas();
        Assert.assertThat(proxyEntity.getLastName(), CoreMatchers.equalTo("xiao"));
        Assert.assertThat(proxyEntity.getMoney(), CoreMatchers.equalTo(10));
        // Assert.assertEquals(0, newModifyIndexesTimes - oldModifyIndexesTimes);
        Assert.assertEquals(1, newModifyDatasTimes - oldModifyDatasTimes);

        // 数据冲突
        // oldModifyIndexesTimes = mockProxyManager.getModifyIndexes();
        oldModifyDatasTimes = mockProxyManager.getModifyDatas();
        proxyEntity.modify("xiao", 10, false);
        Assert.assertThat(proxyEntity.getLastName(), CoreMatchers.equalTo("xiao"));
        Assert.assertThat(proxyEntity.getMoney(), CoreMatchers.equalTo(10));
        // Assert.assertEquals(0, newModifyIndexesTimes - oldModifyIndexesTimes);
        Assert.assertEquals(0, newModifyDatasTimes - oldModifyDatasTimes);
    }

}
