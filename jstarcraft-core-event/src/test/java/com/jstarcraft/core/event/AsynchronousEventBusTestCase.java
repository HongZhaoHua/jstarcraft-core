package com.jstarcraft.core.event;

import org.junit.After;
import org.junit.Before;

import com.jstarcraft.core.event.AsynchronousEventBus;
import com.jstarcraft.core.event.EventBus;

public class AsynchronousEventBusTestCase extends EventBusTestCase {

    private AsynchronousEventBus bus = new AsynchronousEventBus(1000, 10, 1000);

    @Override
    protected EventBus getEventBus() {
        return bus;
    }

    @Before
    public void start() {
        bus.start();
    }

    @After
    public void stop() {
        bus.stop();
    }

}
