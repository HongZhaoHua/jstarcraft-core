package com.jstarcraft.core.event.memory;

import com.jstarcraft.core.event.EventBus;
import com.jstarcraft.core.event.EventBusTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.memory.SynchronousEventBus;

public class SynchronousEventBusTestCase extends EventBusTestCase {

    @Override
    protected EventBus getEventBus(EventMode mode) {
        return new SynchronousEventBus(mode);
    }

}
