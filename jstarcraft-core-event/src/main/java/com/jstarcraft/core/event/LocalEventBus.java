package com.jstarcraft.core.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LocalEventBus implements EventBus {

    protected static final Logger logger = LoggerFactory.getLogger(LocalEventBus.class);

    protected ConcurrentMap<EventMonitor, Set<Class<?>>> monitor2Topics = new ConcurrentHashMap<>();

    protected ConcurrentMap<Class<?>, Set<EventMonitor>> topic2Monitors = new ConcurrentHashMap<>();

    protected LocalEventBus() {
    }

    @Override
    public synchronized boolean registerMonitor(EventMonitor monitor, Set<Class<?>> topics) {
        if (this.monitor2Topics.putIfAbsent(monitor, new HashSet<>(topics)) == null) {
            for (Class<?> topic : topics) {
                Set<EventMonitor> monitors = this.topic2Monitors.get(topic);
                if (monitors == null) {
                    monitors = new HashSet<>();
                    this.topic2Monitors.put(topic, monitors);
                }
                monitors.add(monitor);
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean unregisterMonitor(EventMonitor monitor) {
        Set<Class<?>> topics = this.monitor2Topics.remove(monitor);
        if (topics != null) {
            for (Class<?> topic : topics) {
                Collection<EventMonitor> monitors = this.topic2Monitors.get(topic);
                if (monitors != null) {
                    monitors.remove(monitor);
                    if (monitors.isEmpty()) {
                        this.topic2Monitors.remove(topic);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized Collection<EventMonitor> getMonitors() {
        return monitor2Topics.keySet();
    }

}
