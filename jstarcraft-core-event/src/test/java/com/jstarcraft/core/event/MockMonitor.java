package com.jstarcraft.core.event;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MockMonitor implements EventMonitor<MockEvent> {

    private int index;

    private AtomicInteger count;

    private Semaphore semaphore;

    public MockMonitor(int index, Semaphore semaphore) {
        this.index = index;
        this.count = new AtomicInteger();
        this.semaphore = semaphore;
    }

    @Override
    public void onEvent(MockEvent event) {
        count.incrementAndGet();
        semaphore.release(1);
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
