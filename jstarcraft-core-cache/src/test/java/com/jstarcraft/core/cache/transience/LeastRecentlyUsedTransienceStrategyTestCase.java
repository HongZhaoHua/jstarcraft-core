package com.jstarcraft.core.cache.transience;

import java.util.HashMap;
import java.util.LinkedList;
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

public class LeastRecentlyUsedTransienceStrategyTestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static int MINIMUN_SIZE = 5000;
    static int MAXIMUN_SIZE = 10000;
    static int THREAD_SIZE = Runtime.getRuntime().availableProcessors();
    static int EXPIRE_SECONDS = 15;

    @Test(timeout = 20000)
    public void testExpire() {
        Map<String, String> configuration = new HashMap<>();
        configuration.put(LeastRecentlyUsedTransienceStrategy.PARAMETER_MINIMUN_SIZE, String.valueOf(MINIMUN_SIZE));
        configuration.put(LeastRecentlyUsedTransienceStrategy.PARAMETER_MAXIMUN_SIZE, String.valueOf(MAXIMUN_SIZE));
        configuration.put(LeastRecentlyUsedTransienceStrategy.PARAMETER_CONCURRENCY_LEVEL, String.valueOf(THREAD_SIZE));
        LeastRecentlyUsedTransienceStrategy strategy = new LeastRecentlyUsedTransienceStrategy("stratiegy", configuration);
        strategy.start();

        AtomicInteger expireCount = new AtomicInteger();
        LinkedList<Object> expireObjects = new LinkedList<>();
        TransienceManager manager = strategy.getTransienceManager(new TransienceMonitor() {
            @Override
            public void notifyExchanged(Object key, Object value) {
                expireCount.incrementAndGet();
                expireObjects.add(value);
            }
        });

        for (int index = 0; index < MAXIMUN_SIZE; index++) {
            manager.createInstance(index, MockEntityObject.instanceOf(index, "birdy" + index, "hong", index, index));
        }
        for (int index = 0; index < MINIMUN_SIZE; index++) {
            manager.retrieveInstance(index);
        }
        for (int index = 0; index < MINIMUN_SIZE; index++) {
            int id = MAXIMUN_SIZE + index;
            manager.createInstance(id, MockEntityObject.instanceOf(id, "birdy" + id, "hong", 0, 0));
        }

        Assert.assertThat(expireObjects.size(), CoreMatchers.equalTo(MINIMUN_SIZE));
        for (Object object : expireObjects) {
            MockEntityObject mock = (MockEntityObject) object;
            if (mock.getId() < MINIMUN_SIZE) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testPerformance() throws Exception {
        Map<String, String> configuration = new HashMap<>();
        configuration.put(LeastRecentlyUsedTransienceStrategy.PARAMETER_MINIMUN_SIZE, String.valueOf(MINIMUN_SIZE));
        configuration.put(LeastRecentlyUsedTransienceStrategy.PARAMETER_MAXIMUN_SIZE, String.valueOf(MAXIMUN_SIZE));
        configuration.put(LeastRecentlyUsedTransienceStrategy.PARAMETER_CONCURRENCY_LEVEL, String.valueOf(THREAD_SIZE));
        LeastRecentlyUsedTransienceStrategy strategy = new LeastRecentlyUsedTransienceStrategy("stratiegy", configuration);
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
                        int readId = RandomUtility.randomInteger(0, MAXIMUN_SIZE + MINIMUN_SIZE);
                        int wirteId = RandomUtility.randomInteger(0, MAXIMUN_SIZE + MINIMUN_SIZE);
                        manager.createInstance(wirteId, MockEntityObject.instanceOf(wirteId, "birdy" + wirteId, "hong", 0, 0));
                        manager.retrieveInstance(readId);
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
