package com.jstarcraft.core.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.jstarcraft.core.event.EventMonitor;

public class MockMonitor implements EventMonitor {

    private int index;

    private AtomicInteger count;

    private CountDownLatch latch;

    public MockMonitor(int index, CountDownLatch latch) {
        this.index = index;
        this.count = new AtomicInteger();
        this.latch = latch;
    }

    @Override
    public void onEvent(Object event) {
        count.incrementAndGet();
        latch.countDown();
    }

    public int getIndex() {
        return index;
    }

    public int getCount() {
        return count.get();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = prime * hash + index;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        MockMonitor that = (MockMonitor) object;
        if (this.index != that.index)
            return false;
        return true;
    }

}
