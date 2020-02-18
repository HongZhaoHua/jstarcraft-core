package com.jstarcraft.core.event;

import com.jstarcraft.core.event.EventBus;
import com.jstarcraft.core.event.SynchronousEventBus;

public class SynchronousEventBusTestCase extends EventBusTestCase {

    private SynchronousEventBus bus = new SynchronousEventBus();

    @Override
    protected EventBus getEventBus() {
        return bus;
    }

}
