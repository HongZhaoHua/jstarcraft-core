package com.jstarcraft.core.cache.transience;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.MockEntityObject;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

public class DelayedTransienceStrategyTestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static int DATA_SIZE = 10000;
    static int EXPIRE_SECONDS = 15;

    @Test(timeout = 20000)
    public void testExpire() throws Exception {
        Map<String, String> configuration = new HashMap<>();
        configuration.put(DelayedTransienceStrategy.PARAMETER_EXPIRE, String.valueOf(EXPIRE_SECONDS));
        configuration.put(DelayedTransienceStrategy.PARAMETER_SEGMENT, "3");
        DelayedTransienceStrategy strategy = new DelayedTransienceStrategy("stratiegy", configuration);
        strategy.start();

        AtomicInteger expireCount = new AtomicInteger();
        TransienceManager manager = strategy.getTransienceManager(new TransienceMonitor() {
            @Override
            public void notifyExchanged(Object key, Object value) {
                expireCount.incrementAndGet();
            }
        });

        for (int index = 0; index < DATA_SIZE; index++) {
            manager.createInstance(index, MockEntityObject.instanceOf(index, "birdy" + index, "hong", index, index));
        }

        long begin = System.currentTimeMillis();
        while (manager.getSize() != 0) {
        }
        long end = System.currentTimeMillis();
        String message = StringUtility.format("{}数据的到期时间:{}毫秒", DATA_SIZE, end - begin);
        logger.debug(message);
        // 等待到期事件
        Thread.sleep(1000);
        Assert.assertThat(expireCount.get(), CoreMatchers.equalTo(DATA_SIZE));
    }

    @Test
    public void testPerformance() throws Exception {
        Map<String, String> configuration = new HashMap<>();
        configuration.put(DelayedTransienceStrategy.PARAMETER_EXPIRE, String.valueOf(EXPIRE_SECONDS));
        configuration.put(DelayedTransienceStrategy.PARAMETER_SEGMENT, "3");
        DelayedTransienceStrategy strategy = new DelayedTransienceStrategy("stratiegy", configuration);
        strategy.start();
        TransienceManager manager = strategy.getTransienceManager(null);

        // 多线程并发读写操作
        int threadSize = 100;
        AtomicBoolean run = new AtomicBoolean(true);
        AtomicLong operationCount = new AtomicLong();
        for (int index = 0; index < threadSize; index++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (run.get()) {
                        int readId = RandomUtility.randomInteger(0, DATA_SIZE);
                        int wirteId = RandomUtility.randomInteger(0, DATA_SIZE);
                        manager.retrieveInstance(readId);
                        manager.createInstance(wirteId, MockEntityObject.instanceOf(wirteId, "birdy" + wirteId, "hong", 0, 0));
                        operationCount.incrementAndGet();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }

        Thread.sleep(TimeUnit.MILLISECONDS.convert(EXPIRE_SECONDS, TimeUnit.SECONDS));
        run.set(false);
        if (manager.getSize() == 0) {
            Assert.fail();
        }

        String message = StringUtility.format("{}策略{}条线程在{}秒内执行{}次读写操作", strategy.getName(), threadSize, EXPIRE_SECONDS, operationCount.get());
        logger.debug(message);
    }

}
