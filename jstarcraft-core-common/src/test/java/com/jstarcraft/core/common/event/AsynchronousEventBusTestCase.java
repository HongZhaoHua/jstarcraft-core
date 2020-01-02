package com.jstarcraft.core.common.event;

import org.junit.After;
import org.junit.Before;

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
