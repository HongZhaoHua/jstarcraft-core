package com.jstarcraft.core.common.lockable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.lockable.ChainLockable;
import com.jstarcraft.core.common.lockable.CompareLockManager;
import com.jstarcraft.core.utility.RandomUtility;

public class CompareLockTestCase {

    private CompareLockManager objectLockManager = CompareLockManager.getManager(MockObject.class);
    private CompareLockManager integerLockManager = CompareLockManager.getManager(Integer.class);

    @Test
    public void testSortLocks() {
        Comparable left = new MockObject<>("0");
        Comparable right = new MockObject<>("1");
        Comparable[] objects = null;
        List<Lock> locks = null;

        objects = new Comparable[] { left };
        locks = ChainLockable.sortLocks(objects);
        Assert.assertThat(locks.size(), CoreMatchers.is(1));
        Assert.assertThat(locks.get(0), CoreMatchers.sameInstance(objectLockManager.getInstanceLock(left)));

        objects = new Comparable[] { left, right };
        locks = ChainLockable.sortLocks(objects);
        Assert.assertThat(locks.size(), CoreMatchers.is(2));
        Assert.assertThat(locks.get(0), CoreMatchers.sameInstance(objectLockManager.getInstanceLock(left)));
        Assert.assertThat(locks.get(1), CoreMatchers.sameInstance(objectLockManager.getInstanceLock(right)));

        objects = new Comparable[] { left, left };
        locks = ChainLockable.sortLocks(objects);
        Assert.assertThat(locks.size(), CoreMatchers.is(3));
        Assert.assertThat(locks.get(0), CoreMatchers.sameInstance(objectLockManager.getClassLock()));
        Assert.assertThat(locks.get(1), CoreMatchers.sameInstance(objectLockManager.getInstanceLock(left)));
        Assert.assertThat(locks.get(2), CoreMatchers.sameInstance(objectLockManager.getInstanceLock(left)));

        objects = new Comparable[] { left, right, 1 };
        locks = ChainLockable.sortLocks(objects);
        Assert.assertThat(locks.size(), CoreMatchers.is(3));
        Assert.assertThat(locks.get(0), CoreMatchers.sameInstance(objectLockManager.getInstanceLock(left)));
        Assert.assertThat(locks.get(1), CoreMatchers.sameInstance(objectLockManager.getInstanceLock(right)));
        Assert.assertThat(locks.get(2), CoreMatchers.sameInstance(integerLockManager.getInstanceLock(1)));
    }

    @Test
    public void testChainLock() throws InterruptedException {
        AtomicInteger count = new AtomicInteger();
        Comparable first = new MockObject<>(0);
        Comparable right = new MockObject<>(1);
        Comparable[] objects = new Comparable[] { first, right };
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (Comparable object : objects) {
            executor.submit(() -> {
                Lock lock = objectLockManager.getInstanceLock(object);
                try {
                    Thread.sleep(100);
                    lock.lock();
                    Thread.sleep(100);
                    count.addAndGet(10);
                } catch (InterruptedException exception) {
                    Assert.fail();
                } finally {
                    lock.unlock();
                }
            });
        }

        try (ChainLockable lock = ChainLockable.instanceOf(objects)) {
            lock.open();
            Assert.assertThat(count.get(), CoreMatchers.is(0));
            Thread.sleep(500);
            Assert.assertThat(count.get(), CoreMatchers.is(0));
        }
        Thread.sleep(150);
        Assert.assertThat(count.get(), CoreMatchers.is(20));
    }

    @Test(timeout = 10000)
    public void testPerformance() throws InterruptedException {
        List<Comparable> objects = new ArrayList<>(10);
        objects.add(Byte.valueOf("0"));
        objects.add(Double.valueOf("0"));
        objects.add(Float.valueOf("0"));
        objects.add(Integer.valueOf("0"));
        objects.add(Long.valueOf("0"));
        objects.add(Short.valueOf("0"));
        objects.add(String.valueOf("0"));
        objects.add(Instant.now());
        objects.add(new MockObject<>("left"));
        objects.add(new MockObject<>("right"));

        int count = 500; // 线程数
        int times = 2000; // 任务数
        ExecutorService executor = Executors.newFixedThreadPool(count);
        CountDownLatch latch = new CountDownLatch(count);
        for (int thread = 0; thread < count; thread++) {
            executor.submit(() -> {
                for (int time = 0; time < times; time++) {
                    // 随机锁数量
                    int size = RandomUtility.randomInteger(0, objects.size()) + 1;
                    Comparable[] locks = new Comparable[size];
                    for (int index = 0; index < locks.length; index++) {
                        int random = RandomUtility.randomInteger(0, objects.size());
                        locks[index] = objects.get(random);
                    }
                    ChainLockable lock = ChainLockable.instanceOf(locks);
                    try {
                        lock.open();
                        // 转让CPU
                        Thread.yield();
                    } finally {
                        lock.close();
                    }
                }
                latch.countDown();
            });
        }
        latch.await();
    }

}
