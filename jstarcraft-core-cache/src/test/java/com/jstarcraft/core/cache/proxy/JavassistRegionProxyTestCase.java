package com.jstarcraft.core.cache.proxy;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jstarcraft.core.cache.CacheInformation;
import com.jstarcraft.core.cache.MockRegionObject;

public class JavassistRegionProxyTestCase {

    /** 伪装的ProxyManager,便于测试 */
    private static MockProxyManager mockProxyManager = new MockProxyManager();

    private static JavassistRegionProxy regionProxy;

    @BeforeClass
    public static void before() {
        CacheInformation regionInformation = CacheInformation.instanceOf(MockRegionObject.class);
        regionProxy = new JavassistRegionProxy(mockProxyManager, regionInformation);
    }

    @Test
    public void testDataChange() {
        // 区域对象
        MockRegionObject mockRegion = MockRegionObject.instanceOf(0, 0);
        MockRegionObject proxyRegion = regionProxy.transform(mockRegion);
        Assert.assertTrue(proxyRegion.equals(mockRegion));
        Assert.assertTrue(mockRegion.equals(proxyRegion));

        // 索引不冲突,数据不冲突
        // int oldModifyIndexesTimes = mockProxyManager.getModifyIndexes();
        int oldModifyDatasTimes = mockProxyManager.getModifyDatas();
        proxyRegion.modify(1, true);
        // int newModifyIndexesTimes = mockProxyManager.getModifyIndexes();
        int newModifyDatasTimes = mockProxyManager.getModifyDatas();
        Assert.assertThat(proxyRegion.getOwner(), CoreMatchers.equalTo(1));
        // Assert.assertEquals(0, newModifyIndexesTimes - oldModifyIndexesTimes);
        Assert.assertEquals(1, newModifyDatasTimes - oldModifyDatasTimes);

        // 索引不冲突,数据冲突
        // oldModifyIndexesTimes = mockProxyManager.getModifyIndexes();
        oldModifyDatasTimes = mockProxyManager.getModifyDatas();
        proxyRegion.modify(1, false);
        // newModifyIndexesTimes = mockProxyManager.getModifyIndexes();
        newModifyDatasTimes = mockProxyManager.getModifyDatas();
        Assert.assertThat(proxyRegion.getOwner(), CoreMatchers.equalTo(1));
        // Assert.assertEquals(0, newModifyIndexesTimes - oldModifyIndexesTimes);
        Assert.assertEquals(0, newModifyDatasTimes - oldModifyDatasTimes);
    }

}
