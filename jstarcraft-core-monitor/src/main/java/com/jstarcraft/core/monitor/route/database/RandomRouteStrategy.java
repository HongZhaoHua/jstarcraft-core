package com.jstarcraft.core.monitor.route.database;

import java.util.List;

import com.jstarcraft.core.utility.RandomUtility;

/**
 * 随机路由策略
 * 
 * @author Birdy
 *
 */
public class RandomRouteStrategy implements RouteStrategy {

    @Override
    public String chooseDataSource(List<String> keys) {
        return keys.get(RandomUtility.randomInteger(keys.size()));
    }

}