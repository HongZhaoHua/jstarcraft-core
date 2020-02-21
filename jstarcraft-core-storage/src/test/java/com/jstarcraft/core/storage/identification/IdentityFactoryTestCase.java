package com.jstarcraft.core.storage.identification;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.storage.identification.IdentityFactory;
import com.jstarcraft.core.utility.StringUtility;

public abstract class IdentityFactoryTestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected abstract IdentityFactory getIdentityFactory();

    @Test
    public void testSequence() {
        IdentityFactory identityFactory = getIdentityFactory();
        for (int index = 0; index < 1000; index++) {
            long sequence = identityFactory.getSequence();
            Assert.assertThat(identityFactory.getSequence(), CoreMatchers.equalTo(sequence + 1));
        }
    }

    @Test
    public void testPerformance() throws Exception {
        IdentityFactory identityFactory = getIdentityFactory();
        int duration = 2000;

        int thread = 1;
        ExecutorService single = Executors.newFixedThreadPool(thread);
        Future<Long> future = single.submit(() -> {
            long current = System.currentTimeMillis();
            long size = 0;
            while (System.currentTimeMillis() - current < duration) {
                long less = identityFactory.getSequence();
                long greater = identityFactory.getSequence();
                Assert.assertTrue(greater > less);
                size += 2;
            }
            return size;
        });
        long count = future.get();
        String message = StringUtility.format("标识工厂[{}]在{}线程的条件下,{}毫秒内制造了{}个序列", identityFactory.getClass().getSimpleName(), thread, duration, count);
        logger.debug(message);

        thread = Runtime.getRuntime().availableProcessors();
        ExecutorService multiple = Executors.newFixedThreadPool(thread);
        ArrayList<Future<Long>> futures = new ArrayList<Future<Long>>();
        CountDownLatch latch = new CountDownLatch(thread);
        for (int index = 0; index < thread; index++) {
            future = multiple.submit(() -> {
                latch.countDown();
                latch.await();
                long current = System.currentTimeMillis();
                long size = 0;
                while (System.currentTimeMillis() - current < duration) {
                    long less = identityFactory.getSequence();
                    long greater = identityFactory.getSequence();
                    Assert.assertTrue(greater > less);
                    size += 2;
                }
                return size;
            });
            futures.add(future);
        }
        count = 0;
        for (Future<Long> element : futures) {
            count += element.get();
        }
        message = StringUtility.format("标识工厂[{}]在{}线程的条件下,{}毫秒内制造了{}个序列", identityFactory.getClass().getSimpleName(), thread, duration, count);
        logger.debug(message);
    }

}
