package com.jstarcraft.core.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.jstarcraft.core.event.EventMonitor;

public class MockMonitor implements EventMonitor {

    private AtomicInteger count;

    private CountDownLatch latch;

    public MockMonitor(int count) {
        this.count = new AtomicInteger();
        this.latch = new CountDownLatch(count);
    }

    @Override
    public void onEvent(Object event) {
        count.incrementAndGet();
        latch.countDown();
    }

    public void awaitLatch() throws InterruptedException {
        latch.await();
    }

    public int getCount() {
        return count.get();
    }

}
