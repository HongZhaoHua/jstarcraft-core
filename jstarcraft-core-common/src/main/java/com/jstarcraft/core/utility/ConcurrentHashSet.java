package com.jstarcraft.core.utility;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 并发哈希集合
 * 
 * <pre>
 * 基于{@link ConcurrentHashMap}
 * </pre>
 * 
 * @author Birdy
 *
 * @param <E>
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> {

    private static final Object object = new Object();

    protected final ConcurrentHashMap<E, Object> map;

    public ConcurrentHashSet() {
        this.map = new ConcurrentHashMap<E, Object>();
    }

    public ConcurrentHashSet(int capacity) {
        this.map = new ConcurrentHashMap<E, Object>(capacity);
    }

    public ConcurrentHashSet(int capacity, float factor) {
        this.map = new ConcurrentHashMap<E, Object>(capacity, factor);
    }

    public ConcurrentHashSet(int capacity, float factor, int concurrencyLevel) {
        this.map = new ConcurrentHashMap<E, Object>(capacity, factor, concurrencyLevel);
    }

    public ConcurrentHashSet(Collection<E> collection) {
        this(collection.size());
        addAll(collection);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean contains(Object element) {
        return map.containsKey(element);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public boolean add(E element) {
        return map.put(element, object) == null;
    }

    @Override
    public boolean remove(Object element) {
        return map.remove(element) != null;
    }

    @Override
    public void clear() {
        map.clear();
    }

}
