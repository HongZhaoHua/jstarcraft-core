package com.jstarcraft.core.common.event;

public class SynchronousEventBusTestCase extends EventBusTestCase {

    private SynchronousEventBus bus = new SynchronousEventBus();

    @Override
    protected EventBus getEventBus() {
        return bus;
    }

}
