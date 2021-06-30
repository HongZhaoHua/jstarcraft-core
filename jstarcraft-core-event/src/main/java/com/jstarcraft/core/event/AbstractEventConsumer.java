package com.jstarcraft.core.event;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEventConsumer implements EventConsumer {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractEventConsumer.class);

    protected static final String CONTEXT = "JStarCraftContext";

    protected static final String DATA = "JStarCraftData";

    protected String name;

    protected ConcurrentMap<Class, EventManager> managers;

    protected Supplier<String> getter;

    protected AbstractEventConsumer(String name) {
        this(name, null);
    }

    protected AbstractEventConsumer(String name, Supplier<String> getter) {
        this.name = name;
        this.getter = getter;
        this.managers = new ConcurrentHashMap<>();
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
            EventManager manager = managers.get(type);
            if (manager == null) {
                manager = new EventManager();
                managers.put(type, manager);
            }
            manager.attachMonitor(monitor);
        }
    }

    @Override
    public void unregisterMonitor(Set<Class> types, EventMonitor monitor) {
        for (Class type : types) {
            EventManager manager = managers.get(type);
            if (manager != null) {
                manager.detachMonitor(monitor);
                if (manager.getSize() == 0) {
                    managers.remove(type);
                }
            }
        }
    }

    @Override
    public Collection<EventMonitor> getMonitors(Class type) {
        EventManager manager = managers.get(type);
        return manager == null ? Collections.EMPTY_SET : manager.getMonitors();
    }

}
