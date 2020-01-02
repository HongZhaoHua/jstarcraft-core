package com.jstarcraft.core.common.event;

import java.util.concurrent.CountDownLatch;

public class MockMonitor implements EventMonitor {

    private CountDownLatch latch;

    public MockMonitor(int count) {
        latch = new CountDownLatch(count);
    }

    @Override
    public void onEvent(Object event) {
        latch.countDown();
    }
    
    public void await() throws InterruptedException {
        latch.await();
    }

}
