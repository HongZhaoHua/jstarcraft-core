package com.jstarcraft.core.event;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEventBus implements EventBus {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractEventBus.class);

    protected final EventMode mode;

    protected String name;

    protected ConcurrentMap<Class, EventManager> address2Managers;

    protected AbstractEventBus(EventMode mode, String name) {
        this.mode = mode;
        this.name = name;
        this.address2Managers = new ConcurrentHashMap<>();
    }

    @Override
    public EventMode getMode() {
        return mode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void registerMonitor(Set<Class> addresses, EventMonitor monitor) {
        for (Class address : addresses) {
            EventManager manager = address2Managers.get(address);
            if (manager == null) {
                manager = new EventManager();
                address2Managers.put(address, manager);
            }
            manager.attachMonitor(monitor);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class> addresses, EventMonitor monitor) {
        for (Class address : addresses) {
            EventManager manager = address2Managers.get(address);
            if (manager != null) {
                manager.detachMonitor(monitor);
                if (manager.getSize() == 0) {
                    address2Managers.remove(address);
                }
            }
        }
    }

    @Override
    public Collection<EventMonitor> getMonitors(Class address) {
        EventManager manager = address2Managers.get(address);
        return manager == null ? Collections.EMPTY_SET : manager.getMonitors();
    }

}
