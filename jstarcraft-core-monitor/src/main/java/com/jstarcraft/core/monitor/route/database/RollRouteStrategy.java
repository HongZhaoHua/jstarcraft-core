package com.jstarcraft.core.monitor.route.database;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询路由策略
 * 
 * @author Birdy
 *
 */
public class RollRouteStrategy implements RouteStrategy {

    /** 计数 */
    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public String chooseDataSource(List<String> keys) {
        return keys.get(Math.abs(count.getAndIncrement()) % keys.size());
    }

}
