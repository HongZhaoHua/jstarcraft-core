package com.jstarcraft.core.event;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEventChannel implements EventChannel {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractEventChannel.class);

    protected final EventMode mode;

    protected String name;

    protected ConcurrentMap<Class, EventManager> type2Managers;

    protected AbstractEventChannel(EventMode mode, String name) {
        this.mode = mode;
        this.name = name;
        this.type2Managers = new ConcurrentHashMap<>();
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
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void registerMonitor(Set<Class> types, EventMonitor monitor) {
        for (Class type : types) {
            EventManager manager = type2Managers.get(type);
            if (manager == null) {
                manager = new EventManager();
                type2Managers.put(type, manager);
            }
            manager.attachMonitor(monitor);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class> types, EventMonitor monitor) {
        for (Class type : types) {
            EventManager manager = type2Managers.get(type);
            if (manager != null) {
                manager.detachMonitor(monitor);
                if (manager.getSize() == 0) {
                    type2Managers.remove(type);
                }
            }
        }
    }

    @Override
    public Collection<EventMonitor> getMonitors(Class type) {
        EventManager manager = type2Managers.get(type);
        return manager == null ? Collections.EMPTY_SET : manager.getMonitors();
    }

}
