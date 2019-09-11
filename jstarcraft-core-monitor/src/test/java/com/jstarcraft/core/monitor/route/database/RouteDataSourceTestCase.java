package com.jstarcraft.core.monitor.route.database;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RouteDataSourceTestCase {

    @Autowired
    private NestRouteStrategy strategy;

    @Autowired
    private RouteDataSource dataSource;

    /**
     * 测试切换数据源
     * 
     * @throws SQLException
     */
    @Test
    public void testSwitch() throws SQLException {
        strategy.pushKey("leftDataSource");
        Assert.assertEquals("jdbc:h2:nio:/target/database/left", dataSource.getConnection().getMetaData().getURL());
        strategy.pullKey();

        strategy.pushKey("rightDataSource");
        Assert.assertEquals("jdbc:h2:nio:/target/database/right", dataSource.getConnection().getMetaData().getURL());
        strategy.pullKey();
    }

}
