package com.jstarcraft.core.event;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EventManager implements Iterable<EventMonitor> {

    private Set<EventMonitor> set;

    private EventMonitor[] list;

    public EventManager() {
        this.set = new HashSet<>();
        this.list = new EventMonitor[0];
    }

    public void attachMonitor(EventMonitor monitor) {
        if (this.set.add(monitor)) {
            this.list = this.set.toArray(new EventMonitor[this.set.size()]);
        }
    }

    public void detachMonitor(EventMonitor monitor) {
        if (this.set.remove(monitor)) {
            this.list = this.set.toArray(new EventMonitor[this.set.size()]);
        }
    }

    public Collection<EventMonitor> getMonitors() {
        return Collections.unmodifiableCollection(this.set);
    }

    public EventMonitor getMonitor(int index) {
        return this.list[index];
    }

    public int getSize() {
        return this.list.length;
    }

    @Override
    public Iterator<EventMonitor> iterator() {
        return new MonitorIterator();
    }

    private class MonitorIterator implements Iterator<EventMonitor> {

        private int cursor = 0;

        private int size = list.length;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public EventMonitor next() {
            return list[cursor++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
